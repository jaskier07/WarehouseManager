package pl.kania.warehousemanagerclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kania.warehousemanagerclient.model.entities.Product;

@AllArgsConstructor
@Getter
public enum IdType {
    GLOBAL(Product.ProductEntry._ID),
    LOCAL(Product.ProductEntry.LOCAL_ID);
    private String dbKey;
}
