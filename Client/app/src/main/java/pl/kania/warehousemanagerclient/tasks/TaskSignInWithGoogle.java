package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.kania.warehousemanagerclient.model.GoogleCredentials;
import pl.kania.warehousemanagerclient.model.LoginResult;

import static pl.kania.warehousemanagerclient.tasks.RestService.BASE_URI;

class TaskSignInWithGoogle extends AbstractRestTask<GoogleCredentials, LoginResult>{

    @Override
    protected LoginResult doInBackground(GoogleCredentials... googleCredentials) {
        try {
            final Response response = executeRequest(getRequest(googleCredentials[0]));
            final ResponseBody responseBody = response.body();
            final LoginResult loginResult = getObjectMapper().readValue(responseBody.string(), LoginResult.class);
            if (response.isSuccessful() ) {
                return loginResult;
            } else {
                Log.w("sign in google", "Unsuccessful sign in with google");
                return loginResult;
            }
        } catch (Exception e) {
            Log.e("sign in google", "An error occured while trying to sign in user with google", e);
        }
        LoginResult loginResult = new LoginResult();
        loginResult.setErrorMessage("An error occured while trying to sign in user with google");
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
                .url(BASE_URI + "/sign-in-with-google")
                .build();
    }
}
