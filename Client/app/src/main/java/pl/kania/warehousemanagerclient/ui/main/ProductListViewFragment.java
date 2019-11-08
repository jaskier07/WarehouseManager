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

import pl.kania.warehousemanagerclient.ProductAdapter;
import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.RestService;

public class ProductListViewFragment extends Fragment {

    private RestService restService = new RestService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView view = (ListView)inflater.inflate(R.layout.product_list_view_fragment, container, false);
        ProductAdapter productAdapter = new ProductAdapter(view.getContext(), R.layout.product_list_view_fragment, new ArrayList<>());
        view.setAdapter(productAdapter);

        restService.getAllProducts(r -> getActivity().runOnUiThread(() -> productAdapter.addAll(r)));

        return view;
    }
}
