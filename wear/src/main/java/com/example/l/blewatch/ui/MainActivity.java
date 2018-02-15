package com.example.l.blewatch.ui;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.blewatch.R;
import com.example.l.blewatch.bean.eventbus.EventBleCommandSucceedBean;
import com.example.l.blewatch.bean.eventbus.EventBleConnectSucceedBean;
import com.example.l.blewatch.service.BleService;
import com.example.l.blewatch.sharedPreferences.SPSettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends WearableActivity {

    @BindView(R.id.btn_unlock)
    Button mBtnUnlock;
    @BindView(R.id.btn_scan)
    Button mBtnScan;
    @BindView(R.id.btn_lock)
    Button mBtnLock;
    @BindView(R.id.tv_ble_name)
    TextView mTvBleName;

    private Vibrator mVibrator;

    private BleService mBleService;

    private String mBleAddress;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBleService = ((BleService.LocalBinder) rawBinder).getService();
            mBleAddress = SPSettings.getString(SPSettings.KEY_BLE_ADDRESS, "");
            if (!TextUtils.isEmpty(mBleAddress)) {
                mBleService.connect(mBleAddress);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mBleService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();

        MainActivityPermissionsDispatcher.initPermissionWithPermissionCheck(this);
    }

    private void initView() {
        // Enables Always-on
        setAmbientEnabled();

        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        mBleAddress = SPSettings.getString(SPSettings.KEY_BLE_ADDRESS, "");
        if (!TextUtils.isEmpty(mBleAddress)) {
            mBtnScan.setVisibility(View.GONE);
            mBtnUnlock.setVisibility(View.VISIBLE);
            mBtnLock.setVisibility(View.VISIBLE);
            mTvBleName.setVisibility(View.VISIBLE);
        }

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 绑定BLE的服务
        Intent bindIntent = new Intent(this, BleService.class);
        startService(bindIntent);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {

        Log.d("L-WL", "onDestroy: MainActivity");
        mBleService.disconnect();
        EventBus.getDefault().unregister(this);
        unbindService(mServiceConnection);
        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(MainActivity.this.getApplicationContext(), BleService.class);
//                stopService(intent);
//            }
//        }, 1000);
        super.onDestroy();
    }

    @OnClick({R.id.btn_unlock, R.id.btn_scan, R.id.btn_lock})
    public void onClick(View v) {
        mVibrator.vibrate(new long[]{0, 50}, -1);
        switch (v.getId()) {
            case R.id.btn_unlock:
                if (mBleService != null) {
                    mBleService.sendUnlock();
                }
                break;
            case R.id.btn_scan:
                Intent scanIn = new Intent(this, ScanActivity.class);
                startActivity(scanIn);
                break;
            case R.id.btn_lock:
                if (mBleService != null) {
                    mBleService.sendLock();
                }
                break;
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION})
    void initPermission() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 命令执行反馈
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandSucceedEvent(EventBleCommandSucceedBean bean) {
        switch (bean.getSucceedState()) {
            case EventBleCommandSucceedBean.UNLOCK_SUCCEED:
                Toast.makeText(this, "开门成功", Toast.LENGTH_SHORT).show();
                break;
            case EventBleCommandSucceedBean.LOCK_SUCCEED:
                Toast.makeText(this, "关门成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 连接状态返回
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectSucceedEvent(EventBleConnectSucceedBean bean) {
        switch (bean.getConnectState()) {
            case EventBleConnectSucceedBean.BLE_CONNECT_SUCCEED:
                mBtnScan.setVisibility(View.GONE);
                mBtnUnlock.setVisibility(View.VISIBLE);
                mBtnLock.setVisibility(View.VISIBLE);
                mTvBleName.setVisibility(View.VISIBLE);
                mTvBleName.setText(bean.getBleName());
                break;

            default:
        }
    }
}
