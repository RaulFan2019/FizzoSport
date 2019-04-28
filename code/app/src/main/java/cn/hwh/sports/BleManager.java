package cn.hwh.sports;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.hwh.sports.ble.BleConfig;
import cn.hwh.sports.ble.BleConnectEntity;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class BleManager {
    private static final String TAG = "BleManager";

    /* msg contains */
    public final static int MSG_CONNECTING = 0;//正在连接
    public final static int MSG_CONNECTED = 1;//已连接
    public final static int MSG_DISCONNECT = 2;//断开连接
    public final static int MSG_CONNECT_FAIL = 3;//连接失败
    public final static int MSG_NEW_HEARTBEAT = 4;//新的心率消息
    public final static int MSG_NOT_FIZZO = 5;//不是FIZZO手环设备
    public final static int MSG_NEW_BATTERY = 6;//电量消息
    public final static int MSG_CAN_BIND = 7;//可以绑定了
    public final static int MSG_GET_WORKOUT_MODE = 8;//获取健身模式
    public final static int MSG_GET_AUTO_MODE = 9;//获取自动模式
    public final static int MSG_GET_FIRMWARE_REVISION = 10;//获取版本信息
    public final static int MSG_SETTING_HR_RANGE_SUCCESS = 11;//设置hr心率范围成功
    public final static int MSG_READY_OTA_OK = 12;//已准备好OTA
    public final static int MSG_SHOCK_OK = 13;//反馈震动
    public final static int MSG_GET_HISTORY_NUM_ERROR = 14;//获取记录数量错误
    public final static int MSG_SHOCK_ERROR = 14;//反馈震动错误
    public final static int MSG_CURR_STEP_COUNT = 15;//当前步数


    public final static int INTERVAL_RESUME_BOOT = 2 * 60 * 1000;//boot状态恢复时间

    public final static int LOCAL_MSG_STOP_BLE = 0x01;
    public final static int RESUME_BOOT_STATE = 0x02;


    private static BleManager instance;//唯一实例

    //bluetooth
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter mBluetoothAdapter;
    private Context mContext;


    public String mConnectMac = "";
    public BleConnectEntity mBleConnectE;


    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOCAL_MSG_STOP_BLE) {
                cancelDiscovery();
            } else if (msg.what == RESUME_BOOT_STATE) {
                Log.e(TAG,"RESUME_BOOT_STATE<" + mConnectMac + ">");
                addNewConnect(mConnectMac);
            }
        }
    };


    /**
     * 获取堆栈管理的单一实例
     */
    public static BleManager getBleManager() {
        if (instance == null) {
            instance = new BleManager();
            initBleAdapter();
        }
        return instance;
    }

    public static void initBleAdapter() {
        bluetoothManager = (BluetoothManager) LocalApplication.getInstance().applicationContext
                .getSystemService(LocalApplication.getInstance().BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }

    public void destroy() {
        instance = null;
        mConnectMac = "";
    }

    /**
     * 增加一个新的连接
     *
     * @param address
     */
    public void addNewConnect(final String address) {
        if (mBleConnectE == null) {
            mBleConnectE = new BleConnectEntity(LocalApplication.getInstance().applicationContext
                    , address, mBluetoothAdapter);
        } else {
            if (!mBleConnectE.address.equals(address)) {
                mBleConnectE.disConnect();
                mBleConnectE = new BleConnectEntity(LocalApplication.getInstance().applicationContext
                        , address, mBluetoothAdapter);

            }
        }
        mConnectMac = address;
    }


    /**
     * 重新建立连接
     *
     * @param address
     */
    public void replaceConnect(final String address) {
        Log.v(TAG, "replaceConnect:<" + address + ">");
        mConnectMac = address;
        if (!mConnectMac.equals("")) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothAdapter.startLeScan(new UUID[]{BleConfig.UUID_HEART_RATE_SERVICE}, mLeScanCallback);
        }
    }

    /**
     * 进入boot状态
     */
    public void bootState(Context context) {
        if (mBleConnectE != null) {
            mBleConnectE.disConnect();
            mBleConnectE = null;
        }
        long bootResumeTime = System.currentTimeMillis() + INTERVAL_RESUME_BOOT;
//        Log.v(TAG,"bootResumeTime:" + TimeU.formatDateToStr(new Date(bootResumeTime),TimeU.FORMAT_TYPE_8));
        UserSPData.setBootResumeTime(context, bootResumeTime);
        mLocalHandler.sendEmptyMessageDelayed(RESUME_BOOT_STATE, INTERVAL_RESUME_BOOT);
    }

//    /**
//     * 增加可能需要绑定的设备
//     *
//     * @param address
//     */
//    public void addBindDevice(final String address, final String name) {
//        for (BleConnectEntity entity : mBindList) {
//            if (entity.address.equals(address)) {
//                return;
//            }
//        }
//        BleConnectEntity entity = new BleConnectEntity(LocalApplication.getInstance().applicationContext
//                , address, mBluetoothAdapter);
//        entity.name = name;
//        mBindList.add(entity);
//    }


//    /**
//     * 解除所有尝试绑定设备的连接
//     */
//    public void clearBindDevice() {
//        for (BleConnectEntity entity : mBindList) {
//            entity.disConnect();
//        }
//        mBindList.clear();
//    }
//
//    public void shockDevice(final String address){
//        for (BleConnectEntity entity : mBindList) {
//            if (entity.address.equals(address)) {
//                entity.shock();
//                return;
//            }
//        }
//    }

    /**
     * 获取连接实体类
     *
     * @return
     */
    public BleConnectEntity getConnectEntity() {
        return mBleConnectE;
    }

    /**
     * 扫描新设备
     */
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            Log.v(TAG, "onLeScan:<" + device.getAddress() + ">");
            if (device.getAddress().equals(mConnectMac)) {
                BleManager.getBleManager().addNewConnect(device.getAddress());
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    };

    /**
     * 取消连接
     */
    public void cancelDiscovery() {
        stopScan();
        if (mBleConnectE == null || !mBleConnectE.mIsConnected) {
            replaceConnect(mConnectMac);
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (mBluetoothAdapter != null && mLeScanCallback != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}
