package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.Product;

import static okhttp3.MultipartBody.FORM;
import static pl.kania.warehousemanagerclient.RestService.BASE_URI;

public class TaskAddProduct extends AbstractRestTask<Product> {

    private final Runnable afterAdd;

    public TaskAddProduct(Runnable afterAdd) {
        this.afterAdd = afterAdd;
    }

    @Override
    protected Void doInBackground(Product... products) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), objectMapper.writeValueAsString(products[0]));
//                    .add("product", objectMapper.writeValueAsString(products[0]))
//                    .build();
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
