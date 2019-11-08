package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI;
import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI_PRODUCT;

class TaskDeleteProduct extends AbstractRestTask<Long> {

    private final Runnable afterDelete;

    TaskDeleteProduct(Runnable afterDelete) {
        this.afterDelete = afterDelete;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        if (longs.length > 0) {
            try {
                final Request request = getRequest(longs[0]);
                final Call call = getClient().newCall(request);
                final Response response = call.execute();
                if (response.isSuccessful()) {
                    afterDelete.run();
                } else {
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
                .url(BASE_URI_PRODUCT + "/" + productId + "/deletion")
                .delete()
                .build();
    }
}
