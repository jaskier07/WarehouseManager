package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.Product;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;
import pl.kania.warehousemanagerclient.utils.TextParser;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_NAME;

public class AddProductFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_fragment, container, false);
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        EditText manufacturer = view.findViewById(R.id.manufacturerValueAdd);
        EditText model = view.findViewById(R.id.modelValueAdd);
        EditText price = view.findViewById(R.id.priceValueAdd);
        Button addNewProduct = view.findViewById(R.id.buttonAddNewProduct);
        addNewProduct.setOnClickListener(c -> {
            if (TextParser.isValidNumber(price)) {
                Product product = Product.builder()
                        .manufacturerName(manufacturer.getText().toString())
                        .modelName(model.getText().toString())
                        .price(TextParser.parseDouble(price))
                        .build();
                new RestService(sharedPreferences).addNewProduct(product, () -> FragmentLoader.load(new ProductListViewFragment(), getFragmentManager(), sharedPreferences));
            } else {
                showInfoInvalidNumber();
            }
        });

        return view;
    }

    private void showInfoInvalidNumber() {
        Toast.makeText(getContext().getApplicationContext(), "Enter valid number (> 0)", Toast.LENGTH_LONG).show();
    }
}
