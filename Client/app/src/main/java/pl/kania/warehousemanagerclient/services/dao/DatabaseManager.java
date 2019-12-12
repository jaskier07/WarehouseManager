package pl.kania.warehousemanagerclient.services.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import lombok.Getter;

public class DatabaseManager {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    @Getter
    private ProductDao productDao;
    @Getter
    private UserDao userDao;

    public DatabaseManager(Context context) {
        this.context = context;
        productDao = new ProductDao(db, this::open, this::close);
        userDao = new UserDao(db, this::open, this::close);
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


}
