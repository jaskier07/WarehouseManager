package pl.kania.warehousemanagerclient.services.tasks;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.kania.warehousemanagerclient.model.login.GoogleCredentials;
import pl.kania.warehousemanagerclient.model.login.LoginResult;

import static pl.kania.warehousemanagerclient.services.tasks.RestService.BASE_URI;

class TaskLogInWithGoogle extends AbstractRestTask<GoogleCredentials, LoginResult> {

    @Override
    protected LoginResult doInBackground(GoogleCredentials... credentials) {
        try {
            final Response response = executeRequest(getRequest(credentials[0]));
            final ResponseBody responseBody = response.body();
            final LoginResult loginResult = getObjectMapper().readValue(responseBody.string(), LoginResult.class);
            if (response.isSuccessful() ) {
                return loginResult;
            } else {
                Log.w("login google", "Unsuccessful login with google");
                return loginResult;
            }
        } catch (Exception e) {
            Log.e("login google", "An error occured while trying to log user with google", e);
        }
        LoginResult loginResult = new LoginResult();
        loginResult.setErrorMessage("An error occured while trying to log user with Google");
        return loginResult;
    }

    private Request getRequest(GoogleCredentials credentials) {
        final RequestBody requestBody = new FormBody.Builder()
                .add("clientId", credentials.getClientId())
                .add("clientSecret", credentials.getClientSecret())
                .build();
        return new Request.Builder()
                .addHeader("Authorization", "access " + credentials.getToken())
                .post(requestBody)
                .url(BASE_URI + "/log-in-with-google")
                .build();
    }
}
