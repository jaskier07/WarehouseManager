package pl.kania.warehousemanagerclient.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.kania.warehousemanagerclient.R;

public class LogInFragment extends Fragment {

    private ProductListFragment productListFragment = new ProductListFragment();

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_in_fragment, container, false);
        Button buttonLogIn = view.findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(v -> loadFragment(productListFragment));
        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
