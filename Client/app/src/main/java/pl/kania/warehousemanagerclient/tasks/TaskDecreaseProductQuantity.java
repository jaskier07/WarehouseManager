package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.ProductQuantity;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI_PRODUCT;

class TaskDecreaseProductQuantity extends AbstractRestTask<ProductQuantity> {

    private final Runnable afterDecrease;

    TaskDecreaseProductQuantity(Runnable afterDecrease) {
        this.afterDecrease = afterDecrease;
    }

    @Override
    protected Void doInBackground(ProductQuantity... productQuantities) {
        if (productQuantities.length > 0) {
            try {
                final Request request = getRequest(productQuantities[0]);
                final Call call = getClient().newCall(request);
                final Response response = call.execute();
                if (response.isSuccessful()) {
                    afterDecrease.run();
                } else {
                    Log.w("decrease", "Decreasing product's amount did not complete successfully: " + response.code());
                }
            } catch (Exception e) {
                Log.e("decrease", "An error occured while decreasing product's amount", e);
            }
        }
        return null;
    }

    private Request getRequest(ProductQuantity productQuantity) throws JsonProcessingException {
        final RequestBody responseBody = RequestBody.create(getMediaType(), getObjectMapper().writeValueAsString(productQuantity.getQuantity()));
        return new Request.Builder()
                .url(BASE_URI_PRODUCT + "/" + productQuantity.getProductId() + "/decrease")
                .patch(responseBody)
                .build();
    }
}
