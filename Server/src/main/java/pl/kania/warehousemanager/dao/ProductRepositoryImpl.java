package pl.kania.warehousemanager.dao;

import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ProductRepositoryImpl implements ChangeableQuantity {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void decreaseProductQuantityBy(Integer quantity, Long productId) {
        // TODO
    }

    @Override
    public void increaseProductQuantityBy(Integer quantity, Long productId) {
        // TODO
    }
}
