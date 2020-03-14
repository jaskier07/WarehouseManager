package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataToSyncOnServer {

    private List<ProductClient> newProducts;
    private List<ProductClient> existingProducts;
    private List<Long> removedProductsIds;
}
