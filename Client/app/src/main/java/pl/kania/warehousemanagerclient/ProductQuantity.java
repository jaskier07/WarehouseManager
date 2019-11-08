package pl.kania.warehousemanagerclient;

import lombok.Value;

@Value
public class ProductQuantity {
    Long productId;
    Integer quantity;
}
