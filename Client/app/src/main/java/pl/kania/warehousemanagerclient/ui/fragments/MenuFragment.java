package pl.kania.warehousemanagerclient.ui.fragments;

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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.List;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.SharedPreferencesKey;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnClient;
import pl.kania.warehousemanagerclient.model.login.LoggingMethod;
import pl.kania.warehousemanagerclient.services.ConfigurationProvider;
import pl.kania.warehousemanagerclient.services.SynchronizationService;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.services.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;


public class MenuFragment extends AbstractFragment {

    private GoogleSignInClient googleClientSignIn;
    private RestService restService;
    private SynchronizationService synchronizationService;
    private TextView textView;

    MenuFragment(SharedPreferences sharedPreferences, DatabaseManager db, GoogleSignInClient googleClientSignIn) {
        super(sharedPreferences, db);
        this.googleClientSignIn = googleClientSignIn;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        restService = new RestService(getSharedPreferences(), getContext());
        synchronizationService = new SynchronizationService(getDb(), new ConfigurationProvider(getContext()));

        final Button addProduct = view.findViewById(R.id.buttonGoToAddProduct);
        addProduct.setOnClickListener(k -> FragmentLoader.load(new AddProductFragment(getSharedPreferences(), getDb()), getFragmentManager()));
        final Button productList = view.findViewById(R.id.buttonGoToProductList);
        productList.setOnClickListener(k -> FragmentLoader.load(new ProductListViewFragment(getSharedPreferences(), getDb()), getFragmentManager()));
        final String userLogin = getSharedPreferences().getString(SharedPreferencesKey.USER_LOGIN.getKey(), "unlogged user!");
        final TextView loggedAs = view.findViewById(R.id.textViewLoggedAs);
        loggedAs.setText("You are logged as " + userLogin);
        final Button logout = view.findViewById(R.id.buttonLogout);
        logout.setOnClickListener(c -> actionLogout());
        textView = view.findViewById(R.id.sync_info);
        final Button sync = view.findViewById(R.id.buttonSync);
        sync.setOnClickListener(c -> actionSync(textView));

        return view;
}

    private void actionSync(TextView textView) {
        restService.synchronize(synchronizationService.getDataToSynchronizeOnServer(), this::afterSync);
    }

    private void afterSync(DataToSyncOnClient dataToSyncOnClient) {
        List<String> messages = synchronizationService.updateDataOnDevice(dataToSyncOnClient);
        String formattedMessages = formatMessages(messages);
        getActivity().runOnUiThread(() -> textView.setText(formattedMessages)); // TODO pretty formatting
    }

    private String formatMessages(List<String> messages) {
        if (messages.isEmpty()) {
            return "Nothing special happened.";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : messages) {
            sb.append("* ");
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void actionLogout() {
        getSharedPreferences().edit().putString(SharedPreferencesKey.TOKEN.getKey(), null).commit();
        getSharedPreferences().edit().putString(SharedPreferencesKey.USER_IS_MANAGER.getKey(), null).commit();
        Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_LONG).show();

        String loggingMethod = getSharedPreferences().getString(SharedPreferencesKey.LOGGING_METHOD.getKey(), null);
        if (loggingMethod != null) {
            if (LoggingMethod.valueOf(loggingMethod) == LoggingMethod.GOOGLE) {
                googleClientSignIn.signOut().addOnCompleteListener(a -> Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show());
            }
        }
        FragmentLoader.load(new LogInFragment(getSharedPreferences(), getDb()), getFragmentManager());
    }
}
