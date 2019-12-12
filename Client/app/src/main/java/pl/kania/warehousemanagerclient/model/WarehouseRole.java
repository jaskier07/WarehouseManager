package pl.kania.warehousemanagerclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WarehouseRole {
    MANAGER("MANAGER"),
    EMPLOYEE("EMPLOYEE");

    private String dbKey;
}