package com.example.l.blewatch.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by L on 2018/1/18.
 */

public class BaseApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getmContext() {
        return mContext;
    }

}
