package pl.kania.warehousemanager.services.beans;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class VectorProvider {

    private String u1;
    private String u2;
    private String server;
    private List<String> ids;

    public VectorProvider(@Autowired Environment environment) {
        u1 = environment.getProperty("android.client.id");
        u2 = environment.getProperty("android.client2.id");
        server = environment.getProperty("server.id");
        ids = Arrays.asList(u1, u2, server);
    }

    public ProductVectorClock newVector(String nonServerNodeName, int initialQuantity) {
        ProductVectorClockNode nodeServer = new ProductVectorClockNode(initialQuantity, server);
        ProductVectorClockNode nodeU1;
        ProductVectorClockNode nodeU2;
        if (nonServerNodeName.equals(u1)) {
            nodeU1 = new ProductVectorClockNode(initialQuantity, u1);
            nodeU2 = new ProductVectorClockNode(u2);
        } else if (nonServerNodeName.equals(u2)) {
            nodeU1 = new ProductVectorClockNode(u1);
            nodeU2 = new ProductVectorClockNode(initialQuantity, u2);
        } else {
            throw new IllegalStateException("Unknown node");
        }
        return new ProductVectorClock(Arrays.asList(nodeU1, nodeU2, nodeServer));
    }

    public ProductVectorClock newVectorCreatedOnServer(int initialQuantity) {
        ProductVectorClockNode nodeServer = new ProductVectorClockNode(initialQuantity, server);
        ProductVectorClockNode nodeU1 = new ProductVectorClockNode(u1);
        ProductVectorClockNode nodeU2 = new ProductVectorClockNode(u2);
        return new ProductVectorClock(Arrays.asList(nodeU1, nodeU2, nodeServer));
    }
}
