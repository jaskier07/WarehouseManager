package pl.kania.warehousemanagerclient.tasks;

class TaskDeleteProduct extends AbstractRestTask<Long> {

    private final Runnable afterDelete;

    TaskDeleteProduct(Runnable afterDelete) {
        this.afterDelete = afterDelete;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        return null;
    }
}
