package pl.kania.warehousemanagerclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pl.kania.warehousemanagerclient.ui.main.LogInFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LogInFragment.newInstance())
                    .commitNow();
        }
    }
}
