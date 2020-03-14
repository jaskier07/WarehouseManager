package pl.kania.warehousemanager.stereotype;

import org.junit.jupiter.params.aggregator.AggregateWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(ProductAggregator.class)
public @interface CsvToProduct {
}
