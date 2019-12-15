package pl.kania.warehousemanagerclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pl.kania.warehousemanagerclient.model.SharedPreferencesKey;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.ui.fragments.LogInFragment;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private DatabaseManager db;

    @Override
    public void onBackPressed() {
        if (sharedPreferences.getString(SharedPreferencesKey.TOKEN.getKey(), null) != null) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SharedPreferencesKey.SHARED_PREFERENCES_NAME.getKey(), Context.MODE_PRIVATE);
        db = new DatabaseManager(getApplicationContext());
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LogInFragment(sharedPreferences, db))
                    .commitNow();
        }
    }
}
