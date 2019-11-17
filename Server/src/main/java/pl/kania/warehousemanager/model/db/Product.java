package pl.kania.warehousemanager.model.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "MANUFACTURER_NAME")
    private String manufacturerName;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "QUANTITY")
    private Integer quantity = 0;

    public void update(Product updatedProduct) {
        setManufacturerName(updatedProduct.getManufacturerName());
        setModelName(updatedProduct.getModelName());
        setPrice(updatedProduct.getPrice());
    }
}