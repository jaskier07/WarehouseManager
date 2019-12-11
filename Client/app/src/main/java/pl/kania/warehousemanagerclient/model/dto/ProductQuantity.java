package pl.kania.warehousemanagerclient.model.dto;

import lombok.Value;

@Value
public class ProductQuantity {
    Long productId;
    Integer quantity;
}
