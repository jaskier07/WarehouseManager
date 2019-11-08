package pl.kania.warehousemanagerclient.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.Product;
import pl.kania.warehousemanagerclient.model.ProductQuantity;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.ui.fragments.ProductListViewFragment;
import pl.kania.warehousemanagerclient.utils.TextParser;

public class ProductAdapter extends ArrayAdapter<Product> {

    private final RestService restService = new RestService();
    private final Activity activity;

    public ProductAdapter(@NonNull Context context, @LayoutRes int resource, List<Product> objects, Activity activity) {
        super(context, 0, objects);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_element_fragment, parent, false);
        }
        Product product = getItem(position);

        EditText manufacturerValue = convertView.findViewById(R.id.manufacturerValue);
        manufacturerValue.setText(product.getManufacturerName());
        EditText modelValue = convertView.findViewById(R.id.modelValue);
        modelValue.setText(product.getModelName());
        EditText priceValue = convertView.findViewById(R.id.priceValue);
        priceValue.setText(getNullSafeNumberValue(product.getPrice()));
        TextView quantityValue = convertView.findViewById(R.id.quantityValue);
        quantityValue.setText(getNullSafeNumberValue(product.getQuantity()));
        TextView idValue = convertView.findViewById(R.id.idValue);
        idValue.setText(getNullSafeNumberValue(product.getId()));
        EditText increaseBy = convertView.findViewById(R.id.increaseByValue);
        EditText decreaseBy = convertView.findViewById(R.id.decreaseByValue);

        Button update = convertView.findViewById(R.id.buttonUpdate);
        update.setOnClickListener(c -> restService.updateProduct(Product.builder()
                .id(TextParser.parseLong(idValue))
                .manufacturerName(TextParser.getText(manufacturerValue))
                .modelName(TextParser.getText(modelValue))
                .price(TextParser.parseDouble(priceValue))
                .quantity(TextParser.parseInt(quantityValue))
                .build(), this::updateArrayAdapter));
        Button delete = convertView.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(c -> restService.deleteProduct(TextParser.parseLong(idValue), this::updateArrayAdapter));
        Button increase = convertView.findViewById(R.id.buttonIncrease);
        increase.setOnClickListener(c -> restService.increaseProductQuantity(new ProductQuantity(TextParser.parseLong(idValue), TextParser.parseInt(increaseBy)), this::updateArrayAdapter));
        Button decrease = convertView.findViewById(R.id.buttonDecrease);
        decrease.setOnClickListener(c -> restService.decreaseProductQuantity(new ProductQuantity(TextParser.parseLong(idValue), TextParser.parseInt(decreaseBy)), this::updateArrayAdapter));

        return convertView;
    }

    private void updateArrayAdapter() {
        updateList(activity);
    }

    private String getNullSafeNumberValue(Number number) {
        if (number == null) {
            return "";
        }
        return number.toString();
    }

    public Consumer<List<Product>> updateList(Activity activity) {
        return r -> activity.runOnUiThread(() -> {
            clear();
            addAll(r);
        });
    }
}
