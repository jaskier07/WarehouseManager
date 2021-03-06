package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanager.model.TransferableProduct;

import java.sql.Timestamp;

@Setter
@Getter
public class ProductClient implements TransferableProduct {
    private Long id;
    private Long localId;
    private String manufacturerName;
    private String modelName;
    private Double price;
    private Integer quantity = 0;
    private Timestamp lastModified;
    private boolean removed;

    @Override
    public String toString() {
        return "Product: " + getManufacturerName() + " " + getModelName() + " (id " + getId() + ")(local id " + getLocalId() + ")";
    }
}
