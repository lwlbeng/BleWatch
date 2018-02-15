package com.example.l.blewatch.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.l.blewatch.adapter.BleListAdapter;
import com.example.l.blewatch.R;
import com.example.l.blewatch.bean.BluetoothLeDeviceBean;
import com.example.l.blewatch.bean.eventbus.EventBleConnectSucceedBean;
import com.example.l.blewatch.bean.eventbus.EventBleScanBean;
import com.example.l.blewatch.service.BleService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends WearableActivity {

    @BindView(R.id.lv_devices_list)
    RecyclerView mLvDevicesList;

    private BleListAdapter mAdapter;

    private BleService mBleService;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBleService = ((BleService.LocalBinder) rawBinder).getService();
            mBleService.bleManualScan(ScanActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mBleService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLvDevicesList.setLayoutManager(layoutManager);
        mAdapter = new BleListAdapter(new BleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mBleService != null) {
                    mBleService.bleStopScan();
                    mBleService.connect(position);
                }
            }
        });
        mLvDevicesList.setAdapter(mAdapter);

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 绑定BLE的服务
        Intent bindIntent = new Intent(this, BleService.class);
        startService(bindIntent);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(mServiceConnection);
    }

    @OnClick(R.id.btn_scan_cancel)
    public void onClick(View view) {
        if (view.getId() == R.id.btn_scan_cancel) {
            if (mBleService != null) {
                mBleService.bleStopScan();
            }
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanEvent(EventBleScanBean eventBleScanBean) {
        mAdapter.addDevice(new BluetoothLeDeviceBean(eventBleScanBean.getBleName(), eventBleScanBean.getBleAddr(), eventBleScanBean.getBleRssi()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectSucceedEvent(EventBleConnectSucceedBean bean) {
        switch (bean.getConnectState()) {
            case EventBleConnectSucceedBean.BLE_CONNECT_SUCCEED:
                finish();
                break;

            default:
        }
    }
}
