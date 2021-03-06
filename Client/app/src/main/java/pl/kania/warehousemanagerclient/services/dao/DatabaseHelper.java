package pl.kania.warehousemanagerclient.services.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pl.kania.warehousemanagerclient.model.entities.ProductClient;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Warehouse.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductClient.ProductDll.DROP_TABLE);
        db.execSQL(ProductClient.ProductDll.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
