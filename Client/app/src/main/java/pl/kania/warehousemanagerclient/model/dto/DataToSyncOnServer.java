package pl.kania.warehousemanagerclient.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanagerclient.model.entities.Product;

@Getter
@Setter
public class DataToSyncOnServer {
    private List<Product> products;
    private List<Long> removedProductsIds;
}
