package pl.kania.warehousemanagerclient;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pl.kania.warehousemanagerclient.ui.fragments.LogInFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LogInFragment())
                    .commitNow();
        }
    }
}
