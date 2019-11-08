package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.Product;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI;

class TaskAddProduct extends AbstractRestTask<Product> {

    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private final Runnable afterAdd;

    TaskAddProduct(Runnable afterAdd) {
        this.afterAdd = afterAdd;
    }

    @Override
    protected Void doInBackground(Product... products) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), objectMapper.writeValueAsString(products[0]));
            final Request request = new Request.Builder()
                    .url(BASE_URI + "/product")
                    .post(requestBody)
                    .build();
            final Call call = getClient().newCall(request);
            final Response response = call.execute();
            if (response.isSuccessful()) {
                afterAdd.run();
            } else {
                Log.e("addProduct", "Response is not successful");
            }
        } catch (Exception e) {
            Log.e("addProduct", "An error occured while adding new product", e);
        }
        return null;
    }
}
