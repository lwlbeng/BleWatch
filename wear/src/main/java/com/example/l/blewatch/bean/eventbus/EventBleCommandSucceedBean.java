package com.example.l.blewatch.bean.eventbus;

/**
 * Created by Administrator on 2017/11/27.
 */

public class EventBleCommandSucceedBean {

    public static final int OPEN_WIN_SUCCEED = 0;
    public static final int CLOSE_WIN_SUCCEED = 1;
    public static final int START_CAR_SUCCEED = 2;
    public static final int STOP_CAR_SUCCEED = 3;
    public static final int UNLOCK_SUCCEED = 4;
    public static final int LOCK_SUCCEED = 5;
    public static final int FIND_CAR_SUCCEED = 6;
    public static final int OPEN_TRUNK_SUCCEED = 7;
    public static final int OPEN_WIN_ERROR = 8;
    public static final int CLOSE_WIN_ERROR = 9;
    public static final int START_CAR_ERROR = 10;
    public static final int STOP_CAR_ERROR = 11;
    public static final int UNLOCK_ERROR = 12;
    public static final int LOCK_ERROR = 13;
    public static final int FIND_CAR_ERROR = 14;
    public static final int OPEN_TRUNK_ERROR = 15;
    public static final int AUTOUNLOCK_SUCCEED = 16;

    private int succeedState;

    public EventBleCommandSucceedBean(int succeedState) {
        this.succeedState = succeedState;
    }

    public int getSucceedState() {
        return succeedState;
    }

    public void setSucceedState(int succeedState) {
        this.succeedState = succeedState;
    }

    @Override
    public String toString() {
        return "EventBleCommandSucceedBean{" +
                "succeedState=" + succeedState +
                '}';
    }
}
