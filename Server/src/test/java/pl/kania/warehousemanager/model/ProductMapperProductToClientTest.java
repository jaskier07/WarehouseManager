package pl.kania.warehousemanager.model;

import lombok.SneakyThrows;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ProductClient;

public class ProductMapperProductToClientTest implements ComparableProduct<Product, ProductClient> {

    @SneakyThrows
    public Product createBaseProduct() {
        return TestProductFactory.newProduct();
    }

    @SneakyThrows
    @Override
    public ProductClient createNewProductAndFillItWithValuesFromBaseProduct(Product baseProduct) {
        return ProductMapper.mapServerToClient(baseProduct, "android-app");
    }
}
