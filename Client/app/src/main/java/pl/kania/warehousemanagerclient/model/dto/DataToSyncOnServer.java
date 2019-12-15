package pl.kania.warehousemanagerclient.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;

@Getter
@Setter
public class DataToSyncOnServer {
    private List<ProductClient> newProducts;
    private List<ProductClient> existingProducts;
    private List<Long> removedProductsIds;
}
