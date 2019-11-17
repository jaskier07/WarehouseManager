package pl.kania.warehousemanagerclient.model;

import java.util.Base64;

import lombok.NonNull;
import lombok.Value;

@Value
public class UserCredentials {
    @NonNull
    String login;
    @NonNull
    String password;
    @NonNull
    String clientId;
    @NonNull
    String clientSecret;

    public String getEncoded() {
        final String textToEncode = login + "." + password;
        return new String(Base64.getEncoder().encode(textToEncode.getBytes()));
    }
}
