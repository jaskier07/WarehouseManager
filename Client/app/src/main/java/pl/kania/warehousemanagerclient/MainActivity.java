package pl.kania.warehousemanagerclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.ui.fragments.LogInFragment;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_NAME;
import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_TOKEN;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private DatabaseManager db;

    @Override
    public void onBackPressed() {
        if (sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, null) != null) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        db = new DatabaseManager(getApplicationContext());
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LogInFragment(sharedPreferences, db))
                    .commitNow();
        }
    }
}
