package pl.kania.warehousemanager.model.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import pl.kania.warehousemanager.stereotype.CsvToProduct;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    @DisplayName("Test if product after cloning is identical")
    @ParameterizedTest(name = "Product: {0}")
    @CsvFileSource(resources = "/test/test-products.csv")
    void testCloneProduct(@CsvToProduct Product product) throws CloneNotSupportedException {
        Product cloned = product.clone();

        assertAll(
                () -> assertEquals(product.getId(), cloned.getId()),
                () -> assertEquals(product.getLastModified(), cloned.getLastModified()),
                () -> assertEquals(product.getManufacturerName(), cloned.getManufacturerName()),
                () -> assertEquals(product.getModelName(), cloned.getModelName()),
                () -> assertEquals(product.isRemoved(), cloned.isRemoved())
        );
    }
}