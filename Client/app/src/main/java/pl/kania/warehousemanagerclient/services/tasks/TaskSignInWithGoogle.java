package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.util.Log;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.kania.warehousemanagerclient.model.login.GoogleCredentials;
import pl.kania.warehousemanagerclient.model.login.LoginResult;



class TaskSignInWithGoogle extends AbstractRestTask<GoogleCredentials, LoginResult>{

    public TaskSignInWithGoogle(Context context) {
        super(context);
    }

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
                .url(getBaseUri() + "/sign-in-with-google")
                .build();
    }
}
