package pl.kania.warehousemanagerclient.tasks;

import java.util.List;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.Product;
import pl.kania.warehousemanagerclient.ProductQuantity;
import pl.kania.warehousemanagerclient.tasks.TaskAddProduct;
import pl.kania.warehousemanagerclient.tasks.TaskDecreseProductQuantity;
import pl.kania.warehousemanagerclient.tasks.TaskDeleteProduct;
import pl.kania.warehousemanagerclient.tasks.TaskGetAllProducts;
import pl.kania.warehousemanagerclient.tasks.TaskIncreaseProductQuantity;

public class RestService {

    public static final String BASE_URI = "http://b75b818d.ngrok.io";

    public void getAllProducts(Consumer<List<Product>> updateProducts) {
        new TaskGetAllProducts(updateProducts).execute();
    }

    public void addNewProduct(Product product, Runnable afterAdd) {
        new TaskAddProduct(afterAdd).execute(product);
    }

    public void deleteProduct(Long productId, Runnable afterDelete) {
        new TaskDeleteProduct(afterDelete).execute(productId);
    }

    public void decreaseProductQuantity(ProductQuantity productQuantity, Runnable afterDecrease) {
        new TaskDecreseProductQuantity(afterDecrease).execute(productQuantity);
    }

    public void increaseProductQuantity(ProductQuantity productQuantity, Runnable afterIncrease) {
        new TaskIncreaseProductQuantity(afterIncrease).execute(productQuantity);
    }

    public void updateProduct(Product product, Runnable afterUpdate) {
        new TaskUpdateProduct(afterUpdate).doInBackground(product);
    }
}
