package pl.kania.warehousemanager.model.vector;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ProductVectorClockNode {
    @Setter
    private int quantity = 0;
    private String nodeName;

    public ProductVectorClockNode(int quantity, @NonNull String nodeName) {
        this.quantity = quantity;
        this.nodeName = nodeName;
    }

    public ProductVectorClockNode(@NonNull String nodeName) {
        this.nodeName = nodeName;
    }
}