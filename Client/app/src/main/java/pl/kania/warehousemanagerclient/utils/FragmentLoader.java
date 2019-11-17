package pl.kania.warehousemanagerclient.utils;

import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.ui.fragments.LogInFragment;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_TOKEN;

public class FragmentLoader {

    public static void load(Fragment fragment, FragmentManager fragmentManager, SharedPreferences sharedPreferences) {
        if (fragment.getId() != R.layout.main_activity) {
            if (sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, null) != null) {
                load(fragment, fragmentManager);
            } else {
                load(new LogInFragment(), fragmentManager);
            }
        } else {
            load(fragment, fragmentManager);
        }
    }

    private static void load(Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
