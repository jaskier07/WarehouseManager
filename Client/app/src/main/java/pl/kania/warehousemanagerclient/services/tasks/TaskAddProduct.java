package pl.kania.warehousemanagerclient.services.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.entities.Product;

import static pl.kania.warehousemanagerclient.services.tasks.RestService.BASE_URI_PRODUCT;

class TaskAddProduct extends AbstractRestTask<Product, Void> {

    private final Runnable afterAdd;

    TaskAddProduct(String token, Runnable afterAdd) {
        super(token);
        this.afterAdd = afterAdd;
    }

    @Override
    protected Void doInBackground(Product... products) {
        if (products.length > 0) {
            try {
                final Response response = executeRequest(getRequest(products[0]));
                if (response.isSuccessful()) {
                    afterAdd.run();
                } else {
                    Log.w("addProduct", "Request did not complete successfully");
                }
            } catch (Exception e) {
                Log.e("addProduct", "An error occured while adding new product", e);
            }
        }
        return null;
    }

    private Request getRequest(Product product) throws JsonProcessingException {
        final RequestBody requestBody = RequestBody.create(getMediaType(), getObjectMapper().writeValueAsString(product));
        return new Request.Builder()
                .url(BASE_URI_PRODUCT)
                .addHeader(AUTH_HEADER, getAuthValue())
                .post(requestBody)
                .build();
    }
}
