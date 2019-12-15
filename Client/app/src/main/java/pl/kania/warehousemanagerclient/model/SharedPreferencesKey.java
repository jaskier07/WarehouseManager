package pl.kania.warehousemanagerclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SharedPreferencesKey {
    SHARED_PREFERENCES_NAME("com.kania.warehousemanager.client"),
    TOKEN("token"),
    USER_LOGIN("userLogin"),
    LOGGING_METHOD("loggingMethod"),
    USER_IS_MANAGER("isManager");

    private String key;
}
