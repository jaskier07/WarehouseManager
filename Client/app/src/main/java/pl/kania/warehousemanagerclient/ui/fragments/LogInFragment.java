package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.ExecutionException;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.LoginResult;
import pl.kania.warehousemanagerclient.model.UserCredentials;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

public class LogInFragment extends Fragment {

    public static final String SHARED_PREFERENCES_NAME = "com.kania.warehousemanager.client";
    public static final String SHARED_PREFERENCES_TOKEN = SHARED_PREFERENCES_NAME + "token";
    static final String SHARED_PREFERENCES_USER_LOGIN = SHARED_PREFERENCES_NAME + "userLogin";
    private static final int R_SIGN_IN = 1;
    private SharedPreferences sharedPreferences;
    private TextView info;
    private String clientId;
    private String clientSecret;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.log_in_fragment, container, false);

        info = view.findViewById(R.id.log_in_info);
        info.setText("-");

        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        clientId = getContext().getString(R.string.client_id);
        clientSecret =  getContext().getString(R.string.client_secret);
        final String token = sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, null);

        if (token != null) {
            try {
                if (new RestService(sharedPreferences).checkToken(token)) {
                    handleLoggedUser();
                    return view;
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e("checkToken", "Check token failed.", e);
                info.setText("Failed to check if user is logged in. Details: " + e.getMessage());
            }
        }

        handleNotLoggedUser(view);

        return view;
    }

    private void handleLoggedUser() {
        info.setText("User is loggged.");
        FragmentLoader.load(new MenuFragment(), getFragmentManager());
    }

    private void handleNotLoggedUser(View view) {
        final Button buttonLogIn = view.findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(v -> actionLogIn(view));

        final Button buttonSignIn = view.findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(c -> actionSignIn(view));

        final SignInButton buttonGoogle = view.findViewById(R.id.buttonLogInGoogle);
        buttonGoogle.setOnClickListener(c -> actionLogInWithGoogle());

        info.setText("You are not logged");
    }

    private void actionSignIn(View view) {
        final EditText login = view.findViewById(R.id.loginValueSignIn);
        final EditText password = view.findViewById(R.id.passwordValueSignIn);

        if (login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            info.setText("Provide both login and password");
            return;
        }

        final UserCredentials userCredentials = new UserCredentials(login.getText().toString(), password.getText().toString(), clientId, clientSecret);
        try {
            LoginResult loginResult = new RestService(sharedPreferences).signIn(userCredentials);
            if (loginResult.getErrorMessage() == null) {
                sharedPreferences.edit().putString(SHARED_PREFERENCES_USER_LOGIN, loginResult.getLogin()).commit();
                saveToken(loginResult.getToken());
                handleLoggedUser();
            } else {
                info.setText(loginResult.getErrorMessage());
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e("actionSignIn", "An error occured while signing in user", e);
            info.setText("An error occured while signing in");
        }
    }

    private void actionLogIn(View view) {
        final EditText login = view.findViewById(R.id.loginValueLogIn);
        final EditText password = view.findViewById(R.id.passwordValueLogIn);
        if (login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            info.setText("Provide both login and password");
            return;
        }
        final UserCredentials userCredentials = new UserCredentials(login.getText().toString(), password.getText().toString(), clientId, clientSecret);
        try {
            LoginResult loginResult = new RestService(sharedPreferences).exchangeCredentialsForToken(userCredentials);
            if (loginResult.getErrorMessage() == null) {
                sharedPreferences.edit().putString(SHARED_PREFERENCES_USER_LOGIN, loginResult.getLogin()).commit();
                saveToken(loginResult.getToken());
                handleLoggedUser();
            } else {
                info.setText(loginResult.getErrorMessage());
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e("actionLogIn", "An error occured while exchanging user credentials for token", e);
            info.setText("An error occured while logging in");
        }
    }

    private void actionLogInWithGoogle() {
        final String clientId = getContext().getString(R.string.google_client_id);
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
//                .requestScopes(new Scope("openid"))
                .requestIdToken(clientId)
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(getContext(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            client.signOut().addOnCompleteListener(a -> Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show());
        } else {
            Intent signInIntent = client.getSignInIntent();
            startActivityForResult(signInIntent, R_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == R_SIGN_IN) {
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                new RestService(sharedPreferences).exchangeGoogleTokenForAppToken(account.getIdToken(), this::saveToken);
            } else {
                info.setText("Exchanging Google token for app token failed.");
            }
        } catch (ApiException e) {
            Log.e(ContentValues.TAG, "signInResult:failed code=" + e.getStatusCode(), e);
            info.setText("Obtaining Google token failed.");
        }
    }

    private void saveToken(String token) {
        if (!sharedPreferences.edit().putString(SHARED_PREFERENCES_TOKEN, token).commit()) {
            info.setText("Saving token to preferences failed.");
        }
    }


}
