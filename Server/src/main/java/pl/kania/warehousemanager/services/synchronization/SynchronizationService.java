package pl.kania.warehousemanager.services.synchronization;

import com.google.api.client.util.Lists;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.DataToSyncOnDevice;
import pl.kania.warehousemanager.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanager.services.dao.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SynchronizationService {

    @Autowired
    private Environment environment;

    @Autowired
    private ProductRepository productRepository;

//    @Autowired
//    private VectorClockService vectorClockService;

    public DataToSyncOnDevice synchronizeWithDevice(@NonNull DataToSyncOnServer dataToSyncOnServer, @NonNull String clientId)  {
        final DataToSyncOnDevice dataToSyncOnDevice = new DataToSyncOnDevice();

        final Map<Long, Product> serverProducts = Lists.newArrayList(productRepository.findAllByRemoved(false)).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        final Map<Long, Product> deviceProducts = dataToSyncOnServer.getProducts().stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Product::getId, p -> p));

        final Map<Long, Product> serverRemovedProducts = productRepository.findAllByRemoved(true).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        final List<Long> deviceRemovedProducts = dataToSyncOnServer.getRemovedProductsIds();


        // save new products from device on server
        final List<Product> newDeviceProducts = dataToSyncOnServer.getProducts().stream()
                .filter(p -> p.getId() == null)
                .collect(Collectors.toList());
        for (Product product : newDeviceProducts) {
//            product.getVectorClock().getNode(environment.getProperty("server.id")).incrementVersion(); // TODO sprawdzic czy w bazie są zinkrementowane
            dataToSyncOnDevice.getUpdatedProducts().add(productRepository.save(product));
        }

        // send new products from server to device
        for (Long serverProductId : serverProducts.keySet()) {
            if (!deviceProducts.containsKey(serverProductId) && !deviceRemovedProducts.contains(serverProductId)) {
                final Product product = serverProducts.get(serverProductId);
//                product.getVectorClock().getNode(environment.getProperty("server.id")).incrementVersion();
                productRepository.save(product);
                dataToSyncOnDevice.getNewProducts().add(product);
            }
        }
        // remove products from server
        for (Long deviceRemovedProductId : deviceRemovedProducts) {
            if (!serverRemovedProducts.containsKey(deviceRemovedProductId)) {
                final Optional<Product> serverProductToRemove = productRepository.findById(deviceRemovedProductId);
                if (serverProductToRemove.isPresent()) {
                    serverProductToRemove.get().setRemoved(true);
                    productRepository.save(serverProductToRemove.get());
                }
            }
        }

        // remove products on device
        for (Long serverRemovedProductId : serverRemovedProducts.keySet()) {
            if (!deviceRemovedProducts.contains(serverRemovedProductId)) {
                dataToSyncOnDevice.getRemovedIds().add(serverRemovedProductId);
            }
        }


//        // sync products
//        for (Product clientProduct : dataToSyncOnServer.getProducts()) {
//            final ProductVectorClock clientProductVector = clientProduct.getVectorClock();
//            vectorClockService.getDifferentNodeNames(clientProductVector, serverProducts)
//        }
// TODO analogicznie zrobic na androidzie i przetestowac dodawanie i usuwanie

         return dataToSyncOnDevice;
    }
}
