package pl.kania.warehousemanager.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ProductClient;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMapperTest {

    public static final Timestamp DATE_LAST_MODIFIED = Timestamp.valueOf(LocalDateTime.of(2020, Month.MARCH, 8, 12, 20));

    @Test
    void givenProductClientAndProductVectorClockCreateProductWithFilledValues() throws JsonProcessingException {
        // TODO create product client and vector clock factory

        ProductClient productClient = new ProductClient();
        productClient.setId(1L);
        productClient.setLocalId(3L);
        productClient.setManufacturerName("Samsung");
        productClient.setModelName("Galaxy S8");
        productClient.setPrice(1999.99);
        productClient.setQuantity(2);
        productClient.setRemoved(true);
        productClient.setLastModified(DATE_LAST_MODIFIED);

        ProductVectorClockNode node1 = new ProductVectorClockNode(2, "Galaxy S8");
        ProductVectorClockNode node2 = new ProductVectorClockNode(1, "Galaxy S8");
        ProductVectorClock vectorClock = new ProductVectorClock(Arrays.asList(node1, node2));

        Product product = ProductMapper.mapClientToServer(productClient, vectorClock);

        assertEquals(productClient.getId(), product.getId(), "Products' ids should be equal");
        assertEquals(productClient.getManufacturerName(), product.getManufacturerName(), "Products' manufacturer should be equal");
        assertEquals(productClient.getModelName(), product.getModelName(), "Products' model names should be equal");
        assertEquals(productClient.getPrice(), product.getPrice(), "Products' prices should be equal");
        assertEquals(productClient.isRemoved(), product.isRemoved(), "Products' 'removed' flags should be equal");
        assertEquals(productClient.getLastModified(), product.getLastModified(), "Products' last modification dates should be equal");
    }

}