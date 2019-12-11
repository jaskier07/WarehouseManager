package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.entities.Product;

class TaskUpdateProduct extends AbstractRestTask<Product, Void> {

    private final Runnable afterUpdate;

    TaskUpdateProduct(String token, Runnable afterUpdate, Context context) {
        super(token, context);
        this.afterUpdate = afterUpdate;
    }

    @Override
    protected Void doInBackground(Product... products) {
        try {
            final Product product = products[0];
            final Response response = executeRequest(getRequest(product));
            if (response.isSuccessful()) {
                afterUpdate.run();
            } else {
                Log.w("update", "Request did not complete successfully");
            }
        } catch (Exception e) {
            Log.e("update", "An error occured while updating product", e);
        }
        return null;
    }

    private Request getRequest(Product product) throws JsonProcessingException {
        final RequestBody requestBody = RequestBody.create(getMediaType(), getObjectMapper().writeValueAsString(product));
        return new Request.Builder()
                .addHeader(AUTH_HEADER, getAuthValue())
                .url(getBaseProductUri() + "/" + product.getId() + "/update")
                .put(requestBody)
                .build();
    }
}
