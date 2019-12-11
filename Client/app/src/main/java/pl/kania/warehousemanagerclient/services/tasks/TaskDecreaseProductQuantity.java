package pl.kania.warehousemanagerclient.services.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.function.Consumer;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.model.dto.ChangeQuantityResult;
import pl.kania.warehousemanagerclient.model.dto.ProductQuantity;

import static pl.kania.warehousemanagerclient.services.tasks.RestService.BASE_URI_PRODUCT;

class TaskDecreaseProductQuantity extends AbstractRestTask<ProductQuantity, Void> {

    private final Runnable afterDecrease;
    private final Consumer<String> onFailed;

    TaskDecreaseProductQuantity(String token, Runnable afterDecrease, Consumer<String> onFailed) {
        super(token);
        this.afterDecrease = afterDecrease;
        this.onFailed = onFailed;
    }

    @Override
    protected Void doInBackground(ProductQuantity... productQuantities) {
        if (productQuantities.length > 0) {
            try {
                final Response response = executeRequest(getRequest(productQuantities[0]));
                if (response.isSuccessful()) {
                    afterDecrease.run();
                } else {
                    ChangeQuantityResult changeQuantityResult = getObjectMapper().readValue(response.body().string(), ChangeQuantityResult.class);
                    Log.w("decrease", "Decreasing product's amount did not complete successfully: " + changeQuantityResult.getError());
                    onFailed.accept(changeQuantityResult.getError());
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
                .addHeader(AUTH_HEADER, getAuthValue())
                .url(BASE_URI_PRODUCT + "/" + productQuantity.getProductId() + "/decrease")
                .patch(responseBody)
                .build();
    }
}
