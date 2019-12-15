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
import pl.kania.warehousemanagerclient.model.IdType;
import pl.kania.warehousemanagerclient.model.entities.ProductClient;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;
import pl.kania.warehousemanagerclient.utils.TextParser;

import static pl.kania.warehousemanagerclient.ui.fragments.LogInFragment.SHARED_PREFERENCES_NAME;
import static pl.kania.warehousemanagerclient.utils.TextParser.getValidDoubleValue;
import static pl.kania.warehousemanagerclient.utils.TextParser.getValidIntegerValue;

public class ProductAdapter extends ArrayAdapter<ProductClient> {

    private static final int NO_CHANGE_IN_QUANTITY = 0;
    private final Activity activity;
    private final DatabaseManager db;
    private SharedPreferences sharedPreferences;

    public ProductAdapter(@NonNull Context context, Activity activity, DatabaseManager db) {
        super(context, 0, new ArrayList<>());
        this.activity = activity;
        this.db = db;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_element_fragment, parent, false);
        }
        ProductClient product = getItem(position);
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
        idValue.setText(getNullSafeNumberValue(product.getLocalId()));
        EditText increaseBy = convertView.findViewById(R.id.increaseByValue);
        EditText decreaseBy = convertView.findViewById(R.id.decreaseByValue);

        Button update = convertView.findViewById(R.id.buttonUpdate);
        update.setOnClickListener(c -> //new RestService(sharedPreferences).updateProduct(
                db.updateProduct(ProductClient.builder()
                        .id(product.getId())
                        .manufacturerName(TextParser.getText(manufacturerValue))
                        .modelName(TextParser.getText(modelValue))
                        .price(getValidDoubleValue(priceValue, this::showInfoInvalidNumber))
                        .quantity(TextParser.parseInt(quantityValue))
                        .lastModified(product.getLastModified())
                        .localId(product.getLocalId())
                        .removed(product.isRemoved())
                        .build(), IdType.LOCAL, true, false));//, this::updateArrayAdapter));
        Button delete = convertView.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(c -> deleteProduct(TextParser.parseLong(idValue)));
        Button increase = convertView.findViewById(R.id.buttonIncrease);
        increase.setOnClickListener(c -> changeQuantity(TextParser.parseLong(idValue), getValidIntegerValue(increaseBy, this::showInfoInvalidNumber)));
                Button decrease = convertView.findViewById(R.id.buttonDecrease);
        decrease.setOnClickListener(c -> changeQuantity(TextParser.parseLong(idValue), getValidIntegerValue(decreaseBy, this::showInfoInvalidNumber) * -1));

        return convertView;
    }

    private void changeQuantity(Long id, Integer value) {
//        new RestService(sharedPreferences).decreaseProductQuantity(new ProductQuantity(TextParser.parseLong(idValue),
//                getValidIntegerValue(decreaseBy, this::showInfoInvalidNumber)), this::updateArrayAdapter, this::showInfo);
        //   new RestService(sharedPreferences).increaseProductQuantity(new ProductQuantity(TextParser.parseLong(idValue),
        //           getValidIntegerValue(increaseBy, this::showInfoInvalidNumber)), this::updateArrayAdapter));
        if (value != NO_CHANGE_IN_QUANTITY) {
            boolean updated = db.updateQuantityProductValue(id, value, IdType.LOCAL, true);
            if (updated) {
                updateArrayAdapter();
            }
        }
    }

    private void deleteProduct(Long productId) {
        //new RestService(sharedPreferences).deleteProduct(TextParser.parseLong(idValue), this::updateArrayAdapter,
        //this::showInfoNoPermission));
        // TODO pobranie id zalogowanego użytkownika
        // TODO pobranie jego roli
        // TODO przekazanie do metody wraz z akcją na brak uprawnień
        if (db.deleteProduct(productId, IdType.LOCAL)) {
            updateArrayAdapter();
        }
    }

    private void showInfo(String info) {
        activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), info, Toast.LENGTH_LONG).show());
    }

    private void showInfoInvalidNumber() {
        Toast.makeText(activity.getApplicationContext(), "Enter valid number (> 0)", Toast.LENGTH_LONG).show();
    }

    private void showInfoNoPermission() {
        activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "You do not have permission to delete product", Toast.LENGTH_LONG).show());
    }

    private void updateArrayAdapter() {
        updateList(activity).accept(db.selectAllNonRemovedProductsWithGlobalId());
//        new RestService(sharedPreferences).getAllProducts(prod -> updateList(activity).accept(prod));
    }

    private String getNullSafeNumberValue(Number number) {
        if (number == null) {
            return "";
        }
        return number.toString();
    }

    public Consumer<List<ProductClient>> updateList(Activity activity) {
        return r -> activity.runOnUiThread(() -> {
            if (r != null) {
                clear();
                addAll(r);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "ProductClient list has been updated", Toast.LENGTH_LONG).show();
            }
        });
    }
}
