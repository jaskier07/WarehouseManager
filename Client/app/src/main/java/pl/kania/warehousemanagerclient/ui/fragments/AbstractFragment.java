package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;

@AllArgsConstructor
@Getter
public abstract class AbstractFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private DatabaseManager db;
}
