package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanager.model.db.Product;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DataToSyncOnDevice {
    private List<Long> removedIds = new ArrayList<>();
    private List<Product> newProducts = new ArrayList<>();
    private List<Product> updatedProducts = new ArrayList<>();
}
