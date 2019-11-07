package pl.kania.warehousemanagerclient;

import java.util.List;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.tasks.TaskGetAllProducts;

public class RestService {

    public static final String BASE_URI = "http://5fa831e1.ngrok.io";

    public void getAllProducts(Consumer<List<Product>> updateProducts) {
        new TaskGetAllProducts(updateProducts).execute();
    }
}
