package com.example.sami.ads.helper;

import android.widget.EditText;

/**
 * Created by sami on 1/31/2017.
 */
public class Validation {
    public static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            etText.setError(null);
            return false;
        } else {
            etText.setError("required");
            etText.requestFocus();
            return true;
        }
    }
}
