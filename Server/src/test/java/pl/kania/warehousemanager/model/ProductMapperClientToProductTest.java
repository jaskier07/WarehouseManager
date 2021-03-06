package pl.kania.warehousemanager.model;

import lombok.SneakyThrows;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ProductClient;

class ProductMapperClientToProductTest implements ComparableProductTest<ProductClient, Product> {

    @Override
    public ProductClient createBaseProduct() {
        return TestProductFactory.newProductClient();
    }

    @SneakyThrows
    @Override
    public Product createNewProductAndFillItWithValuesFromBaseProduct(ProductClient baseProduct) {
        return ProductMapper.mapClientToServer(baseProduct, TestProductFactory.newProductVectorClock());
    }
}