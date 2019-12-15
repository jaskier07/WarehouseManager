package pl.kania.warehousemanagerclient.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.SharedPreferencesKey;
import pl.kania.warehousemanagerclient.ui.fragments.AbstractFragment;
import pl.kania.warehousemanagerclient.ui.fragments.LogInFragment;

public class FragmentLoader {

    public static void load(AbstractFragment fragment, FragmentManager fragmentManager) {
        if (fragment.getId() != R.layout.main_activity) {
            if (fragment.getSharedPreferences().getString(SharedPreferencesKey.TOKEN.getKey(), null) != null) {
                loadFragment(fragment, fragmentManager);
            } else {
                loadFragment(new LogInFragment(fragment.getSharedPreferences(), fragment.getDb()), fragmentManager);
            }
        } else {
            loadFragment(fragment, fragmentManager);
        }
    }

    private static void loadFragment(Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
