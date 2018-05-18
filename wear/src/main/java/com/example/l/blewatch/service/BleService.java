package com.example.l.blewatch.service;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.wearable.activity.WearableActivity;
import android.text.TextUtils;
import android.util.Log;

import com.example.l.blewatch.bean.eventbus.EventBleCommandSucceedBean;
import com.example.l.blewatch.bean.eventbus.EventBleConnectSucceedBean;
import com.example.l.blewatch.bean.eventbus.EventBleScanBean;
import com.example.l.blewatch.bean.eventbus.EventErrorBean;
import com.example.l.blewatch.ble.BLEParameters;
import com.example.l.blewatch.ble.Command;
import com.example.l.blewatch.sharedPreferences.SPSettings;
import com.example.l.blewatch.utils.EventBusUtil;
import com.example.l.blewatch.utils.HexUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.l.blewatch.ble.BLEParameters.DESC_CCC;
import static com.example.l.blewatch.ble.BLEParameters.RECEIVE_CHAR_UUID;
import static com.example.l.blewatch.ble.BLEParameters.SEND_CHAR_UUID;


public class BleService extends Service implements BluetoothAdapter.LeScanCallback {

    public static final String DEFAULT_DEVICE_NAME = "ZSBLE";
    private static final long SCAN_PERIOD = 15000;

    public static final int BLE_OPEN_REQUESTCODE = 0x001;

    private final IBinder mBinder = new LocalBinder();

    private String mDeviceAddress = null;
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBleAdapter = null;
    private BluetoothDevice mDevice;
    public BluetoothGatt mGatt = null;
    private BluetoothGattCharacteristic mSendChar;
    private Handler mHandler;

    private Activity mActivity;

    private boolean isSame = false;
    private boolean haveDB = false;
    private boolean b = false;

    /**
     * 用于记录所有扫描到的 ZSBLE 设备的
     */
    private List<BluetoothDevice> mBleScanDevices = new ArrayList<>();


    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    public BleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("L-WL", "onBind: BleService");
        return this.mBinder;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        Log.d("L-WL", "bindService: BleService");
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("L-WL", "onCreate: BleService");
        mHandler = new Handler();
        initBLE();
    }

    public void initBLE() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("L-WL", "onDestroy: BleService");
    }

    //============== 扫描 =======================================

    /**
     * 手动扫描
     */
    public void bleManualScan(WearableActivity activity) {
        mActivity = activity;

        if (mBleAdapter == null || !mBleAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            mActivity.startActivityForResult(enableBtIntent, BLE_OPEN_REQUESTCODE);
        } else {
            scan(SCAN_PERIOD);
        }
    }

    private void scan(long time) {
        mBleScanDevices.clear();
        mBleAdapter.startLeScan(this);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleStopScan();
            }
        }, time);
    }

    public void bleStopScan() {
        mBleAdapter.stopLeScan(this);
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        new Thread() {
            @Override
            public void run() {
                String name = device.getName();
                if (name == null) {
                    return;
                }

                if (name.startsWith(DEFAULT_DEVICE_NAME)) {
                    if (mBleScanDevices.size() > 0) {
                        for (int i = 0; i < mBleScanDevices.size(); i++) {
                            if (mBleScanDevices.get(i).getAddress().equals(device.getAddress())) {
                                isSame = true;
                                // 有就直接跳出方法
                                return;
                            } else {
                                isSame = false;
                            }
                        }

                    }

                    if (!isSame) {
                        Log.d("L-WL", "run: name:==> " + device.getName());
                        mBleScanDevices.add(device);
                        // 发送到UI线程
                        EventBus.getDefault().post(new EventBleScanBean(device.getName(), device.getAddress(), String.valueOf(rssi)));
                    }
                }
            }
        }.start();
    }


    //======================== 连接 ==========================
    public void connect(String address) {
        if (TextUtils.isEmpty(address)) {
            return;
        }

        bleStopScan();
        mDeviceAddress = address;
        mDevice = mBleAdapter.getRemoteDevice(mDeviceAddress);
        mDevice.connectGatt(this, false, mGattCallback);
    }

    public void connect(int position) {
        bleStopScan();
        String address = mBleScanDevices.get(position).getAddress();
        connect(address);
    }

    public void disconnect() {
        if (mGatt != null) {
            mGatt.disconnect();
            b = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BleService.this.stopSelf();
                }
            }, 1000);

        }

    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("L-WL", "onConnectionStateChange: status == " + status);
            if (status != 0 && !b) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 2018/1/17 自动连接
                        connect(mDeviceAddress);
                    }
                }, 1000);
            }

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d("L-WL", "onConnectionStateChange: 连接成功");

                    gatt.discoverServices();
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d("L-WL", "onConnectionStateChange: 连接断开");
//                    connect(SPSettings.getString(SPSettings.KEY_BLE_ADDRESS, ""));
                    mGatt = null;
                    break;

                default:

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> bleGattServiceList = gatt.getServices();
            for (BluetoothGattService bleGattService : bleGattServiceList) {
                Log.d("L-WL", "发现服务: " + bleGattService.getUuid());

                List<BluetoothGattCharacteristic> bleGattCharacteristicList = bleGattService.getCharacteristics();

                for (final BluetoothGattCharacteristic bleGattCharacteristic : bleGattCharacteristicList) {
                    Log.d("L-WL", "发现服务有以下特征: " + bleGattCharacteristic.getUuid());
                    if (RECEIVE_CHAR_UUID.equals(bleGattCharacteristic.getUuid().toString())) {
                        // 发现通知 UUID 就去打开通知的开关
                        requestIndication(gatt, bleGattCharacteristic);

                    } else if (SEND_CHAR_UUID.equals(bleGattCharacteristic.getUuid().toString())) {
                        mSendChar = bleGattCharacteristic;
                    }
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d("L-WL", "requestIndication 结束!");
            mGatt = gatt;
            SPSettings.put(SPSettings.KEY_BLE_ADDRESS, mDevice.getAddress());
            EventBusUtil.sendMessage(new EventBleConnectSucceedBean(EventBleConnectSucceedBean.BLE_CONNECT_SUCCEED, mDevice.getName()));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] newMessage = characteristic.getValue();
            Log.d("L-WL", "onCharacteristicChanged: newMessage: " + HexUtil.encodeHexStr(newMessage));
            byte[] commandByte = new byte[2];
            System.arraycopy(newMessage, 4, commandByte, 0, 2);
            String newCommand = HexUtil.encodeHexStr(commandByte);
            Log.d("L-WL", "onCharacteristicChanged: " + newCommand);
            if (newCommand.startsWith("ab")) {
                byte rssiByte = newMessage[5];
                haveDB = true;
            } else if (newCommand.equals("a200")) {// 上锁成功
                commandSucceed(EventBleCommandSucceedBean.LOCK_SUCCEED);
            } else if (newCommand.equals("a300")) { // 解锁成功
                commandSucceed(EventBleCommandSucceedBean.UNLOCK_SUCCEED);
            }
        }
    };

    private void commandSucceed(int state) {
        EventBusUtil.sendMessage(new EventBleCommandSucceedBean(state));
    }

    /**
     * 蓝牙通知功能打开
     *
     * @param gatt
     * @param characteristic
     */
    private void requestIndication(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        Log.d("L-WL", "requestIndication: ------------------> characteristic_UUID" + characteristic.getUuid().toString());
        Log.d("L-WL", "requestIndication: ------------------> characteristic_descriptor" + characteristic.getDescriptor(DESC_CCC).getUuid().toString());
        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESC_CCC);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        Log.d("L-WL", "requestIndication: --------------> 已调用注册通知");
    }


//================ 发送命令 ============================

    /**
     * 开锁命令
     */
    public void sendUnlock() {
        if (mBleAdapter.isEnabled() && mGatt != null) {
            Log.d("L-WL", "sendUnlock: ");
            mSendChar.setValue(Command.OBD_UNLOCK);
            mGatt.writeCharacteristic(mSendChar);
        } else {
            EventBusUtil.sendMessage(new EventErrorBean(404, "蓝牙未连接，请连接后再使用！"));
        }
    }

    /**
     * 上锁命令
     */
    public void sendLock() {
        if (mBleAdapter.isEnabled() && mGatt != null) {
            mSendChar.setValue(Command.OBD_LOCK);
            mGatt.writeCharacteristic(mSendChar);
        } else {
            EventBusUtil.sendMessage(new EventErrorBean(404, "蓝牙未连接，请连接后再使用！"));
        }
    }

}
