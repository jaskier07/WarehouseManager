package pl.kania.warehousemanagerclient.model.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;

@Getter
@Setter
public class DataToSyncOnClient {
    private List<Long> removedIds = new ArrayList<>();
    private List<ProductClient> newProducts = new ArrayList<>();
    private List<ProductClient> updatedProducts = new ArrayList<>();
    private Map<Long, Long> savedProductIdPerLocalId = new HashMap<>();
}
