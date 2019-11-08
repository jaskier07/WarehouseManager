package pl.kania.warehousemanagerclient.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private Long id;
    @Setter
    private String manufacturerName;
    @Setter
    private String modelName;
    @Setter
    private Double price;

    private Integer quantity;
}
