package pl.kania.warehousemanagerclient.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractRestTask<P, R> extends AsyncTask<P, Void, R> {

    protected static final String AUTH_HEADER = "Authorization";
    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token;

    AbstractRestTask(String token) {
        this.token = token;
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
}
