package pl.kania.warehousemanager.dao;

public interface ChangeableQuantity {
    boolean decreaseProductQuantityBy(Integer quantity, Long productId);

    boolean increaseProductQuantityBy(Integer quantity, Long productId);
}
