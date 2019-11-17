package pl.kania.warehousemanagerclient.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import lombok.Getter;
import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.ui.ProductAdapter;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_NAME;

public class ProductListViewFragment extends Fragment {

    @Getter
    private ProductAdapter productAdapter;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ListView view = (ListView)inflater.inflate(R.layout.product_list_view_fragment, container, false);
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        productAdapter = new ProductAdapter(view.getContext(), getActivity());
        view.setAdapter(productAdapter);

        new RestService(sharedPreferences).getAllProducts(productAdapter.updateList(getActivity()));

        return view;
    }
}
