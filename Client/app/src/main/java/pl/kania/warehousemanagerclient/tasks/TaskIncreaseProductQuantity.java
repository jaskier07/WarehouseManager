package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import pl.kania.warehousemanagerclient.model.ProductQuantity;

class TaskIncreaseProductQuantity extends AbstractRestTask<ProductQuantity> {

    private final Runnable afterIncrease;

    TaskIncreaseProductQuantity(Runnable afterIncrease) {
        this.afterIncrease = afterIncrease;
    }

    @Override
    protected Void doInBackground(ProductQuantity... productQuantities) {
        Log.i("task", "increase");
        return null;
    }
}
