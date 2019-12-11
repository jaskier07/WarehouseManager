package pl.kania.warehousemanagerclient.model.login;

import lombok.Value;

@Value
public class GoogleCredentials {
    private String token;
    private String clientId;
    private String clientSecret;
}
