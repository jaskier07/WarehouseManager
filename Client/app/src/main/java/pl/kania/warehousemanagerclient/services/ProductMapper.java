package pl.kania.warehousemanagerclient.services;

import android.content.ContentValues;
import android.database.Cursor;

import java.sql.Timestamp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kania.warehousemanagerclient.model.entities.Product;

import static android.provider.BaseColumns._ID;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.LAST_MODIFIED;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MANUFACTURER_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MODEL_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRICE;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.QUANTITY;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.REMOVED;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static ContentValues mapProductToContentValues(Product product, boolean mapQuantity) {
        final ContentValues cv = new ContentValues();
        cv.put(MANUFACTURER_NAME, product.getManufacturerName());
        cv.put(MODEL_NAME, product.getModelName());
        cv.put(_ID, product.getId());
        cv.put(PRICE, product.getPrice());
        cv.put(REMOVED, product.isRemoved());
        cv.put(LAST_MODIFIED, product.getLastModified().toString());
        if (mapQuantity) {
            cv.put(QUANTITY, product.getQuantity());
        }
        return cv;
    }

    public static ContentValues mapNewProductToContentValues(Product product) {
        final ContentValues cv = new ContentValues();
        cv.put(_ID, -1);
        cv.put(MANUFACTURER_NAME, product.getManufacturerName());
        cv.put(MODEL_NAME, product.getModelName());
        cv.put(PRICE, product.getPrice());
        cv.put(REMOVED, product.isRemoved());
        cv.put(LAST_MODIFIED, product.getLastModified().toString());
        cv.put(QUANTITY, product.getQuantity());
        return cv;
    }

    public static Product mapCursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getLong(0));
        product.setManufacturerName(cursor.getString(1));
        product.setModelName(cursor.getString(2));
        product.setPrice(cursor.getDouble(3));
        product.setQuantity(cursor.getInt(4));
        product.setLocalId(cursor.getLong(5));
        product.setRemoved(cursor.getInt(6) > 0);
        product.setLastModified(Timestamp.valueOf(cursor.getString(7)));
        return product;
    }

}
