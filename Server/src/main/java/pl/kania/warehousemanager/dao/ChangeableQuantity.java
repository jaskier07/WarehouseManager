package pl.kania.warehousemanager.dao;

public interface ChangeableQuantity {
    void decreaseProductQuantityBy(Integer quantity, Long productId);

    void increaseProductQuantityBy(Integer quantity, Long productId);
}
