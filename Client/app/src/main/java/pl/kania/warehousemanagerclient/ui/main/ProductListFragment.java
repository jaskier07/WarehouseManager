package pl.kania.warehousemanagerclient.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.kania.warehousemanagerclient.Product;
import pl.kania.warehousemanagerclient.ProductAdapter;
import pl.kania.warehousemanagerclient.R;

public class ProductListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView view = (ListView)inflater.inflate(R.layout.listview_product, container, false);

        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "samsung", "galaxy", 25.0, 3));
        products.add(new Product(2L, "huawei", "P9", 30.0, 4));

        ProductAdapter productAdapter = new ProductAdapter(view.getContext(), R.layout.listview_product, products);
        view.setAdapter(productAdapter);

        return view;
    }
}
