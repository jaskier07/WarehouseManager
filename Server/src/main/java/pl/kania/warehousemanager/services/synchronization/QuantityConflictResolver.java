package pl.kania.warehousemanager.services.synchronization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.services.beans.VectorProvider;

@Service
public class QuantityConflictResolver {

    @Autowired
    private VectorProvider vectorProvider;

    public ProductVectorClock getProductVectorClock(ProductVectorClock productVectorClock, int newClientQuantity, String clientId) {
        final int oldClientQuantity = productVectorClock.getNode(clientId).getQuantity();
        final int serverQuantity = productVectorClock.getNode(vectorProvider.getServer()).getQuantity();

        if (oldClientQuantity == newClientQuantity) {
            productVectorClock.copyQuantity(vectorProvider.getServer(), clientId);
            return productVectorClock;
        }
        int differenceInClientQuantity = newClientQuantity - oldClientQuantity;
        int newServerQuantity = serverQuantity + differenceInClientQuantity;
        productVectorClock.getNode(vectorProvider.getServer()).setQuantity(newServerQuantity);
        productVectorClock.getNode(clientId).setQuantity(newServerQuantity);
        return productVectorClock;
    }
}
