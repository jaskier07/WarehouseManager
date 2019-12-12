package pl.kania.warehousemanager.services.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.kania.warehousemanager.model.db.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, ChangeableQuantity {
}
