package pl.kania.warehousemanagerclient.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;

public class MenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        Button addProduct = view.findViewById(R.id.buttonGoToAddProduct);
        addProduct.setOnClickListener(k -> FragmentLoader.load(new AddProductFragment(), getFragmentManager()));
        Button productList = view.findViewById(R.id.buttonGoToProductList);
        productList.setOnClickListener(k -> FragmentLoader.load(new ProductListViewFragment(), getFragmentManager()));

        return view;
    }
}
