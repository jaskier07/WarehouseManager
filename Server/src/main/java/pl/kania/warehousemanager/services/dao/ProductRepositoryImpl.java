package pl.kania.warehousemanager.services.dao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ProductRepositoryImpl implements ChangeableQuantity {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public boolean decreaseProductQuantityBy(Integer quantity, Long productId) {
        em.joinTransaction();
        return em.createNativeQuery("UPDATE PRODUCT SET quantity = quantity - :val WHERE id = :productId AND (quantity - :val >= 0)")
                .setParameter("val", quantity)
                .setParameter("productId", productId)
                .executeUpdate() > 0;
    }

    @Transactional
    @Override
    public boolean increaseProductQuantityBy(Integer quantity, Long productId) {
        em.joinTransaction();
        return em.createNativeQuery("UPDATE PRODUCT SET quantity = quantity + :val WHERE id = :productId")
                .setParameter("val", quantity)
                .setParameter("productId", productId)
                .executeUpdate() > 0;
    }
}
