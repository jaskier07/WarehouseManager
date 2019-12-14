package pl.kania.warehousemanager.services.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kania.warehousemanager.model.db.Product;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, ChangeableQuantity {

    List<Product> findAllByRemoved(boolean removed);
}
