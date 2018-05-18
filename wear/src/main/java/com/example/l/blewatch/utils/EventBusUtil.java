package com.example.l.blewatch.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by L
 * 2018/5/18
 */

public class EventBusUtil {

    public static void sendMessage(Object object) {
        EventBus.getDefault().post(object);
    }
}
