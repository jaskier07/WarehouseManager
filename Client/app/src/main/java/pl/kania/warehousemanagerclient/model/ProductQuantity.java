package pl.kania.warehousemanagerclient.model;

import lombok.Value;

@Value
public class ProductQuantity {
    Long productId;
    Integer quantity;
}
