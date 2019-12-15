package pl.kania.warehousemanagerclient.services.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import pl.kania.warehousemanagerclient.model.IdType;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;
import pl.kania.warehousemanagerclient.services.ProductMapper;

import static android.provider.BaseColumns._ID;
import static pl.kania.warehousemanagerclient.model.entities.ProductClient.ProductEntry.PRODUCT_TABLE_NAME;
import static pl.kania.warehousemanagerclient.model.entities.ProductClient.ProductEntry.REMOVED;

public class DatabaseManager {

    private static final int ERROR = -1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
//    @Getter
//    private UserDao userDao;

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


    public boolean insertAllProducts(List<ProductClient> products, boolean hasGlobalId, boolean updateLastModified) {
        boolean allInserted = true;
        for (ProductClient product : products) {
            if (insertProduct(product, hasGlobalId, updateLastModified) == ERROR) {
                allInserted = false;
            }
        }
        return allInserted;
    }

    public Long insertProduct(ProductClient product, boolean hasGlobalId, boolean updateLastModified) {
        if (updateLastModified) {
            updateProductLastModified(product);
        }

        open();
        final ContentValues cv = hasGlobalId ? ProductMapper.mapProductToContentValues(product, true) : ProductMapper.mapNewProductToContentValues(product);
        long id = db.insert(PRODUCT_TABLE_NAME, null, cv);
        if (id == ERROR) {
            Log.e("insert", "product has not been inserted");
        }
        close();
        return id;
    }

    public List<ProductClient> selectAllNonRemovedNewProducts() {
        open();
        final List<ProductClient> products = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME +" WHERE " + REMOVED + " = 0 AND " + _ID + " = -1", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                products.add(ProductMapper.mapCursorToProduct(cursor));
                cursor.moveToNext();
            }
        }
        close();
        return products;
    }

    public List<ProductClient> selectAllNonRemovedProducts() {
        open();
        final List<ProductClient> products = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME +" WHERE " + REMOVED + " = 0", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                products.add(ProductMapper.mapCursorToProduct(cursor));
                cursor.moveToNext();
            }
        }
        close();
        return products;
    }

    public List<ProductClient> selectAllNonRemovedProductsWithGlobalId() {
        open();
        final List<ProductClient> products = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME +" WHERE " + REMOVED + " = 0 AND " + _ID + " != -1", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                products.add(ProductMapper.mapCursorToProduct(cursor));
                cursor.moveToNext();
            }
        }
        close();
        return products;
    }

    public List<Long> selectRemovedProductGlobalIds() {
        open();
        final Cursor cursor = db.rawQuery("SELECT " + _ID + " FROM " + PRODUCT_TABLE_NAME + " WHERE " + REMOVED + " = 1 AND " + _ID + " != -1", null);
        List<Long> ids = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ids.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        close();
        return ids;
    }

    public Optional<ProductClient> selectProduct(Long id, IdType idType) {
        open();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME + " WHERE " + idType.getDbKey() + " = " + id, null);
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                close();
                return Optional.of(ProductMapper.mapCursorToProduct(cursor));
            }
        }
        close();
        return Optional.empty();
    }

    public boolean deleteProduct(Long productId, IdType idType) {
        final Optional<ProductClient> product = selectProduct(productId, idType);
        if (product.isPresent()) {
            // TODO permission check
            product.get().setRemoved(true);
            updateProduct(product.get(), idType, false, false);
            return true;
        } else {
            Log.e("delete", "product has not been deleted because it does not exist (global id =" + product + ")");
            return false;
        }
    }

    public boolean updateProduct(ProductClient product, IdType idType, boolean updateLastModified, boolean updateQuantity) {
        if (updateLastModified) {
            updateProductLastModified(product);
        }

        open();
        final ContentValues cv = ProductMapper.mapProductToContentValues(product, updateQuantity);
        final Long id = idType == IdType.GLOBAL ? product.getId() : product.getLocalId();
        boolean updated = db.update(PRODUCT_TABLE_NAME, cv, idType.getDbKey() + " = " + id, null) > 0;
        if (!updated) {
            Log.e("update", "product has not been updated");
        }
        close();
        return updated;
    }

    public boolean updateQuantityProductValue(Long id, int change, IdType idType, boolean updateLastModified) {
        final Optional<ProductClient> product = selectProduct(id, idType);
        open();
        if (product.isPresent()) {
            if (updateLastModified) {
                updateProductLastModified(product.get());
            }
            product.get().setQuantity(product.get().getQuantity() + change);
            final ContentValues cv = ProductMapper.mapProductToContentValues(product.get(), true);
            int rowsUpdated = db.update(PRODUCT_TABLE_NAME, cv, idType.getDbKey() + "=" + id, null);
            return rowsUpdated > 0;
        }
        close();
        return false;
    }

    private void updateProductLastModified(ProductClient product) {
        product.setLastModified(new Timestamp(new Date().getTime()));
    }

}
