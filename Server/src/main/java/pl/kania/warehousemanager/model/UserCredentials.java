package pl.kania.warehousemanager.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class UserCredentials {
    @NonNull
    String login;
    @NonNull
    String password;
}
