package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.services.ConfigurationProvider;

/**
 * @param <P> Input data
 * @param <R> Output data
 */
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractRestTask<P, R> extends AsyncTask<P, Void, R> {

    protected static final String AUTH_HEADER = "Authorization";
    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token;
    private ConfigurationProvider configurationProvider;

    AbstractRestTask(String token, Context context) {
        this.token = token;
        this.configurationProvider = new ConfigurationProvider(context);
    }

    AbstractRestTask(Context context) {
        this.configurationProvider = new ConfigurationProvider(context);
    }

    protected MediaType getMediaType() {
        return MediaType.parse(MEDIA_TYPE);
    }

    protected Response executeRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    protected String getAuthValue() {
        return "bearer " + token;
    }

    protected String getBaseProductUri() {
        return configurationProvider.getBaseProductUri();
    }

    protected String getBaseUri() {
        return configurationProvider.getBaseUri();
    }
}
