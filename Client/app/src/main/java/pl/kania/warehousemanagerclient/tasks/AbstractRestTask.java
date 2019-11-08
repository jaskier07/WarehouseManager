package pl.kania.warehousemanagerclient.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.OkHttpClient;

@Getter(value = AccessLevel.PROTECTED)
abstract class AbstractRestTask<P> extends AsyncTask<P, Void, Void> {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
}
