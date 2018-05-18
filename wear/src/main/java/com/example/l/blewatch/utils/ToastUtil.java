package com.example.l.blewatch.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by L
 * 2018/5/18
 */

public class ToastUtil {

    public static void shortShow(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void longShow(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
