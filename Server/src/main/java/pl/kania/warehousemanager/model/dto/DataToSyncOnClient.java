package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataToSyncOnClient {
    private List<Long> removedIds = new ArrayList<>();
    private List<ProductClient> newProducts = new ArrayList<>();
    private List<ProductClient> updatedProducts = new ArrayList<>();
    private Map<Long, Long> savedProductIdPerLocalId = new HashMap<>();
}
