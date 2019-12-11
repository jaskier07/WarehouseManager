package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.login.LoggingMethod;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_LOGGING_METHOD;
import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_NAME;
import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_TOKEN;
import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_USER_LOGIN;

public class MenuFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleClientSignIn;

    MenuFragment(GoogleSignInClient googleClientSignIn) {
        this.googleClientSignIn = googleClientSignIn;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        final Button addProduct = view.findViewById(R.id.buttonGoToAddProduct);
        addProduct.setOnClickListener(k -> FragmentLoader.load(new AddProductFragment(), getFragmentManager(), sharedPreferences));
        final Button productList = view.findViewById(R.id.buttonGoToProductList);
        productList.setOnClickListener(k -> FragmentLoader.load(new ProductListViewFragment(), getFragmentManager(), sharedPreferences));
        final String userLogin = sharedPreferences.getString(SHARED_PREFERENCES_USER_LOGIN, "unlogged user!");
        final TextView loggedAs = view.findViewById(R.id.textViewLoggedAs);
        loggedAs.setText("You are logged as " + userLogin);
        final Button logout = view.findViewById(R.id.buttonLogout);
        logout.setOnClickListener(c -> actionLogout());

        return view;

}

    private void actionLogout() {
        sharedPreferences.edit().putString(SHARED_PREFERENCES_TOKEN, null).commit();
        Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_LONG).show();

        String loggingMethod = sharedPreferences.getString(SHARED_PREFERENCES_LOGGING_METHOD, null);
        if (loggingMethod != null) {
            if (LoggingMethod.valueOf(loggingMethod) == LoggingMethod.GOOGLE) {
                googleClientSignIn.signOut().addOnCompleteListener(a -> Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show());
            }
        }
        FragmentLoader.load(new LogInFragment(), getFragmentManager(), sharedPreferences);
    }
}
