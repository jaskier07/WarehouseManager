package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.kania.warehousemanagerclient.model.login.LoginResult;
import pl.kania.warehousemanagerclient.model.login.UserCredentials;



public class TaskLogIn extends AbstractRestTask<UserCredentials, LoginResult> {

    public TaskLogIn(Context context) {
        super(context);
    }

    @Override
    protected LoginResult doInBackground(UserCredentials... credentials) {
        try {
            final Response response = executeRequest(getRequest(credentials[0]));
            final ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();
            final LoginResult loginResult = getObjectMapper().readValue(responseBodyString, LoginResult.class);
            if (response.isSuccessful() ) {
                return loginResult;
            } else {
                Log.w("login", "Unsuccessful login");
                return loginResult;
            }
        } catch (Exception e) {
            Log.e("login", "An error occured while trying to log user", e);
        }
        LoginResult loginResult = new LoginResult();
        loginResult.setErrorMessage("An error occured while trying to log user");
        return loginResult;
    }

    private Request getRequest(UserCredentials credentials) {
        final RequestBody requestBody = new FormBody.Builder()
                .add("clientId", credentials.getClientId())
                .add("clientSecret", credentials.getClientSecret())
                .build();
        return new Request.Builder()
                .addHeader("Authorization", "Basic " + credentials.getEncoded())
                .post(requestBody)
                .url(getBaseUri() + "/log-in")
                .build();
    }
}
