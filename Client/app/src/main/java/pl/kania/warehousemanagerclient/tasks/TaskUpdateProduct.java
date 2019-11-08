package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

import pl.kania.warehousemanagerclient.model.Product;

class TaskUpdateProduct extends AbstractRestTask<Product> {

    private final Runnable afterUpdate;

    TaskUpdateProduct(Runnable afterUpdate) {
        this.afterUpdate = afterUpdate;
    }

    @Override
    protected Void doInBackground(Product... products) {
        Log.i("task", "update");
        return null;
    }
}
