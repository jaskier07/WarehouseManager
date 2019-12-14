package pl.kania.warehousemanagerclient.model.entities;

import android.provider.BaseColumns;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static android.provider.BaseColumns._ID;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.LAST_MODIFIED;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.LOCAL_ID;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MANUFACTURER_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.MODEL_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRICE;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.PRODUCT_TABLE_NAME;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.QUANTITY;
import static pl.kania.warehousemanagerclient.model.entities.Product.ProductEntry.REMOVED;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private Long id;
    private String manufacturerName;
    private String modelName;
    private Double price;
    private Integer quantity;
    private Long localId;
//    private String vectorClock;
    private boolean removed;
    private Timestamp lastModified;

    public static class ProductEntry implements BaseColumns {
        public static final String PRODUCT_TABLE_NAME = "PRODUCT";
        public static final String LOCAL_ID = "LOCAL_ID";
        public static final String MANUFACTURER_NAME = "MANUFACTURER_NAME";
        public static final String MODEL_NAME = "MODEL_NAME";
        public static final String PRICE = "PRICE";
        public static final String QUANTITY = "QUANTITY";
//        public static final String VECTOR_CLOCK = "VECTOR_CLOCK";
        public static final String REMOVED = "REMOVED";
        public static final String LAST_MODIFIED = "LAST_MODIFIED";
    }

    public static class ProductDll {
        public static final String CREATE_TABLE = "CREATE TABLE " + PRODUCT_TABLE_NAME + "("
                + _ID + " INTEGER, "
                + MANUFACTURER_NAME + " TEXT, "
                + MODEL_NAME + " TEXT, "
                + PRICE + " REAL, "
                + QUANTITY + " INTEGER, "
                + LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + VECTOR_CLOCK + " TEXT, "
                + REMOVED + " INTEGER, "
                + LAST_MODIFIED + " DATETIME "
                + ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME;
    }

//    public void setVectorClock(ProductVectorClock clock) throws JsonProcessingException {
//        this.vectorClock = clock.toJson();
//    }
//
//    public ProductVectorClock getVectorClock() throws IOException {
//        if (vectorClock == null) {
//            return null;
//        }
//
//        return new ObjectMapper().readValue(vectorClock, ProductVectorClock.class);
//    }
}
