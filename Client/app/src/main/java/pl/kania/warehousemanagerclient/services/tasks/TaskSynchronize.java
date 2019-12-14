package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.function.Consumer;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnDevice;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnServer;

public class TaskSynchronize extends AbstractRestTask<DataToSyncOnServer, DataToSyncOnDevice> {

    private Consumer<DataToSyncOnDevice> afterSync;

    TaskSynchronize(String token, Context context, Consumer<DataToSyncOnDevice> afterSync) {
        super(token, context);
        this.afterSync = afterSync;
    }

    @Override
    protected DataToSyncOnDevice doInBackground(DataToSyncOnServer... dataToSyncOnServer) {
        try {
            final Response response = executeRequest(getRequest(dataToSyncOnServer[0]));
            final ResponseBody body = response.body();
            if (response.isSuccessful() && body != null) {
                afterSync.accept(getObjectMapper().readValue(body.string(), DataToSyncOnDevice.class));
            } else {
                Log.w("synchronize", "Response body is empty");
            }
        } catch (Exception e) {
            Log.e("synchronize", "An error occured while synchronizing", e);
        }
        return null;
    }

    private Request getRequest(DataToSyncOnServer dataToSyncOnServer) throws JsonProcessingException {
        final RequestBody requestBody = RequestBody.create(getMediaType(), getObjectMapper().writeValueAsString(dataToSyncOnServer));
        return new Request.Builder()
                .url(getBaseUri() + "/synchronize")
                .addHeader(AUTH_HEADER, getAuthValue())
                .post(requestBody)
                .build();
    }
}
