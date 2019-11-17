package pl.kania.warehousemanager.security;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

@Component
public class GoogleService {

    @Autowired
    private Environment environment;
    private OkHttpClient client = new OkHttpClient();

    private String userAuthorizationUri;
    private String clientId;
    private String redirectUri;

    @PostConstruct
    private void initializeFieldsFromAppProperties() {
        userAuthorizationUri = environment.getProperty("google.userAuthorizationUri");
        clientId = environment.getProperty("google.client.id");
        redirectUri = environment.getProperty("google.redirectUri");
    }

    public void dd() {
        String state = new BigInteger(130, new SecureRandom()).toString(32);

        String url = userAuthorizationUri + "?client_id=" + clientId + "&response_type=code&scope=openid%20email"
                + "&login_hint=jsmith@example.com&redirect_uri=" + redirectUri;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                final ResponseBody body = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
