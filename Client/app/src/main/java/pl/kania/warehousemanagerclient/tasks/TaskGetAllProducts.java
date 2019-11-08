package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.Product;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI;


class TaskGetAllProducts extends AbstractRestTask<Void> {
    private final Consumer<List<Product>> updateProducts;

    TaskGetAllProducts(Consumer<List<Product>> updateProducts) {
        this.updateProducts = updateProducts;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            final Request request = new Request.Builder()
                    .url(BASE_URI + "/products")
                    .build();
            final Call call = getClient().newCall(request);
            final Response response = call.execute();

            if (response.isSuccessful()) {
                Product[] products = getObjectMapper().readValue(response.body().string(), Product[].class);
                updateProducts.accept(Arrays.asList(products));
            } else {
                Log.w("getAllProducts", "Error code != 200 or empty response body");
            }
        } catch (Exception e) {
            Log.e("getAllProducts", "An error occured while getting all products", e);
        }
        return null;
    }
}
