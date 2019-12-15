package pl.kania.warehousemanager.services.synchronization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.util.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.ProductMapper;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.DataToSyncOnClient;
import pl.kania.warehousemanager.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanager.model.dto.ProductClient;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.services.beans.VectorProvider;
import pl.kania.warehousemanager.services.dao.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SynchronizationService {

    public static final int NO_GLOBAL_ID = -1;

    @Autowired
    private Environment environment;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMerger productMerger;

    @Autowired
    private VectorProvider vectorProvider;

    public DataToSyncOnClient synchronizeWithDevice(@NonNull DataToSyncOnServer dataToSyncOnServer, @NonNull String clientId) {
        log.info("----------- New request ------------ ");
        final DataToSyncOnClient dataToSyncOnClient = new DataToSyncOnClient();

        final Map<Long, Product> serverProducts = getMapNonRemovedServerProductsPerId();
        final Map<Long, ProductClient> clientProducts = getMapNonRemovedClientProductsPerId(dataToSyncOnServer);
        log.info("Server non-removed products: " + serverProducts.keySet().toString());
        log.info("Client non-removed non-new products: " + clientProducts.keySet().toString());
        log.info("Client new products: " + dataToSyncOnServer.getNewProducts().toString());

        final Map<Long, Product> serverRemovedProducts = getMapRemovedServerProductsPerId();
        final List<Long> clientRemovedProducts = dataToSyncOnServer.getRemovedProductsIds();
        log.info("Server removed products: " + serverRemovedProducts.keySet().toString());
        log.info("Client removed products: " + clientRemovedProducts.toString());

        final Map<Long, Long> savedIdsPerLocalIds = saveNewProductsFromClientOnServer(dataToSyncOnServer.getNewProducts(), clientId);
        dataToSyncOnClient.getSavedProductIdPerLocalId().putAll(savedIdsPerLocalIds);

        final List<ProductClient> newProductsOnClient = getNewProductsToSaveOnClient(serverProducts, clientProducts, clientRemovedProducts, clientId);
        dataToSyncOnClient.getNewProducts().addAll(newProductsOnClient);
        log.info("New products on client: " + newProductsOnClient.toString());

        removeProductsFromServer(serverRemovedProducts, clientRemovedProducts);

        List<Long> productIdsToRemove = getProductIdsToRemoveOnClient(clientProducts, serverRemovedProducts, clientRemovedProducts);
        dataToSyncOnClient.getRemovedIds().addAll(productIdsToRemove);
        log.info("Products to remove on client: " + productIdsToRemove.toString());

        Set<Long> commonProductIds = getProductIdsOnBothServerAndClient(serverProducts, clientProducts);
        final List<ProductClient> updatedProducts = productMerger.mergeProducts(serverProducts, clientProducts, commonProductIds, clientId);
        dataToSyncOnClient.setUpdatedProducts(updatedProducts);

        return dataToSyncOnClient;
    }




    private Set<Long> getProductIdsOnBothServerAndClient(Map<Long, Product> serverProducts, Map<Long, ProductClient> clientProducts) {
        Set<Long> ids = new HashSet<>();
        for (Long id : serverProducts.keySet()) {
            if (clientProducts.containsKey(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    private List<Long> getProductIdsToRemoveOnClient(Map<Long, ProductClient> clientProducts, Map<Long, Product> serverRemovedProducts, List<Long> clientRemovedProducts) {
        List<Long> productIdsToRemove = new ArrayList<>();
        for (Long serverRemovedProductId : serverRemovedProducts.keySet()) {
            if (!clientRemovedProducts.contains(serverRemovedProductId) && clientProducts.containsKey(serverRemovedProductId)) {
                productIdsToRemove.add(serverRemovedProductId);
            }
        }
        return productIdsToRemove;
    }

    private void removeProductsFromServer(Map<Long, Product> serverRemovedProducts, List<Long> clientRemovedProducts) {
        for (Long deviceRemovedProductId : clientRemovedProducts) {
            if (!serverRemovedProducts.containsKey(deviceRemovedProductId) && deviceRemovedProductId != NO_GLOBAL_ID) {
                final Optional<Product> serverProductToRemove = productRepository.findById(deviceRemovedProductId);
                if (serverProductToRemove.isPresent()) {
                    serverProductToRemove.get().setRemoved(true);
                    productRepository.save(serverProductToRemove.get());
                    log.info("Removed product " + serverProductToRemove.get().toString());
                }
            }
        }
    }

    private List<ProductClient> getNewProductsToSaveOnClient(Map<Long, Product> serverProducts, Map<Long, ProductClient> clientProducts, List<Long> clientRemovedProducts, @NonNull String clientId) {
        final List<ProductClient> newProducts = new ArrayList<>();
        for (Long serverProductId : serverProducts.keySet()) {
            if (!clientProducts.containsKey(serverProductId) && !clientRemovedProducts.contains(serverProductId)) {
                try {
                    // FIXME no need to update last product modification timestamp?
                    final Product product = serverProducts.get(serverProductId);
                    final ProductVectorClock vectorClock = product.getVectorClock();
                    vectorClock.copyQuantity(vectorProvider.getServer(), clientId);
                    product.setVectorClock(vectorClock);
                    productRepository.save(product);
                    newProducts.add(ProductMapper.mapServerToClient(product, clientId));
                } catch (JsonProcessingException e) {
                    log.error("An error with vector clock occurred while adding new products to save on client (id " + serverProductId + ")", e);
                }
            }
        }
        return newProducts;
    }

    private Map<Long, Long> saveNewProductsFromClientOnServer(List<ProductClient> newClientProducts, @NonNull String clientId) {
        final Map<Long, Long> savedProductIdPerLocalId = new HashMap<>();
        for (ProductClient product : newClientProducts) {
            try {
                final Product productToSave = ProductMapper.mapClientToServer(product, vectorProvider.newVector(clientId, product.getQuantity()));
                final Product savedProduct = productRepository.save(productToSave);
                savedProductIdPerLocalId.put(product.getLocalId(), savedProduct.getId());
                log.info("Saved " + savedProduct.toString());
            } catch (JsonProcessingException e) {
                log.error("An error with vector clock occurred while saving new products on server (product id " + product.getId() + ")", e);
            }
        }
        return savedProductIdPerLocalId;
    }

    private Map<Long, Product> getMapRemovedServerProductsPerId() {
        return productRepository.findAllByRemoved(true).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    private Map<Long, ProductClient> getMapNonRemovedClientProductsPerId(@NonNull DataToSyncOnServer dataToSyncOnServer) {
        return dataToSyncOnServer.getExistingProducts().stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(ProductClient::getId, p -> p));
    }

    private Map<Long, Product> getMapNonRemovedServerProductsPerId() {
        return Lists.newArrayList(productRepository.findAllByRemoved(false)).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }
}
