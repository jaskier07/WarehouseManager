package pl.kania.warehousemanagerclient.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanagerclient.model.entities.Product;

@Getter
@Setter
public class DataToSyncOnDevice {
    private List<Long> removedIds = new ArrayList<>();
    private List<Product> newProducts = new ArrayList<>();
    private List<Product> updatedProducts = new ArrayList<>();
}
