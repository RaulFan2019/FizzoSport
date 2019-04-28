package cn.hwh.sports.ble;

import cn.hwh.sports.BleManager;

/**
 * Created by Raul.Fan on 2016/11/24.
 * 蓝牙连接事件
 */
public class BleConnectEE {

    public int msg;

    public int battery;
    public int hr;//心率
    public int cadence;//步频
    public int stepCount;//步数
    public int speed;//速度

    public long currStepCount;

    public byte byteResult;

    public String StrMsg;
    public String StrMsgTwo;


    public BleConnectEE(int msg) {
        this.msg = msg;
    }

    public BleConnectEE(int msg, long currStepCount) {
        this.msg = msg;
        this.currStepCount = currStepCount;
    }

    public BleConnectEE(int msg, int battery) {
        this.msg = msg;
        this.battery = battery;
    }

    public BleConnectEE(int msg, int hr, int cadence, int stepCount, int speed) {
        this.msg = msg;
        this.hr = hr;
        this.cadence = cadence;
        this.stepCount = stepCount;
        this.speed = speed;
    }

    public BleConnectEE(int msg, byte byteResult) {
        this.msg = msg;
        this.byteResult = byteResult;
    }

    public BleConnectEE(int msg, String StrMsg) {
        this.msg = msg;
        this.StrMsg = StrMsg;
    }

    public BleConnectEE(int msg, String StrMsg, String strMsgTwo) {
        this.msg = msg;
        this.StrMsg = StrMsg;
        this.StrMsgTwo = strMsgTwo;
    }
}
