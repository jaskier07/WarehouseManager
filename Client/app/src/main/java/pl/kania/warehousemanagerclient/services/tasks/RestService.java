package pl.kania.warehousemanagerclient.services.tasks;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnClient;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanagerclient.model.dto.ProductQuantity;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;
import pl.kania.warehousemanagerclient.model.login.GoogleCredentials;
import pl.kania.warehousemanagerclient.model.login.LoginResult;
import pl.kania.warehousemanagerclient.model.login.UserCredentials;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_TOKEN;

public class RestService {

    private SharedPreferences sharedPreferences;
    private Context context;

    public RestService(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
    }

    public void synchronize(DataToSyncOnServer dataToSyncOnServer, Consumer<DataToSyncOnClient> afterSync) {
        new TaskSynchronize(getToken(), context, afterSync).execute(dataToSyncOnServer);
    }

    public void getAllProducts(Consumer<List<ProductClient>> updateProducts) {
        new TaskGetAllProducts(getToken(), updateProducts, context).execute();
    }

    public void addNewProduct(ProductClient product, Runnable afterAdd) {
        new TaskAddProduct(getToken(), afterAdd, context).execute(product);
    }

    public void deleteProduct(Long productId, Runnable afterDelete, Runnable onFailure) {
        new TaskDeleteProduct(getToken(), afterDelete, onFailure, context).execute(productId);
    }

    public void decreaseProductQuantity(ProductQuantity productQuantity, Runnable afterDecrease, Consumer<String> onFailed) {
        new TaskDecreaseProductQuantity(getToken(), afterDecrease, onFailed, context).execute(productQuantity);
    }

    public void increaseProductQuantity(ProductQuantity productQuantity, Runnable afterIncrease) {
        new TaskIncreaseProductQuantity(getToken(), afterIncrease, context).execute(productQuantity);
    }

    public void updateProduct(ProductClient product, Runnable afterUpdate) {
        new TaskUpdateProduct(getToken(), afterUpdate, context).execute(product);
    }

    public LoginResult logInWithGoogle(GoogleCredentials credentials) throws ExecutionException, InterruptedException {
       return new TaskLogInWithGoogle(context).execute(credentials).get();
    }

    public LoginResult signInWithGoogle(GoogleCredentials credentials) throws ExecutionException, InterruptedException {
        return new TaskSignInWithGoogle(context).execute(credentials).get();
    }

    public boolean checkToken(String token) throws ExecutionException, InterruptedException {
        return new TaskCheckToken(context).execute(token).get();
    }

    public LoginResult exchangeCredentialsForToken(UserCredentials userCredentials) throws ExecutionException, InterruptedException {
        return new TaskLogIn(context).execute(userCredentials).get();
    }

    private String getToken() {
        return sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, "");
    }

    public LoginResult signIn(UserCredentials userCredentials) throws ExecutionException, InterruptedException {
        return new TaskSignIn(context).execute(userCredentials).get();
    }
}
