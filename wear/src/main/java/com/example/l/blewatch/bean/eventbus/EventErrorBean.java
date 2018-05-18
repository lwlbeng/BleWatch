package com.example.l.blewatch.bean.eventbus;

/**
 * Created by L
 * 2018/5/18
 */

public class EventErrorBean {
    private int state;
    private String message;

    public EventErrorBean(int state, String message) {
        this.state = state;
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EventErrorBean{" +
                "state=" + state +
                ", message='" + message + '\'' +
                '}';
    }
}
