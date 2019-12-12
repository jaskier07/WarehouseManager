package pl.kania.warehousemanagerclient.model.entities;

import android.provider.BaseColumns;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanagerclient.model.WarehouseRole;

import static android.provider.BaseColumns._ID;
import static pl.kania.warehousemanagerclient.model.entities.User.UserEntry.LOGIN;
import static pl.kania.warehousemanagerclient.model.entities.User.UserEntry.PASSWORD;
import static pl.kania.warehousemanagerclient.model.entities.User.UserEntry.ROLE;
import static pl.kania.warehousemanagerclient.model.entities.User.UserEntry.USER_TABLE_NAME;

@Getter
@Setter
public class User {
    private Long id;
    private String login;
    private String password;
    private WarehouseRole role;

    public static class UserEntry implements BaseColumns {
        public static final String USER_TABLE_NAME = "USER";
        public static final String LOGIN = "LOGIN";
        public static final String PASSWORD = "PASSWORD";
        public static final String ROLE = "ROLE";
    }

    public static class UserDll {
        public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LOGIN + " TEXT, "
                + PASSWORD + " TEXT, "
                + ROLE + " REAL);";
    }
}
