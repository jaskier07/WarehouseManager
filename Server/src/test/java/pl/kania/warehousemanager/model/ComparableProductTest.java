package pl.kania.warehousemanager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface ComparableProductTest<T extends TransferableProduct, U extends TransferableProduct> {

    T createBaseProduct();

    U createNewProductAndFillItWithValuesFromBaseProduct(T baseProduct);

    @DisplayName("Check if product created by ProductMapper returns new product with identical field values")
    @Test
    default void givenBaseProductCreateNewProductAndFillItWithValuesFromBaseProductThenExpectSameValues() {
        T product1 = createBaseProduct();
        U product2 = createNewProductAndFillItWithValuesFromBaseProduct(product1);

        assertAll(
                () -> assertEquals(product1.getId(), product2.getId(), "Products' ids should be equal"),
                () -> assertEquals(product1.getManufacturerName(), product2.getManufacturerName(), "Products' manufacturer should be equal"),
                () -> assertEquals(product1.getModelName(), product2.getModelName(), "Products' model names should be equal"),
                () -> assertEquals(product1.getPrice(), product2.getPrice(), "Products' prices should be equal"),
                () -> assertEquals(product1.isRemoved(), product2.isRemoved(), "Products' 'removed' flags should be equal"),
                () -> assertEquals(product1.getLastModified(), product2.getLastModified(), "Products' last modification dates should be equal")
        );
    }


}
