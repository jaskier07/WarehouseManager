package pl.kania.warehousemanager.services.synchronization;

import com.google.api.client.util.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.DataToSyncOnDevice;
import pl.kania.warehousemanager.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanager.services.dao.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SynchronizationService {

    @Autowired
    private Environment environment;

    @Autowired
    private ProductRepository productRepository;

    public DataToSyncOnDevice synchronizeWithDevice(@NonNull DataToSyncOnServer dataToSyncOnServer, @NonNull String clientId)  {
        final DataToSyncOnDevice dataToSyncOnClient = new DataToSyncOnDevice();

        final Map<Long, Product> serverProducts = getMapNonRemovedServerProductsPerId();
        final Map<Long, Product> clientProducts = getMapNonRemovedClientProductsPerId(dataToSyncOnServer);
        log.info("Server non-removed products: " + serverProducts.keySet().toString());
        log.info("Client non-removed products: " + clientProducts.keySet().toString());

        final Map<Long, Product> serverRemovedProducts = getMapRemovedServerProductsPerId();
        final List<Long> clientRemovedProducts = dataToSyncOnServer.getRemovedProductsIds();
        log.info("Server removed products: " + serverRemovedProducts.keySet().toString());
        log.info("Client removed products: " + clientRemovedProducts.toString());

        saveNewProductsFromClientOnServer(dataToSyncOnServer, dataToSyncOnClient);

        final List<Product> newProductsOnClient = getNewProductsToSaveOnClient(serverProducts, clientProducts, clientRemovedProducts);
        dataToSyncOnClient.getNewProducts().addAll(newProductsOnClient);
        log.info("New products on client: " + newProductsOnClient.toString());

        removeProductsFromServer(serverRemovedProducts, clientRemovedProducts);

        List<Long> productIdsToRemove = getProductIdsToRemoveOnClient(serverRemovedProducts, clientRemovedProducts);
        dataToSyncOnClient.getRemovedIds().addAll(productIdsToRemove);
        log.info("Products to remove on client: " + productIdsToRemove.toString());


//        // sync products
//        for (Product clientProduct : dataToSyncOnServer.getProducts()) {
//            final ProductVectorClock clientProductVector = clientProduct.getVectorClock();
//            vectorClockService.getDifferentNodeNames(clientProductVector, serverProducts)
//        }
// TODO analogicznie zrobic na androidzie i przetestowac dodawanie i usuwanie

         return dataToSyncOnClient;
    }

    private List<Long> getProductIdsToRemoveOnClient(Map<Long, Product> serverRemovedProducts, List<Long> clientRemovedProducts) {
        List<Long> productIdsToRemove = new ArrayList<>();
        for (Long serverRemovedProductId : serverRemovedProducts.keySet()) {
            if (!clientRemovedProducts.contains(serverRemovedProductId)) {
                productIdsToRemove.add(serverRemovedProductId);
            }
        }
        return productIdsToRemove;
    }

    private void removeProductsFromServer(Map<Long, Product> serverRemovedProducts, List<Long> clientRemovedProducts) {
        for (Long deviceRemovedProductId : clientRemovedProducts) {
            if (!serverRemovedProducts.containsKey(deviceRemovedProductId)) {
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
        List<Product> newProducts = new ArrayList<>();
        for (Long serverProductId : serverProducts.keySet()) {
            if (!clientProducts.containsKey(serverProductId) && !clientRemovedProducts.contains(serverProductId)) {
                final Product product = serverProducts.get(serverProductId);
//                product.getVectorClock().getNode(environment.getProperty("server.id")).incrementVersion();
                productRepository.save(product);
                newProducts.add(product);
            }
        }
        return newProducts;
    }

    private void saveNewProductsFromClientOnServer(@NonNull DataToSyncOnServer dataToSyncOnServer, DataToSyncOnDevice dataToSyncOnClient) {
        final List<Product> newClientProducts = dataToSyncOnServer.getProducts().stream()
                .filter(p -> p.getId() == null)
                .collect(Collectors.toList());
        for (Product product : newClientProducts) {
//            product.getVectorClock().getNode(environment.getProperty("server.id")).incrementVersion(); // TODO sprawdzic czy w bazie są zinkrementowane
            final Product savedProduct = productRepository.save(product);
            dataToSyncOnClient.getUpdatedProducts().add(savedProduct);
            log.info("Saved " + savedProduct.toString());
        }
    }

    private Map<Long, Product> getMapRemovedServerProductsPerId() {
        return productRepository.findAllByRemoved(true).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    private Map<Long, Product> getMapNonRemovedClientProductsPerId(@NonNull DataToSyncOnServer dataToSyncOnServer) {
        return dataToSyncOnServer.getProducts().stream()
                .filter(p -> p.getId() != null && p.getId() != -1)
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    private Map<Long, Product> getMapNonRemovedServerProductsPerId() {
        return Lists.newArrayList(productRepository.findAllByRemoved(false)).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }
}
