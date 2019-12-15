package pl.kania.warehousemanager.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ProductClient;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static Product mapClientToServer(final ProductClient client, final ProductVectorClock vectorClock) throws JsonProcessingException {
        final Product server = new Product();
        server.setLastModified(client.getLastModified());
        server.setManufacturerName(client.getManufacturerName());
        server.setModelName(client.getModelName());
        server.setPrice(client.getPrice());
        server.setVectorClock(vectorClock);
        server.setRemoved(client.isRemoved());
        server.setId(client.getId());
        return server;
    }

    public static ProductClient mapServerToClient(final Product server, String clientId) throws JsonProcessingException {
        final ProductClient client = new ProductClient();
        client.setLastModified(server.getLastModified());
        client.setManufacturerName(server.getManufacturerName());
        client.setModelName(server.getModelName());
        client.setPrice(server.getPrice());
        client.setQuantity(server.getVectorClock().getNode(clientId).getQuantity());
        client.setRemoved(server.isRemoved());
        client.setId(server.getId());
        client.setLocalId(-1L);
        return client;
    }
}
