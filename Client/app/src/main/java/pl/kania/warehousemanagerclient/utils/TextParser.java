package pl.kania.warehousemanagerclient.utils;

import android.widget.EditText;
import android.widget.TextView;

public class TextParser {

    public static Long parseLong(TextView textView) {
        return Long.valueOf(textView.getText().toString());
    }

    public static Integer parseInt(TextView textView) {
        return Integer.valueOf(textView.getText().toString());
    }

    public static String getText(TextView textView) {
        return textView.getText().toString();
    }

    public static Double parseDouble(EditText textView) {
        return Double.valueOf(textView.getText().toString());
    }
}
