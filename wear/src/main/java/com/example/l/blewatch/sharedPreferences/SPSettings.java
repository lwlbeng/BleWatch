package com.example.l.blewatch.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.example.l.blewatch.base.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存设置数据，主要用于保存与帐号无关联信息,退出登录不需调用清除方法
 *
 * @author yang
 * @time 2017-3-17 下午5:45:00
 */
public class SPSettings {
    private static final String FILE_SETTINGS = "settings";
    private static SharedPreferences sSharedPreference = BaseApplication
            .getmContext().getSharedPreferences(FILE_SETTINGS,
                    Context.MODE_PRIVATE);
    private static Editor mEditor = sSharedPreference.edit();

    /**
     * 蓝牙地址
     */
    public static final String KEY_BLE_ADDRESS = "ble_address";


    /**
     * 保存数据
     *
     * @param key   键
     * @param value 值
     */
    public static void put(String key, Object value) {
        if (value == null) {
            value = "";
        }
        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        }
        mEditor.commit();
    }

    public static String getString(String key, String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "";
        }
        return sSharedPreference.getString(key, (String) defaultValue);// 解密处理
    }




}
