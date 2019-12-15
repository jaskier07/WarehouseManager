package pl.kania.warehousemanager.services.synchronization;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.ProductMapper;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ProductClient;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;
import pl.kania.warehousemanager.services.beans.VectorProvider;
import pl.kania.warehousemanager.services.dao.ProductRepository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ProductMerger {

    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("hh:mm:ss dd-MM-yy");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private QuantityConflictResolver quantityConflictResolver;

    @Autowired
    private VectorProvider vectorProvider;

    public List<ProductClient> mergeProducts(Map<Long, Product> serverProducts, Map<Long, ProductClient> clientProducts, Set<Long> commonProductIds, String clientId) {
        final List<ProductClient> productsToUpdateOnClient = new ArrayList<>();
        for (Long id : commonProductIds) {
            final Product serverProduct = serverProducts.get(id);
            final ProductClient clientProduct = clientProducts.get(id);
            try {
                if (!serverProduct.getLastModified().equals(clientProduct.getLastModified())) {
                    final ProductVectorClock vector = quantityConflictResolver.getProductVectorClock(serverProduct.getVectorClock(), clientProduct.getQuantity(), clientId);
                    log.info("New vector clock for product (id " + id + ") " + vector.toString());
                    try {
                        if (serverProduct.getLastModified().before(clientProduct.getLastModified())) {
                            overwriteServerProduct(id, serverProduct, clientProduct, vector);
                            clientProduct.setQuantity(vector.getNode(clientId).getQuantity());
                            productsToUpdateOnClient.add(clientProduct);
                        } else {
                            serverProduct.setVectorClock(vector);
                            final ProductClient overwrittenProduct = overwriteClientProduct(id, serverProduct, clientProduct, clientId);
                            productsToUpdateOnClient.add(overwrittenProduct);
                            productRepository.save(serverProduct);
                        }
                    } catch (Exception e) {
                        log.error("An error occurred while merging product (id " + id + ")", e);
                    }
                } else {
                    final ProductVectorClockNode serverNode = serverProduct.getVectorClock().getNode(vectorProvider.getServer());
                    if (!clientProduct.getQuantity().equals(serverNode.getQuantity())) {
                        overrideClientQuantityWhenLastModifiedDateEqual(clientId, productsToUpdateOnClient, id, serverProduct, clientProduct, serverProduct.getVectorClock());
                    }
                }

            } catch (JsonProcessingException e) {
                log.error("An error with vector clock occurred while merging product versions from client and id (id " + id + ")", e);
            }
        }
        return productsToUpdateOnClient;
    }

    private void overrideClientQuantityWhenLastModifiedDateEqual(String clientId, List<ProductClient> productsToUpdateOnClient, Long id, Product serverProduct, ProductClient clientProduct, ProductVectorClock vector) throws JsonProcessingException {
        vector.copyQuantity(vectorProvider.getServer(), clientId);
        serverProduct.setVectorClock(vector);
        productRepository.save(serverProduct);
        clientProduct.setQuantity(vector.getNode(clientId).getQuantity());
        productsToUpdateOnClient.add(clientProduct);
        log.info("Client's quantity different from server's quantity, client's quantity set to " + clientProduct.getQuantity() +" (id " + id + ")");
    }

    private ProductClient overwriteClientProduct(Long id, Product serverProduct, ProductClient clientProduct, String clientId) throws CloneNotSupportedException, JsonProcessingException {
        final ProductClient updatedClientProduct = ProductMapper.mapServerToClient(serverProduct.clone(), clientId);
        updatedClientProduct.setLocalId(clientProduct.getLocalId());
        log.info("Client: product to update (id " + id + ") [server: " + formatTimestamp(serverProduct.getLastModified()) + "][client: " + formatTimestamp(clientProduct.getLastModified()) + "]");
        return updatedClientProduct;
    }

    private void overwriteServerProduct(Long id, Product serverProduct, ProductClient clientProduct, ProductVectorClock vectorClock) throws JsonProcessingException {
        productRepository.save(ProductMapper.mapClientToServer(clientProduct, vectorClock));
        log.info("Server: updated product (id " + id + ") [server: " + formatTimestamp(serverProduct.getLastModified()) + "][client: " + formatTimestamp(clientProduct.getLastModified()) + "]");
    }

    private String formatTimestamp(Timestamp lastModified) {
        return dateTimeFormatter.format(lastModified);
    }
}
