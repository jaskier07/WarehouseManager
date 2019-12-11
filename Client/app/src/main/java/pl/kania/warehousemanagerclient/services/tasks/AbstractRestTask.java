package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.kania.warehousemanagerclient.services.BaseUriProvider;

@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractRestTask<P, R> extends AsyncTask<P, Void, R> {

    protected static final String AUTH_HEADER = "Authorization";
    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token;
    private BaseUriProvider baseUriProvider;

    AbstractRestTask(String token, Context context) {
        this.token = token;
        this.baseUriProvider = new BaseUriProvider(context);
    }

    AbstractRestTask(Context context) {
        this.baseUriProvider = new BaseUriProvider(context);
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
        return baseUriProvider.getBASE_URI_PRODUCT();
    }

    protected String getBaseUri() {
        return baseUriProvider.getBASE_URI();
    }
}
