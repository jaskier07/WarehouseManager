package pl.kania.warehousemanagerclient.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import lombok.Getter;
import pl.kania.warehousemanagerclient.R;

import static android.content.Context.TELEPHONY_SERVICE;

@Getter
public class ConfigurationProvider {

    private final String baseUri;
    private final String baseProductUri;
    private String clientId;
    private String clientSecret;
    private String googleClientId;
    private String device1Id;
    private String device2Id;
    private String serverId;

    public ConfigurationProvider(Context context) {
        this.baseUri = context.getString(R.string.rest_service_uri);
        this.baseProductUri = baseUri + "/product";
        clientId = getMyClientId(context);
        clientSecret = context.getString(R.string.client_secret);
        googleClientId = context.getString(R.string.google_client_id);
        device1Id = context.getString(R.string.android_client_id);
        device2Id = context.getString(R.string.android_client_id2);
        serverId = context.getString(R.string.server_id);
    }

    @SuppressLint("MissingPermission")
    private String getMyClientId(Context context) {
        final TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        return TelephonyMgr.getImei();
    }

}
