package pl.kania.warehousemanagerclient.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static pl.kania.warehousemanagerclient.RestService.BASE_URI;

@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractRestTask extends AsyncTask<Void, Void, Void> {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
}
