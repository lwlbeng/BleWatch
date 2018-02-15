package com.example.l.blewatch.bean.eventbus;

/**
 * Created by L on 2018/1/18.
 */

public class EventBleRssiBean {
    private int rssi;

    public EventBleRssiBean(int rssi) {
        this.rssi = rssi;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
