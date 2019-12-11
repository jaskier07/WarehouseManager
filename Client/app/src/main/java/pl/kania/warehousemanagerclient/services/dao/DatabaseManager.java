package pl.kania.warehousemanagerclient.services.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertAllProducts(List<Product> products) {
        products.forEach(this::insertProduct);
    }

    public Long insertProduct(Product product) {
        open();
        final ContentValues cv = mapProductToContentValues(product, true);
        long id = db.insert(PRODUCT_TABLE_NAME, null, cv);
        if (id == -1) {
            Log.e("insert", "product has not been inserted");
        }
        close();
        return id;
    }

    public List<Product> selectAllProducts() {
        open();
        final List<Product> products = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                products.add(mapCursorToProduct(cursor));
                cursor.moveToNext();
            }
        }
        close();
        return products;
    }

    public Optional<Product> selectProduct(Long id) {
        open();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME + " WHERE " + _ID + " = " + id, null);
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                close();
                return Optional.of(mapCursorToProduct(cursor));
            }
        }
        close();
        return Optional.empty();
    }

    private Product mapCursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getLong(0));
        product.setManufacturerName(cursor.getString(1));
        product.setModelName(cursor.getString(2));
        product.setPrice(cursor.getDouble(3));
        product.setQuantity(cursor.getInt(4));
        return product;
    }

    public boolean deleteProduct(Long productId) {
        open(); // TODO sprawdzenie uprawnień
        boolean inserted = db.delete(PRODUCT_TABLE_NAME, _ID + "=" + productId, null) > 0;
        if (!inserted) {
            Log.e("delete", "product has not been deleted");
        }
        close();
        return inserted;
    }

    public boolean updateNonQuantityProductValues(Long id, Product product) {
        open();
        final ContentValues cv = mapProductToContentValues(product, false);
        boolean updated = db.update(PRODUCT_TABLE_NAME, cv, _ID + " = " + id, null) > 0;
        if (!updated) {
            Log.e("update", "product has not been updated");
        }
        close();
        return updated;
    }

    public boolean updateQuantityProductValue(Long id, int change) {
        open();
        final Cursor cursor = db.rawQuery("UPDATE product SET quantity = quantity + " + change + " WHERE id = " + id, null);
        if (cursor != null) {
            final boolean updated = cursor.moveToFirst();
            if (!updated) {
                Log.e("update", "product's quantity has not been updated");
            }
            close();
            return updated;
        }
        Log.e("update", "product's quantity has not been updated");
        close();
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
