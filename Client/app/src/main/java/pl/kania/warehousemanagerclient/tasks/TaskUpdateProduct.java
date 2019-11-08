package pl.kania.warehousemanagerclient.tasks;

import pl.kania.warehousemanagerclient.Product;

class TaskUpdateProduct extends AbstractRestTask<Product> {

    private final Runnable afterUpdate;

    TaskUpdateProduct(Runnable afterUpdate) {
        this.afterUpdate = afterUpdate;
    }

    @Override
    protected Void doInBackground(Product... products) {
        return null;
    }
}
