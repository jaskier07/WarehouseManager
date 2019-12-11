package pl.kania.warehousemanagerclient.services.tasks;

import android.util.Log;

import okhttp3.Request;
import okhttp3.Response;

import static pl.kania.warehousemanagerclient.services.tasks.RestService.BASE_URI;

public class TaskCheckToken extends AbstractRestTask<String, Boolean> {


    @Override
    protected Boolean doInBackground(String... tokens) {
        try {
            final Response response = executeRequest(getRequest(tokens[0]));
            if (response.isSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            Log.e("checkToken", "An error occured while token checking", e);
        }
        return false;
    }

    private Request getRequest(String token) {
        return new Request.Builder()
                .get()
                .addHeader(AUTH_HEADER, "bearer " + token)
                .url(BASE_URI + "/check-token")
                .build();
    }
}
