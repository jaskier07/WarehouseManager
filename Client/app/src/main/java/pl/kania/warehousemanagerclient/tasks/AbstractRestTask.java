package pl.kania.warehousemanagerclient.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

@Getter(value = AccessLevel.PROTECTED)
abstract class AbstractRestTask<P, R> extends AsyncTask<P, Void, R> {

    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected MediaType getMediaType() {
        return MediaType.parse(MEDIA_TYPE);
    }
}
