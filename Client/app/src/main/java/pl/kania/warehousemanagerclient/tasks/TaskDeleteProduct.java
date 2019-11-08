package pl.kania.warehousemanagerclient.tasks;

import android.util.Log;

class TaskDeleteProduct extends AbstractRestTask<Long> {

    private final Runnable afterDelete;

    TaskDeleteProduct(Runnable afterDelete) {
        this.afterDelete = afterDelete;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        Log.i("task", "delete");
        return null;
    }
}
