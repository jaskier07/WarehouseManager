package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI_PRODUCT;

class TaskDeleteProduct extends AbstractRestTask<Long, Void> {

    private final Runnable onSuccess;
    private final Runnable onFailure;

    TaskDeleteProduct(String token, Runnable onSuccess, Runnable onFailure) {
        super(token);
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        if (longs.length > 0) {
            try {
                final Request request = getRequest(longs[0]);
                final Call call = getClient().newCall(request);
                final Response response = call.execute();
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    onFailure.run();
                    Log.w("delete", "Product deletion did not complete successfully");
                }
            } catch (Exception e) {
                Log.e("delete", "An error occured while deleting product");
            }
        }
        return null;
    }

    private Request getRequest(Long productId) {
        return new Request.Builder()
                .addHeader(AUTH_HEADER, getAuthValue())
                .url(BASE_URI_PRODUCT + "/" + productId + "/deletion")
                .delete()
                .build();
    }
}
