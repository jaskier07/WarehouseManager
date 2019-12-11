package pl.kania.warehousemanagerclient.services.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import pl.kania.warehousemanagerclient.model.entities.Product;

import static android.provider.BaseColumns._ID;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MANUFACTURER_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MODEL_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRICE;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRODUCT_TABLE_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.QUANTITY;

public class DatabaseManager {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertAll(List<Product> products) {
        products.forEach(this::insert);
    }

    public boolean insert(Product product) {
        ContentValues cv = mapProductToContentValues(product, true);
        boolean inserted = db.insert(PRODUCT_TABLE_NAME, null, cv) > 0;
        if (!inserted) {
            Log.e("insert", "product has not been inserted");
        }
        return inserted;
    }

    public boolean delete(long id) {
        boolean inserted = db.delete(PRODUCT_TABLE_NAME, _ID + "=" + id, null) > 0;
        if (!inserted) {
            Log.e("delete", "product has not been deleted");
        }
        return inserted;
    }

    public boolean updateNonQuantityValues(Long id, Product product) {
        ContentValues cv = mapProductToContentValues(product, false);
        boolean updated = db.update(PRODUCT_TABLE_NAME, cv, _ID + " = " + id, null) > 0;
        if (!updated) {
            Log.e("update", "product has not been updated");
        }
        return updated;
    }

    public boolean updateQuantityValue(Long id, int change) {
        final Cursor cursor = db.rawQuery("UPDATE product SET quantity = quantity + " + change + " WHERE id = " + id, null);
        if (cursor != null) {
            final boolean updated = cursor.moveToFirst();
            if (!updated) {
                Log.e("update", "product's quantity has not been updated");
            }
            return updated;
        }
        Log.e("update", "product's quantity has not been updated");
        return false;
    }

    private ContentValues mapProductToContentValues(Product product, boolean mapQuantity) {
        final ContentValues cv = new ContentValues();
        cv.put(MANUFACTURER_NAME, product.getManufacturerName());
        cv.put(MODEL_NAME, product.getModelName());
        cv.put(_ID, product.getId());
        cv.put(PRICE, product.getPrice());
        if (mapQuantity) {
            cv.put(QUANTITY, product.getQuantity());
        }
        return cv;
    }
}
