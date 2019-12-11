package pl.kania.warehousemanagerclient.services.tasks;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.kania.warehousemanagerclient.model.entities.Product;

import static pl.kania.warehousemanagerclient.services.tasks.RestService.BASE_URI;


class TaskGetAllProducts extends AbstractRestTask<Void, List<Product>> {
    private final Consumer<List<Product>> updateProducts;

    TaskGetAllProducts(String token, Consumer<List<Product>> updateProducts) {
        super(token);
        this.updateProducts = updateProducts;
    }

    @Override
    protected List<Product> doInBackground(Void... voids) {
        try {
            final Response response = executeRequest(getRequest());
            final ResponseBody responseBody = response.body();

            if (response.isSuccessful() && responseBody != null) {
                return Arrays.asList(getObjectMapper().readValue(responseBody.string(), Product[].class));
            } else {
                Log.w("getAllProducts", "Error code != 200 or empty response body");
            }
        } catch (Exception e) {
            Log.e("getAllProducts", "An error occured while getting all products", e);
        }
        return null;
    }

    private Request getRequest() {
        return new Request.Builder()
                .url(BASE_URI + "/products")
                .addHeader(AUTH_HEADER, getAuthValue())
                .build();
    }

    @Override
    protected void onPostExecute(List<Product> products) {
        updateProducts.accept(products);
    }
}
