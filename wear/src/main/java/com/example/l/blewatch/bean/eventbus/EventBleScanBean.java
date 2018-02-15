package com.example.l.blewatch.bean.eventbus;

/**
 * Created by Administrator on 2017/10/18.
 */

public class EventBleScanBean {
    private String bleName;
    private String bleAddr;
    private String bleRssi;

    public EventBleScanBean(String bleName, String bleAddr, String bleRssi) {
        this.bleName = bleName;
        this.bleAddr = bleAddr;
        this.bleRssi = bleRssi;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleAddr() {
        return bleAddr;
    }

    public void setBleAddr(String bleAddr) {
        this.bleAddr = bleAddr;
    }

    public String getBleRssi() {
        return bleRssi;
    }

    public void setBleRssi(String bleRssi) {
        this.bleRssi = bleRssi;
    }

    @Override
    public String toString() {
        return "EventBleScanBean{" +
                "bleName='" + bleName + '\'' +
                ", bleAddr='" + bleAddr + '\'' +
                ", bleRssi='" + bleRssi + '\'' +
                '}';
    }
}
