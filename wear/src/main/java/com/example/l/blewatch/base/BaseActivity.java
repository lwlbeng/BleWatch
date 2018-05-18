package com.example.l.blewatch.base;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by L
 * 2018/5/18
 */

public class BaseActivity extends WearableActivity {

    protected Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 注册 EventBus
        EventBus.getDefault().register(this);

        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
