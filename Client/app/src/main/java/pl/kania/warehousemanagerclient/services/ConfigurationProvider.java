package pl.kania.warehousemanagerclient.services;

import android.content.Context;

import lombok.Getter;
import pl.kania.warehousemanagerclient.R;

@Getter
public class ConfigurationProvider {

    private final String baseUri;
    private final String baseProductUri;
    private String clientId;
    private String clientSecret;
    private String googleClientId;

    public ConfigurationProvider(Context context) {
        this.baseUri = context.getString(R.string.rest_service_uri);
        this.baseProductUri = baseUri + "/product";
        clientId = context.getString(R.string.client_id);
        clientSecret = context.getString(R.string.client_secret);
        googleClientId = context.getString(R.string.google_client_id);
    }

}
