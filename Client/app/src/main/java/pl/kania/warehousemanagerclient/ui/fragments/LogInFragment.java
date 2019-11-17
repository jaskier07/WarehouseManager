package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.log_in_fragment, container, false);
        info = view.findViewById(R.id.log_in_info);
        info.setText("-");
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
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
        buttonLogIn.setOnClickListener(v -> signIn(view));
        final Button buttonSignIn = view.findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(c -> {
            Uri loginUri = Uri.parse(RestService.BASE_URI_LOGIN);
            Intent intent = new Intent(Intent.ACTION_VIEW, loginUri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });
        final SignInButton buttonGoogle = view.findViewById(R.id.buttonLogInGoogle);
        buttonGoogle.setOnClickListener(c -> signInWithGoogle());
        info.setText("You are not logged");
    }

    private void signIn(View view) {
        final EditText login = view.findViewById(R.id.loginValueLogIn);
        final EditText password = view.findViewById(R.id.passwordValueLogIn);
        if (login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            info.setText("Provide both login and password");
            return;
        }
        final String clientId = getContext().getString(R.string.client_id);
        final String clientSecret = getContext().getString(R.string.client_secret);
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
            Log.e("signIn", "An error occured while exchanging user credentials for token", e);
            info.setText("An error occured while logging in");
        }
    }

    private void signInWithGoogle() {
        final String clientId = getContext().getString(R.string.google_client_id);
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
//                .requestScopes(new Scope("openid"))
                .requestServerAuthCode(clientId)
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(getContext(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            // TODO
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
