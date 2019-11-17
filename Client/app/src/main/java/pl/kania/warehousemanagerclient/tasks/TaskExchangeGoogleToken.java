package pl.kania.warehousemanagerclient.tasks;

import java.util.function.Consumer;

class TaskExchangeGoogleToken extends AbstractRestTask<String, String> {
    private final Consumer<String> afterTokenObtaining;

    TaskExchangeGoogleToken(Consumer<String> afterTokenObtaining) {
       this.afterTokenObtaining = afterTokenObtaining;
    }

    @Override
    protected String doInBackground(String... strings) {

        // TODO
        return null;
    }
}
