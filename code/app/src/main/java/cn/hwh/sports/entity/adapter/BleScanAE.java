package cn.hwh.sports.entity.adapter;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2016/7/20.
 */
public class BleScanAE {

    public static final int STATE_DISCONNECT = 0;//未连接
    public static final int STATE_CONNECTING = 1;//连接中
    public static final int STATE_CONNECTED = 2;//已连接

    public BluetoothDevice device;
    public int mConnectState;
    public int rssi;

    public BleScanAE(BluetoothDevice device, int mConnectState, int rssi) {
        this.device = device;
        this.mConnectState = mConnectState;
        this.rssi = rssi;
    }
}
