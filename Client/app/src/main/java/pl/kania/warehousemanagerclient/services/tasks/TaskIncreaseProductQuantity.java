package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.dto.ProductQuantity;



class TaskIncreaseProductQuantity extends AbstractRestTask<ProductQuantity, Void> {

    private final Runnable afterIncrease;

    TaskIncreaseProductQuantity(String token, Runnable afterIncrease, Context context) {
        super(token, context);
        this.afterIncrease = afterIncrease;
    }

    @Override
    protected Void doInBackground(ProductQuantity... productQuantities) {
        if (productQuantities.length > 0) {
            try {
                final Request request = getRequest(productQuantities[0]);
                final Call call = getClient().newCall(request);
                final Response response = call.execute();
                if (response.isSuccessful()) {
                    afterIncrease.run();
                } else {
                    Log.w("increase", "Increasing product's amount did not complete successfully: " + response.code());
                }
            } catch (Exception e) {
                Log.e("increase", "An error occured while increasing product's amount", e);
            }
        }
        return null;
    }

    private Request getRequest(ProductQuantity productQuantity) throws JsonProcessingException {
        final RequestBody responseBody = RequestBody.create(getMediaType(), getObjectMapper().writeValueAsString(productQuantity.getQuantity()));
        return new Request.Builder()
                .addHeader(AUTH_HEADER, getAuthValue())
                .url(getBaseProductUri() + "/" + productQuantity.getProductId() + "/increase")
                .patch(responseBody)
                .build();
    }
}
