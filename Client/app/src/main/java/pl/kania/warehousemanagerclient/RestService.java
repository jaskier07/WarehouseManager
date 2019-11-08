package pl.kania.warehousemanagerclient;

import java.util.List;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.tasks.TaskAddProduct;
import pl.kania.warehousemanagerclient.tasks.TaskGetAllProducts;

public class RestService {

    public static final String BASE_URI = "http://b75b818d.ngrok.io";

    public void getAllProducts(Consumer<List<Product>> updateProducts) {
        new TaskGetAllProducts(updateProducts).execute();
    }

    public void addNewProduct(Product product, Runnable afterAdd) {
        new TaskAddProduct(afterAdd).execute(product);
    }
}
