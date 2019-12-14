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
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnClient;
import pl.kania.warehousemanagerclient.model.login.LoggingMethod;
import pl.kania.warehousemanagerclient.services.ConfigurationProvider;
import pl.kania.warehousemanagerclient.services.SynchronizationService;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.services.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_LOGGING_METHOD;
import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_TOKEN;
import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_USER_LOGIN;

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
        final String userLogin = getSharedPreferences().getString(SHARED_PREFERENCES_USER_LOGIN, "unlogged user!");
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
        getSharedPreferences().edit().putString(SHARED_PREFERENCES_TOKEN, null).commit();
        Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_LONG).show();

        String loggingMethod = getSharedPreferences().getString(SHARED_PREFERENCES_LOGGING_METHOD, null);
        if (loggingMethod != null) {
            if (LoggingMethod.valueOf(loggingMethod) == LoggingMethod.GOOGLE) {
                googleClientSignIn.signOut().addOnCompleteListener(a -> Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show());
            }
        }
        FragmentLoader.load(new LogInFragment(getSharedPreferences(), getDb()), getFragmentManager());
    }
}
