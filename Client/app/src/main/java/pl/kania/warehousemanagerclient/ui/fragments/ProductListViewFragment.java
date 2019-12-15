package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import lombok.Getter;
import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.ui.ProductAdapter;

public class ProductListViewFragment extends AbstractFragment {

    @Getter
    private ProductAdapter productAdapter;

    public ProductListViewFragment(SharedPreferences sharedPreferences, DatabaseManager db) {
        super(sharedPreferences, db);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ListView view = (ListView)inflater.inflate(R.layout.product_list_view_fragment, container, false);
        productAdapter = new ProductAdapter(view.getContext(), getActivity(), getDb());
        view.setAdapter(productAdapter);
        final List<ProductClient> products = getDb().selectAllNonRemovedProducts();
        products.sort(ProductClient::compareTo);
        productAdapter.updateList(getActivity()).accept(products);


        return view;
    }
}
