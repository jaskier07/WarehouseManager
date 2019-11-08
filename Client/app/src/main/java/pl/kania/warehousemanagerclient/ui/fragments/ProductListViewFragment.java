package pl.kania.warehousemanagerclient.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import lombok.Getter;
import pl.kania.warehousemanagerclient.ui.ProductAdapter;
import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.tasks.RestService;

public class ProductListViewFragment extends Fragment {

    private final RestService restService = new RestService();
    @Getter
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ListView view = (ListView)inflater.inflate(R.layout.product_list_view_fragment, container, false);
        productAdapter = new ProductAdapter(view.getContext(), getActivity());
        view.setAdapter(productAdapter);

        restService.getAllProducts(productAdapter.updateList(getActivity()));

        return view;
    }
}
