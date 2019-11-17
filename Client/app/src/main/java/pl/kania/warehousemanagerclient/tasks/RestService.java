package pl.kania.warehousemanagerclient.tasks;

import android.content.SharedPreferences;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.model.GoogleCredentials;
import pl.kania.warehousemanagerclient.model.LoginResult;
import pl.kania.warehousemanagerclient.model.Product;
import pl.kania.warehousemanagerclient.model.ProductQuantity;
import pl.kania.warehousemanagerclient.model.UserCredentials;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_TOKEN;

public class RestService {

    static final String BASE_URI = "http://c13cf18d.ngrok.io";
    static final String BASE_URI_PRODUCT = BASE_URI + "/product";
    public static final String BASE_URI_LOGIN = BASE_URI + "/login";
    private SharedPreferences sharedPreferences;

    public RestService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void getAllProducts(Consumer<List<Product>> updateProducts) {
        new TaskGetAllProducts(getToken(), updateProducts).execute();
    }

    public void addNewProduct(Product product, Runnable afterAdd) {
        new TaskAddProduct(getToken(), afterAdd).execute(product);
    }

    public void deleteProduct(Long productId, Runnable afterDelete, Runnable onFailure) {
        new TaskDeleteProduct(getToken(), afterDelete, onFailure).execute(productId);
    }

    public void decreaseProductQuantity(ProductQuantity productQuantity, Runnable afterDecrease) {
        new TaskDecreaseProductQuantity(getToken(), afterDecrease).execute(productQuantity);
    }

    public void increaseProductQuantity(ProductQuantity productQuantity, Runnable afterIncrease) {
        new TaskIncreaseProductQuantity(getToken(), afterIncrease).execute(productQuantity);
    }

    public void updateProduct(Product product, Runnable afterUpdate) {
        new TaskUpdateProduct(getToken(), afterUpdate).execute(product);
    }

    public LoginResult logInWithGoogle(GoogleCredentials credentials) throws ExecutionException, InterruptedException {
       return new TaskLogInWithGoogle().execute(credentials).get();
    }

    public LoginResult signInWithGoogle(GoogleCredentials credentials) throws ExecutionException, InterruptedException {
        return new TaskSignInWithGoogle().execute(credentials).get();
    }

    public boolean checkToken(String token) throws ExecutionException, InterruptedException {
        return new TaskCheckToken().execute(token).get();
    }

    public LoginResult exchangeCredentialsForToken(UserCredentials userCredentials) throws ExecutionException, InterruptedException {
        return new TaskLogIn().execute(userCredentials).get();
    }

    private String getToken() {
        return sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, "");
    }

    public LoginResult signIn(UserCredentials userCredentials) throws ExecutionException, InterruptedException {
        return new TaskSignIn().execute(userCredentials).get();
    }
}
