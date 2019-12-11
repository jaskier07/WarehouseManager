package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import okhttp3.Request;
import okhttp3.Response;



public class TaskCheckToken extends AbstractRestTask<String, Boolean> {

    public TaskCheckToken(Context context) {
        super(context);
    }

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
                .url(getBaseProductUri() + "/check-token")
                .build();
    }
}
