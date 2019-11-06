package pl.kania.warehousemanagerclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.List;

import pl.kania.warehousemanagerclient.ui.main.ProductListFragment;

public class ProductAdapter extends ArrayAdapter<Product> {
    public ProductAdapter(@NonNull Context context, @LayoutRes int resource, List<Product> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_element_fragment, parent, false);
        }
        Product product = getItem(position);
        EditText manufacturerValue = listItemView.findViewById(R.id.manufacturerValue);
        manufacturerValue.setText(product.getManufacturerName());
        return listItemView;
    }
}
