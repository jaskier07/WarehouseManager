package pl.kania.warehousemanagerclient.services;

import android.content.Context;

import lombok.Getter;
import pl.kania.warehousemanagerclient.R;

@Getter
public class BaseUriProvider {

    private final String BASE_URI;
    private final String BASE_URI_PRODUCT;

    public BaseUriProvider(Context context) {
        this.BASE_URI = context.getString(R.string.rest_service_uri);
        this.BASE_URI_PRODUCT = BASE_URI + "/product";
    }
}
