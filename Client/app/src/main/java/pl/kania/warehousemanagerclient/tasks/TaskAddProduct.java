package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.Product;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI;
import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI_PRODUCT;

class TaskAddProduct extends AbstractRestTask<Product> {

    private final Runnable afterAdd;

    TaskAddProduct(Runnable afterAdd) {
        this.afterAdd = afterAdd;
    }

    @Override
    protected Void doInBackground(Product... products) {
        if (products.length > 0) {
            try {
                final Request request = getRequest(products[0]);
                final Call call = getClient().newCall(request);
                final Response response = call.execute();
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
                .post(requestBody)
                .build();
    }
}
