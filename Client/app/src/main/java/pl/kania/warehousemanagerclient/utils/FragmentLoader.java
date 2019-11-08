package pl.kania.warehousemanagerclient.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import pl.kania.warehousemanagerclient.R;

public class FragmentLoader {

    public static void load(Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
