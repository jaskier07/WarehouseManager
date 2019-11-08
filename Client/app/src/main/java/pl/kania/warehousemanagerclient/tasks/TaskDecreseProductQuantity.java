package pl.kania.warehousemanagerclient.tasks;

import pl.kania.warehousemanagerclient.model.ProductQuantity;

class TaskDecreseProductQuantity extends AbstractRestTask<ProductQuantity> {

    private final Runnable afterDecrease;

    TaskDecreseProductQuantity(Runnable afterDecrease) {
        this.afterDecrease = afterDecrease;
    }

    @Override
    protected Void doInBackground(ProductQuantity... productQuantities) {
        return null;
    }
}
