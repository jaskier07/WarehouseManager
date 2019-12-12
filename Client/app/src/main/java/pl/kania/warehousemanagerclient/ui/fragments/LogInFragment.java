package pl.kania.warehousemanagerclient.ui.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.login.GoogleCredentials;
import pl.kania.warehousemanagerclient.model.login.LoggingMethod;
import pl.kania.warehousemanagerclient.model.login.LoginResult;
import pl.kania.warehousemanagerclient.model.login.UserCredentials;
import pl.kania.warehousemanagerclient.services.ConfigurationProvider;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.services.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

public class LogInFragment extends AbstractFragment {

    public static final String SHARED_PREFERENCES_NAME = "com.kania.warehousemanager.client";
    public static final String SHARED_PREFERENCES_TOKEN = SHARED_PREFERENCES_NAME + "token";
    public static final String SHARED_PREFERENCES_USER_LOGIN = SHARED_PREFERENCES_NAME + "userLogin";
    static final String SHARED_PREFERENCES_LOGGING_METHOD = SHARED_PREFERENCES_NAME + "loggingMethod";
    private static final int R_LOG_IN = 1;
    private static final int R_SIGN_IN = 2;
    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText loginEditTextSignIn;
    private EditText passwordEditTextSignIn;
    private TextView info;
    private GoogleSignInClient googleClientSignIn;
    private RestService restService;
    private ConfigurationProvider configurationProvider;

    public LogInFragment(SharedPreferences sharedPreferences, DatabaseManager db) {
        super(sharedPreferences, db);
        this.restService = new RestService(sharedPreferences, getContext());
        this.configurationProvider = new ConfigurationProvider(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.log_in_fragment, container, false);

        info = view.findViewById(R.id.log_in_info);
        info.setText("-");
        loginEditText = view.findViewById(R.id.loginValueLogIn);
        passwordEditText = view.findViewById(R.id.passwordValueLogIn);
        loginEditTextSignIn = view.findViewById(R.id.loginValueSignIn);
        passwordEditTextSignIn = view.findViewById(R.id.passwordValueSignIn);

        final String token = getSharedPreferences().getString(SHARED_PREFERENCES_TOKEN, null);
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(configurationProvider.getClientId())
                .build();
        googleClientSignIn = GoogleSignIn.getClient(getContext(), gso);

        if (token != null) {
            try {
                if (new RestService(getSharedPreferences(), getContext()).checkToken(token)) {
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
        FragmentLoader.load(new MenuFragment(getSharedPreferences(), getDb(), googleClientSignIn), getFragmentManager());
    }

    private void handleNotLoggedUser(View view) {
        info.setText("You are not logged");

        final Button buttonLogIn = view.findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(c -> getUserCredentialsFromForm(loginEditText, passwordEditText)
                .ifPresent(this::actionLogIn));

        final Button buttonSignIn = view.findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(c -> getUserCredentialsFromForm(loginEditTextSignIn, passwordEditTextSignIn)
                .ifPresent(this::actionSignIn));

        final SignInButton buttonGoogleLogIn = view.findViewById(R.id.buttonLogInGoogle);
        buttonGoogleLogIn.setOnClickListener(c -> actionLogInWithGoogle());

        final SignInButton buttonGooogleSignIn = view.findViewById(R.id.buttonSignInGoogle);
        buttonGooogleSignIn.setOnClickListener(c -> {
            Intent signInIntent = googleClientSignIn.getSignInIntent();
            startActivityForResult(signInIntent, R_SIGN_IN);
        });
    }

    private void actionSignIn(UserCredentials userCredentials) {
        try {
            LoginResult loginResult = restService.signIn(userCredentials);
            handleLoginResult(loginResult, LoggingMethod.DEFAULT);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("actionSignIn", "An error occured while signing in user", e);
            info.setText("An error occured while signing in");
        }
    }

    private void actionLogIn(UserCredentials userCredentials) {
        try {
            LoginResult loginResult = restService.exchangeCredentialsForToken(userCredentials);
            handleLoginResult(loginResult, LoggingMethod.DEFAULT);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("actionLogIn", "An error occured while exchanging user credentials for token", e);
            info.setText("An error occured while logging in");
        }
    }

    private void actionLogInWithGoogle() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null && account.getIdToken() != null) {
            try {
                final GoogleCredentials googleCredentials = new GoogleCredentials(account.getIdToken(), configurationProvider.getClientId(), configurationProvider.getClientSecret());
                final LoginResult loginResult = restService.logInWithGoogle(googleCredentials);
                handleLoginResult(loginResult, LoggingMethod.GOOGLE);
            } catch (InterruptedException | ExecutionException e) {
                Log.e("google loginEditText", "error", e);
                info.setText("Obtaining server token from Google token failed.");
            }
        } else {
            Intent signInIntent = googleClientSignIn.getSignInIntent();
            startActivityForResult(signInIntent, R_LOG_IN);
        }
    }

    private void handleLoginResult(LoginResult loginResult, LoggingMethod loggingMethod) {
        if (loginResult.getErrorMessage() == null) {
            getSharedPreferences().edit().putString(SHARED_PREFERENCES_USER_LOGIN, loginResult.getLogin()).commit();
            getSharedPreferences().edit().putString(SHARED_PREFERENCES_LOGGING_METHOD, loggingMethod.toString()).apply();
            saveToken(loginResult.getToken());
            loginEditText.setText("");
            loginEditTextSignIn.setText("");
            passwordEditText.setText("");
            passwordEditTextSignIn.setText("");
            handleLoggedUser();
        } else {
            info.setText(loginResult.getErrorMessage());
            if (loggingMethod == LoggingMethod.GOOGLE) {
                googleClientSignIn.signOut();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == R_LOG_IN) {
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleLogInWithGoogleResult(task);
        } else if (requestCode == R_SIGN_IN) {
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInWithGoogleResult(task);
        }
    }

    private void handleLogInWithGoogleResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                final GoogleCredentials googleCredentials = new GoogleCredentials(account.getIdToken(), configurationProvider.getClientId(), configurationProvider.getClientSecret());
                final LoginResult loginResult = restService.logInWithGoogle(googleCredentials);
                handleLoginResult(loginResult, LoggingMethod.GOOGLE);
            } else {
                info.setText("Exchanging Google token for app token failed.");
            }
        } catch (ApiException e) {
            Log.e("google loginEditText", "signInResult:failed code=" + e.getStatusCode(), e);
            info.setText("Obtaining Google token failed.");
        } catch (InterruptedException | ExecutionException e) {
            Log.e("google loginEditText", "error", e);
            info.setText("Obtaining Google token failed.");
        }
    }

    private void handleSignInWithGoogleResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                final GoogleCredentials googleCredentials = new GoogleCredentials(account.getIdToken(), configurationProvider.getClientId(), configurationProvider.getClientSecret());
                final LoginResult loginResult = restService.signInWithGoogle(googleCredentials);
                handleLoginResult(loginResult, LoggingMethod.GOOGLE);
            } else {
                info.setText("Exchanging Google token for app token failed.");
            }
        } catch (ApiException e) {
            Log.e("google sign in", "signInResult:failed code=" + e.getStatusCode(), e);
            info.setText("Obtaining Google token failed.");
        } catch (InterruptedException | ExecutionException e) {
            Log.e("google sign in", "error", e);
            info.setText("Obtaining Google token failed.");
        }
    }

    private void saveToken(String token) {
        if (!getSharedPreferences().edit().putString(SHARED_PREFERENCES_TOKEN, token).commit()) {
            info.setText("Saving token to preferences failed.");
        }
    }

    private Optional<UserCredentials> getUserCredentialsFromForm(EditText login, EditText password) {
        if (login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            info.setText("Provide both login and password");
            return Optional.empty();
        }

        return Optional.of(new UserCredentials(login.getText().toString(), password.getText().toString(), configurationProvider.getClientId(), configurationProvider.getClientSecret()));
    }

}
