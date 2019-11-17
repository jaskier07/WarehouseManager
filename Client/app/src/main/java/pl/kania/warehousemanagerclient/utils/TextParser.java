package pl.kania.warehousemanagerclient.utils;

import android.widget.EditText;
import android.widget.TextView;

public class TextParser {

    public static Long parseLong(TextView textView) {
        if (isNullOrEmpty(textView)) {
            return null;
        }
        return Long.valueOf(textView.getText().toString());
    }

    public static Integer parseInt(TextView textView) {
        if (isNullOrEmpty(textView)) {
            return null;
        }
        return Integer.valueOf(textView.getText().toString());
    }

    public static String getText(TextView textView) {
        if (isNull(textView)) {
            return null;
        }
        return textView.getText().toString();
    }

    public static Double parseDouble(EditText textView) {
        if (isNullOrEmpty(textView)) {
            return null;
        }
        return Double.valueOf(textView.getText().toString());
    }

    private static boolean isNullOrEmpty(TextView textView) {
        return isNull(textView) || textView.getText().length() == 0;
    }

    private static boolean isNull(TextView textView) {
        return textView == null || textView.getText() == null;
    }

    public static Integer getValidIntegerValue(EditText priceValue, Runnable onInvalidNumber) {
        try {
            Integer number = parseInt(priceValue);
            if (number != null && number > 0) {
                return number;
            }
        } catch (Exception e) {
        }
        onInvalidNumber.run();
        return 0;
    }

    public static Double getValidDoubleValue(EditText priceValue, Runnable onInvalidNumber) {
        try {
            Double number = parseDouble(priceValue);
            if (number != null && number > 0) {
                return number;
            }
        } catch (Exception e) {
        }
        onInvalidNumber.run();
        return 0D;
    }

    public static boolean isValidNumber(EditText priceValue) {
        try {
            Double number = parseDouble(priceValue);
            return number != null && number > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
