package pl.kania.warehousemanagerclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;

@AllArgsConstructor
@Getter
public enum IdType {
    GLOBAL(ProductClient.ProductEntry._ID),
    LOCAL(ProductClient.ProductEntry.LOCAL_ID);
    private String dbKey;
}
