package pl.kania.warehousemanagerclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import pl.kania.warehousemanagerclient.R;
import pl.kania.warehousemanagerclient.model.Product;
import pl.kania.warehousemanagerclient.model.ProductQuantity;
import pl.kania.warehousemanagerclient.tasks.RestService;
import pl.kania.warehousemanagerclient.utils.TextParser;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_NAME;
import static pl.kania.warehousemanagerclient.utils.TextParser.getValidDoubleValue;
import static pl.kania.warehousemanagerclient.utils.TextParser.getValidIntegerValue;

public class ProductAdapter extends ArrayAdapter<Product> {

    private final Activity activity;
    private SharedPreferences sharedPreferences;

    public ProductAdapter(@NonNull Context context, Activity activity) {
        super(context, 0, new ArrayList<>());
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_element_fragment, parent, false);
        }
        Product product = getItem(position);
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

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
        update.setOnClickListener(c -> new RestService(sharedPreferences).updateProduct(
                Product.builder()
                        .id(TextParser.parseLong(idValue))
                        .manufacturerName(TextParser.getText(manufacturerValue))
                        .modelName(TextParser.getText(modelValue))
                        .price(getValidDoubleValue(priceValue, this::showInfoInvalidNumber))
                        .quantity(TextParser.parseInt(quantityValue))
                        .build(), this::updateArrayAdapter));
        Button delete = convertView.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(c -> new RestService(sharedPreferences).deleteProduct(TextParser.parseLong(idValue), this::updateArrayAdapter,
                this::showInfoNoPermission));
        Button increase = convertView.findViewById(R.id.buttonIncrease);
        increase.setOnClickListener(c -> new RestService(sharedPreferences).increaseProductQuantity(new ProductQuantity(TextParser.parseLong(idValue),
                getValidIntegerValue(increaseBy, this::showInfoInvalidNumber)), this::updateArrayAdapter));
        Button decrease = convertView.findViewById(R.id.buttonDecrease);
        decrease.setOnClickListener(c -> new RestService(sharedPreferences).decreaseProductQuantity(new ProductQuantity(TextParser.parseLong(idValue),
                getValidIntegerValue(decreaseBy, this::showInfoInvalidNumber)), this::updateArrayAdapter));

        return convertView;
    }

    private void showInfoInvalidNumber() {
        Toast.makeText(activity.getApplicationContext(), "Enter valid number (> 0)", Toast.LENGTH_LONG).show();
    }

    private void showInfoNoPermission() {
        Toast.makeText(activity.getApplicationContext(), "You do not have permission to delete product", Toast.LENGTH_LONG).show();
    }

    private void updateArrayAdapter() {
        new RestService(sharedPreferences).getAllProducts(prod -> updateList(activity).accept(prod));
    }

    private String getNullSafeNumberValue(Number number) {
        if (number == null) {
            return "";
        }
        return number.toString();
    }

    public Consumer<List<Product>> updateList(Activity activity) {
        return r -> activity.runOnUiThread(() -> {
            if (r != null) {
                clear();
                addAll(r);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Product list has been updated", Toast.LENGTH_LONG).show();
            }
        });
    }
}
