package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanager.model.db.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataToSyncOnClient {
    private List<Long> removedIds = new ArrayList<>();
    private List<Product> newProducts = new ArrayList<>();
    private List<Product> updatedProducts = new ArrayList<>();
    private Map<Long, Long> savedProductIdPerLocalId = new HashMap<>();
}
