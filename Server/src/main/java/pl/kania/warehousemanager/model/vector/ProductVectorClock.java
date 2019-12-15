package pl.kania.warehousemanager.model.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductVectorClock {
    private List<ProductVectorClockNode> nodes;

    public ProductVectorClockNode getNode(String name) {
        return nodes.stream().filter(n -> n.getNodeName().equals(name)).findFirst().get();
    }

    public void copyQuantity(String from, String to) {
        getNode(to).setQuantity(getNode(from).getQuantity());
    }
}

