package pl.kania.warehousemanager.model.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanager.model.TransferableProduct;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Setter
@Getter
public class Product implements Cloneable, TransferableProduct {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "MANUFACTURER_NAME")
    private String manufacturerName;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "VECTOR_CLOCK")
    private String vectorClock;

    @Column(name = "REMOVED")
    private boolean removed;

    @Column(name = "LAST_MODIFIED")
    private Timestamp lastModified;

    public void updateFrom(Product updatedProduct) {
        setManufacturerName(updatedProduct.getManufacturerName());
        setModelName(updatedProduct.getModelName());
        setPrice(updatedProduct.getPrice());
    }

    @Override
    public Product clone() throws CloneNotSupportedException {
        Product copy = (Product) super.clone();
        copy.id = getId();
        copy.removed = isRemoved();
        copy.lastModified = getLastModified();
        copy.manufacturerName = getManufacturerName();
        copy.modelName = getModelName();
        copy.price = getPrice();
        return copy;
    }

    public void setVectorClock(ProductVectorClock clock) throws JsonProcessingException {
        this.vectorClock = new ObjectMapper().writeValueAsString(clock.getNodes());
        new ObjectMapper().readValue(vectorClock, ProductVectorClockNode[].class);
    }

    public ProductVectorClock getVectorClock() throws JsonProcessingException {
        final List<ProductVectorClockNode> list = new ObjectMapper().readValue(vectorClock, new TypeReference<List<ProductVectorClockNode>>(){});
        return new ProductVectorClock(list);
    }

    @Override
    public String toString() {
        return "Product: " + getManufacturerName() + " " + getModelName() + " (id " + getId() + ")";
    }
}
