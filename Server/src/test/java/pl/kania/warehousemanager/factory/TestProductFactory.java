package pl.kania.warehousemanager.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ProductClient;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

public class TestProductFactory {

    private static final Timestamp DATE_LAST_MODIFIED = Timestamp.valueOf(LocalDateTime.of(2020, Month.MARCH, 8, 12, 20));
    private static final long ID = 1L;
    private static final long LOCAL_ID = 3L;
    private static final String MANUFACTURER_NAME = "Samsung";
    private static final String MODEL_NAME = "Galaxy S8";
    private static final double PRICE = 1999.99;
    private static final int QUANTITY = 2;
    private static final boolean REMOVED = true;

    public static ProductClient newProductClient() {
        ProductClient productClient = new ProductClient();
        productClient.setId(ID);
        productClient.setLocalId(LOCAL_ID);
        productClient.setManufacturerName(MANUFACTURER_NAME);
        productClient.setModelName(MODEL_NAME);
        productClient.setPrice(PRICE);
        productClient.setQuantity(QUANTITY);
        productClient.setRemoved(REMOVED);
        productClient.setLastModified(DATE_LAST_MODIFIED);
        return productClient;
    }

    public static Product newProduct() throws JsonProcessingException {
        Product product = new Product();
        product.setId(ID);
        product.setRemoved(REMOVED);
        product.setPrice(PRICE);
        product.setModelName(MODEL_NAME);
        product.setManufacturerName(MANUFACTURER_NAME);
        product.setVectorClock(newProductVectorClock());
        return product;
    }

    public static ProductVectorClock newProductVectorClock() {
        ProductVectorClockNode node1 = new ProductVectorClockNode(2, "android-app");
        ProductVectorClockNode node2 = new ProductVectorClockNode(1, "ios-app");
        return new ProductVectorClock(Arrays.asList(node1, node2));
    }
}
