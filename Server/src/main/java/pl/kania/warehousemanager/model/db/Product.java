package pl.kania.warehousemanager.model.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "LOCAL_ID")
    private Long localId;

    @Column(name = "MANUFACTURER_NAME")
    private String manufacturerName;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "QUANTITY")
    private Integer quantity = 0;

//    @Setter(AccessLevel.NONE)
//    @Column(name = "VECTOR_CLOCK")
//    private String vectorClock;

    @Column(name = "REMOVED")
    private boolean removed = false;

    @Column(name = "LAST_MODIFIED")
    private Timestamp lastModified;

    public void update(Product updatedProduct) {
        setManufacturerName(updatedProduct.getManufacturerName());
        setModelName(updatedProduct.getModelName());
        setPrice(updatedProduct.getPrice());
    }

//    public void setVectorClock(ProductVectorClock clock) throws JsonProcessingException {
//        this.vectorClock = clock.toJson();
//    }
//
//    public ProductVectorClock getVectorClock() throws JsonProcessingException {
//        return new ObjectMapper().readValue(vectorClock, ProductVectorClock.class);
//    }
}
