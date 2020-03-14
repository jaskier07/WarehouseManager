package pl.kania.warehousemanager.stereotype;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.vector.ProductVectorClock;
import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductAggregator implements ArgumentsAggregator {
    @SneakyThrows
    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        int index = 0;

        Product product = new Product();
        product.setId(accessor.getLong(index++));
        index++;
        product.setManufacturerName(accessor.getString(index++));
        product.setModelName(accessor.getString(index++));
        product.setPrice(accessor.getDouble(index++));
        product.setRemoved(accessor.getBoolean(index++));
        String date = accessor.getString(index++);
        if (date != null) {
            product.setLastModified(Timestamp.valueOf(date));
        }

        List<ProductVectorClockNode> nodes = new ArrayList<>();

        while (accessor.size() > index) {
            ProductVectorClockNode node;
            Integer quantity = accessor.getInteger(index++);
            if (quantity != null) {
                node = new ProductVectorClockNode(quantity, accessor.getString(index++));
            } else {
                node = new ProductVectorClockNode(accessor.getString(index++));
            }
            nodes.add(node);
        }

        product.setVectorClock(new ProductVectorClock(nodes));
        return product;
    }
}
