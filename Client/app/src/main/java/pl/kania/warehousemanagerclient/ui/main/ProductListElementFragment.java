package pl.kania.warehousemanagerclient.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import pl.kania.warehousemanagerclient.Product;
import pl.kania.warehousemanagerclient.ProductAdapter;
import pl.kania.warehousemanagerclient.ProductQuantity;
import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.FragmentLoader;
import pl.kania.warehousemanagerclient.utils.TextParser;

public class ProductListElementFragment extends Fragment {

    private RestService restService = new RestService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_list_element_fragment, container);

        TextView id = view.findViewById(R.id.idValue);
        TextView quantity = view.findViewById(R.id.quantityValue);
        EditText manufacturer = view.findViewById(R.id.manufacturerValue);
        EditText model = view.findViewById(R.id.modelValue);
        EditText price = view.findViewById(R.id.priceValue);
        EditText increaseBy = view.findViewById(R.id.increaseByValue);
        EditText decreaseBy = view.findViewById(R.id.decreaseByValue);

        Button update = view.findViewById(R.id.buttonUpdate);
        update.setOnClickListener(c -> restService.updateProduct(Product.builder()
                .id(TextParser.parseLong(id))
                .manufacturerName(TextParser.getText(manufacturer))
                .modelName(TextParser.getText(model))
                .price(TextParser.parseDouble(price))
                .quantity(TextParser.parseInt(quantity))
                .build(), this::updateArrayAdapter));
        Button delete = view.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(c -> restService.deleteProduct(TextParser.parseLong(id), this::updateArrayAdapter));
        Button increase = view.findViewById(R.id.buttonIncrease);
        increase.setOnClickListener(c -> restService.increaseProductQuantity(new ProductQuantity(TextParser.parseLong(id), TextParser.parseInt(increaseBy)), this::updateArrayAdapter));
        Button decrease = view.findViewById(R.id.buttonDecrease);
        decrease.setOnClickListener(c -> restService.decreaseProductQuantity(new ProductQuantity(TextParser.parseLong(id), TextParser.parseInt(decreaseBy)), this::updateArrayAdapter));

        return view;
    }

    private void updateArrayAdapter() {
        getProductAdapterFromParent().updateList(getActivity());
    }

    private ProductAdapter getProductAdapterFromParent() {
        ProductListViewFragment parentFragment = (ProductListViewFragment) getParentFragment();
        return parentFragment.getProductAdapter();
    }
}
