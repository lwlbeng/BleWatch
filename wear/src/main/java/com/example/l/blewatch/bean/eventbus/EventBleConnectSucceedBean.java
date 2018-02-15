package com.example.l.blewatch.bean.eventbus;

/**
 * Created by L on 2018/1/18.
 */

public class EventBleConnectSucceedBean {

    public static final int BLE_CONNECT_SUCCEED = 0;

    private int connectState;
    private String bleName;

    public EventBleConnectSucceedBean(int connectState, String bleName) {
        this.connectState = connectState;
        this.bleName = bleName;
    }

    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }
}
