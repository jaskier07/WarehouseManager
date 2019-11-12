package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.Product;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI_PRODUCT;

class TaskUpdateProduct extends AbstractRestTask<Product, Void> {

    private final Runnable afterUpdate;

    TaskUpdateProduct(Runnable afterUpdate) {
        this.afterUpdate = afterUpdate;
    }

    @Override
    protected Void doInBackground(Product... products) {
        if (products.length > 0) {
            try {
                final Product product = products[0];
                final Request request = getRequest(product);
                final Call call = getClient().newCall(request);
                final Response response = call.execute();
                if (response.isSuccessful()) {
                    afterUpdate.run();
                } else {
                    Log.w("update", "Request did not complete successfully");
                }
            } catch (Exception e) {
                Log.e("update", "An error occured while updating product", e);
            }
        }
        return null;
    }

    private Request getRequest(Product product) throws JsonProcessingException {
        final RequestBody requestBody = RequestBody.create(getMediaType(), getObjectMapper().writeValueAsString(product));
        return new Request.Builder()
                .url(BASE_URI_PRODUCT + "/" + product.getId() + "/update")
                .put(requestBody)
                .build();
    }
}
