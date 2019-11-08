package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import pl.kania.warehousemanagerclient.model.ProductQuantity;

class TaskDecreseProductQuantity extends AbstractRestTask<ProductQuantity> {

    private final Runnable afterDecrease;

    TaskDecreseProductQuantity(Runnable afterDecrease) {
        this.afterDecrease = afterDecrease;
    }

    @Override
    protected Void doInBackground(ProductQuantity... productQuantities) {
        Log.i("task", "decrease");
        return null;
    }
}
