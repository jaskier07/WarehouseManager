package pl.kania.warehousemanager.services.synchronization;

import com.google.api.client.util.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.DataToSyncOnClient;
import pl.kania.warehousemanager.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanager.services.dao.ProductRepository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("hh:mm:ss dd-MM-yy");

    @Autowired
    private Environment environment;

    @Autowired
    private ProductRepository productRepository;

    public DataToSyncOnClient synchronizeWithDevice(@NonNull DataToSyncOnServer dataToSyncOnServer, @NonNull String clientId)  {
        log.info("----------- New request ------------ ");
        final DataToSyncOnClient dataToSyncOnClient = new DataToSyncOnClient();

        final Map<Long, Product> serverProducts = getMapNonRemovedServerProductsPerId();
        final Map<Long, Product> clientProducts = getMapNonRemovedClientProductsPerId(dataToSyncOnServer);
        log.info("Server non-removed products: " + serverProducts.keySet().toString());
        log.info("Client non-removed non-new products: " + clientProducts.keySet().toString());
        log.info("Client new products: " + dataToSyncOnServer.getNewProducts().toString());

        final Map<Long, Product> serverRemovedProducts = getMapRemovedServerProductsPerId();
        final List<Long> clientRemovedProducts = dataToSyncOnServer.getRemovedProductsIds();
        log.info("Server removed products: " + serverRemovedProducts.keySet().toString());
        log.info("Client removed products: " + clientRemovedProducts.toString());

        final Map<Long, Long> savedIdsPerLocalIds = saveNewProductsFromClientOnServer(dataToSyncOnServer.getNewProducts());
        dataToSyncOnClient.getSavedProductIdPerLocalId().putAll(savedIdsPerLocalIds);

        final List<Product> newProductsOnClient = getNewProductsToSaveOnClient(serverProducts, clientProducts, clientRemovedProducts);
        dataToSyncOnClient.getNewProducts().addAll(newProductsOnClient);
        log.info("New products on client: " + newProductsOnClient.toString());

        removeProductsFromServer(serverRemovedProducts, clientRemovedProducts);

        List<Long> productIdsToRemove = getProductIdsToRemoveOnClient(clientProducts, serverRemovedProducts, clientRemovedProducts);
        dataToSyncOnClient.getRemovedIds().addAll(productIdsToRemove);
        log.info("Products to remove on client: " + productIdsToRemove.toString());

        Set<Long> commonProductIds = getProductIdsOnBothServerAndClient(serverProducts, clientProducts);
        final List<Product> updatedProducts = mergeProducts(serverProducts, clientProducts, commonProductIds);
        dataToSyncOnClient.setUpdatedProducts(updatedProducts);

//        // sync products
//        for (Product clientProduct : dataToSyncOnServer.getProducts()) {
//            final ProductVectorClock clientProductVector = clientProduct.getVectorClock();
//            vectorClockService.getDifferentNodeNames(clientProductVector, serverProducts)
//        }
// TODO analogicznie zrobic na androidzie i przetestowac dodawanie i usuwanie

         return dataToSyncOnClient;
    }

    private List<Product> mergeProducts(Map<Long, Product> serverProducts, Map<Long, Product> clientProducts, Set<Long> commonProductIds) {
        final List<Product> productsToUpdateOnClient = new ArrayList<>();
        for (Long id : commonProductIds) {
            final Product serverProduct = serverProducts.get(id);
            final Product clientProduct = clientProducts.get(id);
            if (!serverProduct.getLastModified().equals(clientProduct.getLastModified())) {
                try {
                    if (serverProduct.getLastModified().before(clientProduct.getLastModified())) {
                        // TODO zmiana quantiyt
                        productRepository.save(clientProduct);
                        log.info("Server: updated product (id " + id + ") [server: " + formatTimestamp(serverProduct.getLastModified()) + "][client: " + formatTimestamp(clientProduct.getLastModified()) + "]");
                    } else {
                        final Product updatedClientProduct = serverProduct.clone();
                        // TODO zmiana quantity
                        updatedClientProduct.setLocalId(clientProduct.getLocalId());
                        productsToUpdateOnClient.add(updatedClientProduct);
                        log.info("Client: product to update (id " + id + ") [server: " + formatTimestamp(serverProduct.getLastModified()) + "][client: " + formatTimestamp(clientProduct.getLastModified()) + "]");
                    }
                } catch (Exception e) {
                    log.error("An error occured while merging product (id " + id + ")", e);
                }
            }
        }
        return productsToUpdateOnClient;
    }

    private String formatTimestamp(Timestamp lastModified) {
        return dateTimeFormatter.format(lastModified);
    }

    private Set<Long> getProductIdsOnBothServerAndClient(Map<Long, Product> serverProducts, Map<Long, Product> clientProducts) {
        Set<Long> ids = new HashSet<>();
        for (Long id : serverProducts.keySet()) {
            if (clientProducts.containsKey(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    private List<Long> getProductIdsToRemoveOnClient(Map<Long, Product> clientProducts, Map<Long, Product> serverRemovedProducts, List<Long> clientRemovedProducts) {
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

    private List<Product> getNewProductsToSaveOnClient(Map<Long, Product> serverProducts, Map<Long, Product> clientProducts, List<Long> clientRemovedProducts) {
        final List<Product> newProducts = new ArrayList<>();
        for (Long serverProductId : serverProducts.keySet()) {
            if (!clientProducts.containsKey(serverProductId) && !clientRemovedProducts.contains(serverProductId)) {
                final Product product = serverProducts.get(serverProductId);
//                product.getVectorClock().getNode(environment.getProperty("server.id")).incrementVersion();
//                productRepository.save(product);
                newProducts.add(product);
            }
        }
        return newProducts;
    }

    private Map<Long, Long> saveNewProductsFromClientOnServer(List<Product> newClientProducts) {
        final Map<Long, Long> savedProductIdPerLocalId = new HashMap<>();
        for (Product product : newClientProducts) {
//            product.getVectorClock().getNode(environment.getProperty("server.id")).incrementVersion(); // TODO sprawdzic czy w bazie są zinkrementowane
            final Product savedProduct = productRepository.save(product);
            savedProductIdPerLocalId.put(product.getLocalId(), savedProduct.getId());
            log.info("Saved " + savedProduct.toString());
        }
        return savedProductIdPerLocalId;
    }

    private Map<Long, Product> getMapRemovedServerProductsPerId() {
        return productRepository.findAllByRemoved(true).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    private Map<Long, Product> getMapNonRemovedClientProductsPerId(@NonNull DataToSyncOnServer dataToSyncOnServer) {
        return dataToSyncOnServer.getExistingProducts().stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    private Map<Long, Product> getMapNonRemovedServerProductsPerId() {
        return Lists.newArrayList(productRepository.findAllByRemoved(false)).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }
}
