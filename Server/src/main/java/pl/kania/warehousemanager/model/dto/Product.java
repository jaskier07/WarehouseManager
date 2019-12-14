package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Product {
    private Long id;
    private Long nonServerId;
    private String manufacturerName;
    private String modelName;
    private Double price;
    private Integer quantity = 0;
    private String lastModified;
    private boolean removed;
}
