package pl.kania.warehousemanagerclient.model.entities;

import android.provider.BaseColumns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static android.provider.BaseColumns._ID;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MANUFACTURER_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MODEL_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRICE;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRODUCT_TABLE_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.QUANTITY;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private Long id;
    @Setter
    private String manufacturerName;
    @Setter
    private String modelName;
    @Setter
    private Double price;

    private Integer quantity;

    public static class ProductEntry implements BaseColumns {
        public static final String PRODUCT_TABLE_NAME = "PRODUCT";
        public static final String MANUFACTURER_NAME = "MANUFACTURER_NAME";
        public static final String MODEL_NAME = "MODEL_NAME";
        public static final String PRICE = "PRICE";
        public static final String QUANTITY = "QUANTITY";
    }

    public static class ProductDll {
        public static final String CREATE_TABLE = "CREATE TABLE " + PRODUCT_TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MANUFACTURER_NAME + " TEXT, "
                + MODEL_NAME + " TEXT, "
                + PRICE + " REAL, "
                + QUANTITY + " INTEGER);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME;
    }
}
