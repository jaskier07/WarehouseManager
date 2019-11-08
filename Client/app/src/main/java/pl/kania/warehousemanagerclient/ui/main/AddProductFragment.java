package pl.kania.warehousemanagerclient.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.kania.warehousemanagerclient.Product;
import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

public class AddProductFragment extends Fragment {

    private RestService restService = new RestService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_fragment, container, false);

        EditText manufacturer = view.findViewById(R.id.manufacturerValueAdd);
        EditText model = view.findViewById(R.id.modelValueAdd);
        EditText price = view.findViewById(R.id.priceValueAdd);
        Button addNewProduct = view.findViewById(R.id.buttonAddNewProduct);
        addNewProduct.setOnClickListener(c -> {
            Product product = Product.builder()
                    .manufacturerName(manufacturer.getText().toString())
                    .modelName(model.getText().toString())
                    .price(Double.valueOf(price.getText().toString()))
                    .build();
            restService.addNewProduct(product, () -> FragmentLoader.load(new ProductListViewFragment(), getFragmentManager()));
        });

        return view;
    }
}
