package pl.kania.warehousemanagerclient.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

public class LogInFragment extends Fragment {

    private MenuFragment menu_fragment = new MenuFragment();

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_in_fragment, container, false);
        Button buttonLogIn = view.findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(v -> FragmentLoader.load(menu_fragment, getFragmentManager()));
        return view;
    }

}
