package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.entities.Product;



class TaskAddProduct extends AbstractRestTask<Product, Void> {

    private final Runnable afterAdd;

    TaskAddProduct(String token, Runnable afterAdd, Context context) {
        super(token, context);
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
                .url(getBaseProductUri())
                .addHeader(AUTH_HEADER, getAuthValue())
                .post(requestBody)
                .build();
    }
}
