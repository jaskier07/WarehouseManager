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
}
