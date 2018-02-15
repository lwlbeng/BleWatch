package com.example.l.blewatch.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/17.
 */

public class BluetoothLeDeviceBean implements Serializable {
    private String BluetoothName;
    private String BluetoothAddress;
    private String rssi;

    public BluetoothLeDeviceBean(String bluetoothName, String bluetoothAddress, String rssi) {
        BluetoothName = bluetoothName;
        BluetoothAddress = bluetoothAddress;
        this.rssi = rssi;
    }

    public String getBluetoothName() {
        return BluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        BluetoothName = bluetoothName;
    }

    public String getBluetoothAddress() {
        return BluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        BluetoothAddress = bluetoothAddress;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "BluetoothLeDeviceBean{" +
                "BluetoothName='" + BluetoothName + '\'' +
                ", BluetoothAddress='" + BluetoothAddress + '\'' +
                ", rssi=" + rssi +
                '}';
    }
}
