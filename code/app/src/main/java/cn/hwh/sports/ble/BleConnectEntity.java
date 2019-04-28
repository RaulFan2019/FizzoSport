package cn.hwh.sports.ble;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.SyncWatchDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.SyncWatchDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.SyncWatchWorkoutEE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetLatestFirmwareRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.MyRequestParams;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.bannertoast.TipViewController;
import cn.hwh.sports.utils.CalorieU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.FileU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.TimeU;
import cn.hwh.sports.utils.WindowUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class BleConnectEntity {

    private static final String TAG = "BleConnectEntity";
    private static final int READ_BATTERY_INTERVAL = 10 * 60 * 1000;
    private static final int BATTERY_LOW_POWER = 20;

    private static final int INTERVAL_WRITE_DELAY = 1000;

    private static final long OUT_OF_TIME_DELETE = 3 * 1000;
    private static final long OUT_OF_TIME_GET_STEP = 10 * 1000;

    /* msg contains */
    private static final int MSG_REPEAT_CONNECT = 0x01;//重新连接设备
    private static final int MSG_REPEAT_NOTIFY_HR = 0x02;//重新notify心率
    private static final int MSG_READ_MANUFACTURER = 0x03;//重复读取生产厂家
    private static final int MSG_REPEAT_NOTIFY_FIZZO_PRIVATE = 0x04;//重复notify fizzo 私有服务
    private static final int MSG_REPEAT_WRITE_UTC = 0x05;//重复写入UTC
    private static final int MSG_REPEAT_GET_BATTERY = 0x06;//读电量
    private static final int MSG_REPEAT_GET_HISTORY_NUM = 0x08;
    private static final int MSG_REPEAT_GET_HISTORY_TITLE = 0x09;
    private static final int MSG_REPEAT_GET_HISTORY_DATA = 0x10;
    private static final int MSG_REPEAT_DELETE_HISTORY_DATA = 0x11;
    private static final int MSG_REPEAT_STOP_SYNC_HISTORY_DATA = 0x12;
    private static final int MSG_REPEAT_GET_WORKOUT_MODE = 0x13;
    private static final int MSG_REPEAT_GET_AUTO_MODE = 0x14;
    private static final int MSG_REPEAT_READ_FIRMWARE = 0x15;
    private static final int MSG_REPEAT_WRITE_USER_HR = 0x16;//重复写入UTC
    private static final int MSG_REPEAT_READY_UPDATE = 0x17;
    private static final int MSG_REPEAT_SHOCK = 0x18;
    private static final int MSG_SHOW_LOW_POWER = 0x19;
    private static final int MSG_NEED_UPDATE_FIRMWARE = 0x20;
    private static final int MSG_REPEAT_DISCOVER = 0x21;//重新连接设备
    private static final int MSG_ANALYSIS_WORKOUT = 0x22;//解析记录
    private static final int MSG_UPDATE_DATA = 0x23;
    private static final int MSG_DELETE_OUT_OF_TIME = 0x24;//删除历史，获取回复超时
    private static final int MSG_REPEAT_CONTROL_LIGHT = 0x25;//控制光管
    private static final int MSG_SHOW_WATCH_RESUME = 0x26;
    private static final int MSG_READ_CURR_STEP_COUNT = 0x27;
    private static final int MSG_GET_STEP_HISTORY_COUNT = 0x28;//获取步数历史数量
    private static final int MSG_GET_STEP_HISTORY_DATA = 0x29;//获取步数数据
    private static final int MSG_DELETE_STEP_HISTORY_DATA = 0x30;//删除步数数据
    private static final int MSG_GET_STEP_TIME_OUT = 0x31;//同步步数超时


    /* local characteristic*/
    BluetoothGattCharacteristic mManufacturerCharacteristic;//设备制造商地址
    BluetoothGattCharacteristic mFirmwareCharacteristic;//设备版本

    BluetoothGattService mHrGattService;//心率服务
    BluetoothGattCharacteristic mHrCharacteristic;//心率特征

    BluetoothGattCharacteristic mFizzoPrivateCMDCharacteristic;//发送命令特征
    BluetoothGattCharacteristic mFizzoPrivateNotifyCharacteristic;//获取结果特征

    BluetoothGattCharacteristic mBatteryCharacteristic;//电量特征

    /* local data */
    private Context mContext;//上下文

    public String address;//连接的mac地址
    public String name = "";//设备名称
    public String firmwareVer = "";

    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    BluetoothGattCallback mGattCallback;//GATT回调
    public BluetoothGatt mBluetoothGatt;//GATT实例

    public boolean mIsConnected = false;//连接状态
    public boolean mSyncNow = false;//是否正在同步

    private boolean mNeedConnect = true;//需要重新连接

    public int mCurrBattery = 100;
    private boolean mNeedShowNotify = true;

    private Callback.Cancelable mCancelable;
    private long mCurrHistoryName = 0;
    private int mDiscoverCount = 0;

    //历史记录相关
    private int mCurrIndex = 0;//目前读取的index值
    private long mLastStartTime = 0;//上次读取历史的开始时间
    private long mStepCount = 0;
    private byte[] mHistoryTitleBytes = new byte[4];
    private List<byte[]> mHistoryWatchData = new ArrayList<>();
    private List<byte[]> mStepData = new ArrayList<>();
    private int splitId = 0;
    private long heatbeatId = 0;
    private int mAllHrSize = 0;
    private int mAllHrTotal = 0;
    private int mTotalIndex = 0;
    private String mSyncLog = "";


    public int mTotalSyncCount;
    public int mNeedSyncCount;
    public int mTotalStepHistoryCount;


    public boolean mNeedUpdate = false;

    private boolean mHasGetHistoryNum = false;
    private boolean mHasSyncFinish = false;
    private boolean mHasGetStepCountNum = false;


    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //重新连接
            if (msg.what == MSG_REPEAT_CONNECT) {
                mHasSyncFinish = false;
                mHasGetHistoryNum = false;
                //三星手机特殊处理
                if (Build.MANUFACTURER.equals("samsung")) {
                    BleManager.getBleManager().destroy();
                    BleManager.getBleManager().replaceConnect(address);
                } else {
                    try {
                        if (mBluetoothGatt != null) {
                            disConnect();
                            mNeedConnect = true;
                            mBluetoothGatt.close();
                        }
                        initCallback();
                        mBluetoothGatt = mBluetoothAdapter.getRemoteDevice(address)
                                .connectGatt(mContext, false, mGattCallback);
                    } catch (IllegalArgumentException ex) {
                        repeatConnect(500);
                    }
                }
                //尝试notify hr
            } else if (msg.what == MSG_REPEAT_NOTIFY_HR) {
                if (!setCharacteristicNotification(mHrCharacteristic, (Boolean) msg.obj)) {
                    sendMsg(MSG_REPEAT_NOTIFY_HR, msg.obj, 500);
                    //若 notify 心率成功，开始读电量，若 已绑定，没有运动，则准备同步数据,
                } else {
                    EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_CAN_BIND, address));
                    sendMsg(MSG_REPEAT_GET_BATTERY, true, 0);
                    if (LocalApplication.getInstance().getLoginUser(mContext).bleMac.equals(address)
                            && WorkoutDBData.getUnFinishWorkout(LocalApplication.getInstance().getLoginUser(mContext).userId) == null) {
                        reStartSync();
//                        mLocalHandler.sendEmptyMessage(MSG_GET_STEP_HISTORY_COUNT);
//                        mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_HISTORY_NUM);
                    }
                }
                //尝试读取生产厂家
            } else if (msg.what == MSG_READ_MANUFACTURER) {
                readManufacturer();
                //尝试notify fizzo 私有服务
            } else if (msg.what == MSG_REPEAT_NOTIFY_FIZZO_PRIVATE) {
                if (!setCharacteristicNotification(mFizzoPrivateNotifyCharacteristic, (Boolean) msg.obj)) {
                    sendMsg(MSG_REPEAT_NOTIFY_FIZZO_PRIVATE, msg.obj, 500);
                    //若 notify 私有服务成功, 写入UTC
                } else {
                    mLocalHandler.sendEmptyMessage(MSG_REPEAT_WRITE_UTC);
                }
                //写入UTC
            } else if (msg.what == MSG_REPEAT_WRITE_UTC) {
                writeUTC();
                //读电量
            } else if (msg.what == MSG_REPEAT_GET_BATTERY) {
                readBattery();
                //获取历史数量
            } else if (msg.what == MSG_REPEAT_GET_HISTORY_NUM) {
                if (!getWatchHistoryNum()) {
                    mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_HISTORY_NUM, INTERVAL_WRITE_DELAY);
                }
                //重试读取历史数据头部
            } else if (msg.what == MSG_REPEAT_GET_HISTORY_TITLE) {
                if (!getHistoryTitle()) {
                    mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_HISTORY_TITLE, INTERVAL_WRITE_DELAY);
                }
                //重复写入获取历史数据
            } else if (msg.what == MSG_REPEAT_GET_HISTORY_DATA) {
                if (!getHistoryData()) {
                    mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_HISTORY_DATA, INTERVAL_WRITE_DELAY);
                }
            } else if (msg.what == MSG_REPEAT_DELETE_HISTORY_DATA) {
                if (!deleteHistory()) {
                    mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_DELETE_HISTORY_DATA, INTERVAL_WRITE_DELAY);
                }
                //停止获取历史数据
            } else if (msg.what == MSG_REPEAT_STOP_SYNC_HISTORY_DATA) {
                if (!stopSyncHistoryData()) {
                    mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_STOP_SYNC_HISTORY_DATA, INTERVAL_WRITE_DELAY);
                }
                //获取健身模式
            } else if (msg.what == MSG_REPEAT_GET_WORKOUT_MODE) {
                readWorkoutMode();
                //获取自动模式
            } else if (msg.what == MSG_REPEAT_GET_AUTO_MODE) {
                readAutoMode();
                //读取设备版本信息
            } else if (msg.what == MSG_REPEAT_READ_FIRMWARE) {
                readFirmVersion();
                //写入用户心率
            } else if (msg.what == MSG_REPEAT_WRITE_USER_HR) {
                writeUserHr();
                //写入准备升级标志
            } else if (msg.what == MSG_REPEAT_READY_UPDATE) {
                readyUpdate();
                //尝试反馈震动
            } else if (msg.what == MSG_REPEAT_SHOCK) {
                shock();
            } else if (msg.what == MSG_SHOW_LOW_POWER) {
                showLowPower();
            } else if (msg.what == MSG_NEED_UPDATE_FIRMWARE) {
                LocalApplication.getInstance().showFirmWareUpdateDialog(mContext, (String) msg.obj);
            } else if (msg.what == MSG_REPEAT_DISCOVER) {
                mHasSyncFinish = false;
                mHasGetHistoryNum = false;
                mDiscoverCount++;
                if (mDiscoverCount > 1) {
                    mDiscoverCount = 0;
                    mLocalHandler.sendEmptyMessage(MSG_REPEAT_CONNECT);
                } else {
                    doRepeatDiscover();
                }
            } else if (msg.what == MSG_ANALYSIS_WORKOUT) {
                analysisWorkoutData();
            } else if (msg.what == MSG_UPDATE_DATA) {
                uploadSyncWatchData();
            } else if (msg.what == MSG_DELETE_OUT_OF_TIME) {
                mSyncNow = false;
                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, -1));
                mSyncLog += "\n" + TimeU.logTime() + "超时退出";
                writeTitleLog();
            } else if (msg.what == MSG_REPEAT_CONTROL_LIGHT) {
                controlLight((boolean) msg.obj);
            } else if (msg.what == MSG_SHOW_WATCH_RESUME) {
//                LocalApplication.getInstance().showWatchResumeActivity(mContext);
            } else if (msg.what == MSG_READ_CURR_STEP_COUNT) {
                readStepCount();
                //读取步数历史数据个数
            } else if (msg.what == MSG_GET_STEP_HISTORY_COUNT) {
                readStepHistoryCount();
                //获取步数历史数据
            } else if (msg.what == MSG_GET_STEP_HISTORY_DATA) {
                readStepHistoryData();
                //delete step history
            } else if (msg.what == MSG_DELETE_STEP_HISTORY_DATA) {
                deleteStepHistory();
            }else if (msg.what == MSG_GET_STEP_TIME_OUT){
                mSyncNow = false;
                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, 0));
            }
        }
    };

    public BleConnectEntity(Context context, final String mac, final BluetoothAdapter mAdapter) {
        this.address = mac;
        this.mBluetoothAdapter = mAdapter;
        this.mContext = context;

        initCallback();
        try {
            mBluetoothGatt = mBluetoothAdapter.getRemoteDevice(mac).connectGatt(context,
                    false, mGattCallback);
        } catch (IllegalArgumentException ex) {
            repeatConnect(500);
        }
    }


    private void initCallback() {
        mGattCallback = null;
        mGattCallback = new BluetoothGattCallback() {
            //连接状态发生改变
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                // 连接失败判断
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "<" + address + ">" + "status !GATT_SUCCESS:" +  ",status;" + status + ",newState:" + newState);
                    if (mNeedConnect) {
                        mIsConnected = false;
                        EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_CONNECT_FAIL));
                        repeatConnect(500);
                    }
                    return;
                }
                //已连接
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "<" + address + ">" + "Connected to GATT server.");
                    Log.i(TAG, "<" + address + ">" + "Attempting to start service discovery");
                    destroyHandler();
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.discoverServices();
                    }
                    repeatDiscover(7000);
                    //连接断开
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i(TAG, "<" + address + ">" + "Disconnected from GATT server.");
                    if (!mNeedConnect) {
                        mBluetoothGatt.close();
                    }
                    mIsConnected = false;
                    EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_DISCONNECT));
                    repeatConnect(500);
                }
            }

            //发现服务
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.v(TAG, "onServicesDiscovered status:" + status);
                mLocalHandler.removeMessages(MSG_REPEAT_DISCOVER);
                mLocalHandler.removeMessages(MSG_REPEAT_CONNECT);
                BleManager.getBleManager().stopScan();

                // 发现服务失败
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    repeatConnect(0);
                    return;
                }
                if (mIsConnected) {
                    return;
                }
//                Log.v(TAG, "real connected");
                mIsConnected = true;

                Log.e(TAG,"gatt.getServices().size():" + gatt.getServices().size());
                //是否在boot状态
                if (gatt.getServices().size() < 9 && mNeedConnect) {
                    boolean refresh = refreshDeviceCache();
                    Log.v(TAG, "refreshDeviceCache:" + refresh);
//                    mLocalHandler.sendEmptyMessage(MSG_SHOW_WATCH_RESUME);
                    repeatConnect(2000);
                    return;
                }
                Log.v(TAG,"MSG_CONNECTED");
                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_CONNECTED));

                //扫描服务
                for (BluetoothGattService gattService : gatt.getServices()) {
                    Log.e(TAG, "<" + address + ">" + "UUID " + gattService.getUuid().toString());

                    //发现device info service
                    if (gattService.getUuid().equals(BleConfig.UUID_DEVICE_INFO_SERVICE)) {
                        mManufacturerCharacteristic = gattService.getCharacteristic(BleConfig.UUID_MANUFACTURER_NAME);
                        mFirmwareCharacteristic = gattService.getCharacteristic(BleConfig.UUID_FIRMWARE_REVISION);
                    }
                    //发现心率服务
                    if (gattService.getUuid().equals(BleConfig.UUID_HEART_RATE_SERVICE)) {
//                        Log.v(TAG, "find heart beat service");
                        mHrGattService = gattService;
                        mHrCharacteristic = mHrGattService.getCharacteristic(BleConfig.UUID_HEART_RATE_MEASUREMENT);
                    }

                    //发现Fizzo 专用服务
                    if (gattService.getUuid().equals(BleConfig.UUID_FIZZO_PRIVATE_SERVICE)) {
//                        Log.v(TAG, "find private service");
                        mFizzoPrivateCMDCharacteristic = gattService.getCharacteristic(BleConfig.UUID_FIZZO_PRIVATE_CMD_C);
//                        Log.v(TAG,"mFizzoPrivateCMDCharacteristic ==null:" + (mFizzoPrivateCMDCharacteristic ==null));
                        mFizzoPrivateNotifyCharacteristic = gattService.getCharacteristic(BleConfig.UUID_FIZZO_PRIVATE_NOTIFY_C);
                    }

                    //发现电量服务
                    if (gattService.getUuid().equals(BleConfig.UUID_BATTERY_SERVICE)) {
                        mBatteryCharacteristic = gattService.getCharacteristic(BleConfig.UUID_BATTERY_C);
                    }
                }

                //若没有拿到心率C ,重新连接设备
                if (mHrCharacteristic == null || mFizzoPrivateCMDCharacteristic == null) {
                    repeatConnect(500);
                    return;
                }

                //重复读取Firmware
                mLocalHandler.sendEmptyMessage(MSG_REPEAT_READ_FIRMWARE);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int
                    status) {
//                Log.e(TAG, "<" + address + ">" + "characteristic--：" + "onCharacteristicRead:" + characteristic.getUuid() +",status:" + status);
                readCharacteristic(characteristic);

            }

            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                Log.e(TAG, "<" + address + ">" + "characteristic--：" + "onCharacteristicChanged");
                readCharacteristicOnChange(characteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                Log.v(TAG, "onCharacteristicWrite status:" + status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    writeCharacteristicSuccess(characteristic);
                } else {
                    writeCharacteristicFail(characteristic);
                }
            }
        };
    }


    /**
     * 读取生产厂家
     */
    public void readManufacturer() {
        boolean readMenu = mBluetoothGatt.readCharacteristic(mManufacturerCharacteristic);
        if (!readMenu) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_READ_MANUFACTURER, 500);
        }
    }

    /**
     * 停止同步
     */
    public void stopSync() {
        mSyncNow = false;
        mLocalHandler.sendEmptyMessage(MSG_REPEAT_STOP_SYNC_HISTORY_DATA);
    }

    /**
     * 重新开始同步
     */
    public void reStartSync() {
        mSyncLog = "";
        mCurrHistoryName = 0;
        mSyncNow = true;
        mHasSyncFinish = false;
        mHasGetHistoryNum = false;
        mLastStartTime = 0;
        mCurrIndex = 0;
//        mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_HISTORY_NUM);
        mLocalHandler.sendEmptyMessage(MSG_GET_STEP_HISTORY_COUNT);
        mLocalHandler.sendEmptyMessageDelayed(MSG_GET_STEP_TIME_OUT,OUT_OF_TIME_GET_STEP);
    }


    /**
     * 解析读取的信息
     *
     * @param characteristic
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        //读取生产厂家
        if (BleConfig.UUID_MANUFACTURER_NAME.equals(characteristic.getUuid())) {
            String manufacturerName = characteristic.getStringValue(0).trim();
//            Log.v(TAG, "manufacturerName:" + manufacturerName);
            //若是Fizzo手环, notify 私有服务
//            if (manufacturerName.equals("123Sports")) {
//            if (true) {
//                Message msg = new Message();
//                msg.what = MSG_REPEAT_NOTIFY_FIZZO_PRIVATE;
//                msg.obj = true;
//                mLocalHandler.sendMessage(msg);
//                //发出不是FIZZO手环的消息
//            } else {
//                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_NOT_FIZZO));
//            }
            return;
        }

        if (BleConfig.UUID_BATTERY_C.equals(characteristic.getUuid())) {
//            Log.v(TAG,"characteristic.getValue() != null:" + (characteristic.getValue() != null));
            if (characteristic.getValue() != null) {
                final int battery = characteristic.getValue()[0];
                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_NEW_BATTERY, battery));
                mCurrBattery = battery;
//                Log.e(TAG, "battery:" + battery);
                if (battery < BATTERY_LOW_POWER) {
                    mLocalHandler.sendEmptyMessage(MSG_SHOW_LOW_POWER);
                } else {
                    mNeedShowNotify = true;
                }
            }
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_BATTERY, READ_BATTERY_INTERVAL);
            return;
        }

        if (BleConfig.UUID_FIRMWARE_REVISION.equals(characteristic.getUuid())) {
            firmwareVer = characteristic.getStringValue(0).trim();
            Message msg = new Message();
            msg.what = MSG_REPEAT_NOTIFY_FIZZO_PRIVATE;
            msg.obj = true;
            mLocalHandler.sendMessage(msg);
            if (LocalApplication.getInstance().getLoginUser(mContext).bleMac.equals(address)) {
                postGetLeastVersion();
            }
            return;
        }
    }

    /**
     * 解析notify数据
     */
    private void readCharacteristicOnChange(BluetoothGattCharacteristic characteristic) {
        //心率数据
        if (BleConfig.UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            if (characteristic.getValue() != null) {
                byte[] data = characteristic.getValue();
                int hr = data[1] & 0xff;//心率
                int stepCount = 0;//步数
                int cadence = 0;//步频
                int speed = 0;//速度
                if (data.length > 4) {
                    stepCount = ((data[2] & 0xff) | ((data[3] & 0xff) << 8));
                    cadence = data[4] & 0xff;
                    speed = data[5] & 0xff;
                }
//                Log.e(TAG, "hr:" + hr + ",stepCount:" + stepCount + ",cadence:" + cadence);
//                SportSpData.setStepCount(mContext, stepCount);
                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_NEW_HEARTBEAT, hr, cadence, stepCount, speed));
            }
            return;
        }
        //私有服务notify
        if (BleConfig.UUID_FIZZO_PRIVATE_NOTIFY_C.equals(characteristic.getUuid())) {
            if (characteristic.getValue() != null) {
                byte[] data = characteristic.getValue();
                byte action = data[0];
                byte cmd = data[1];
                byte status = data[2];
                Log.v(TAG,"info:" + BleUtils.bytesToHexString(characteristic.getValue()));
//                Log.v(TAG, "action:" + action + ",cmd:" + cmd + "status:" + status);
                //若是设置命令
                if (action == BleConfig.ACTION_TAG_SETTING) {
                    switch (cmd) {
                        //设置UTC
                        case BleConfig.CMD_SETTING_UTC:
//                            if (status == BleConfig.PRIVATE_STATUS_SUCCESS) {
//                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_WRITE_USER_HR);
//                            } else {
//                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_WRITE_UTC);
//                            }
                            break;
                        //设置心率
                        case BleConfig.CMD_SETTING_HR_RANGE:
                            EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_SETTING_HR_RANGE_SUCCESS));
                            break;
                    }
                }
                //若是读命令
                if (action == BleConfig.ACTION_TAG_READ) {
                    switch (cmd) {
                        //读取健身模式
                        case BleConfig.CMD_READ_WORKOUT_MODE:
                            if (status == BleConfig.PRIVATE_STATUS_SUCCESS) {
                                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_GET_WORKOUT_MODE, data[3]));
                            } else {
                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_WORKOUT_MODE);
                            }
                            break;
                        //手动自动模式
                        case BleConfig.CMD_READ_AUTO_MODE:
                            if (status == BleConfig.PRIVATE_STATUS_SUCCESS) {
                                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_GET_AUTO_MODE, data[3]));
                            } else {
                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_AUTO_MODE);
                            }
                            break;
                        //读取当前步数
                        case BleConfig.CMD_READ_STEP_COUNT:
//                            Log.v(TAG,"data[3]:" + data[3]);
//                            Log.v(TAG,"data[4]:" + data[4]);
//                            Log.v(TAG,"data[5]:" + data[5]);
//                            Log.v(TAG,"data[6]:" + data[6]);
//                            Log.v(TAG,"data[2]:" + data[2]);

                            byte[] cStepCountB = new byte[]{data[6], data[5], data[4], data[3]};
                            long cStepCount = BleUtils.byteToLong(cStepCountB);
//                            Log.v(TAG, "cStepCount:" + cStepCount);
                            EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_CURR_STEP_COUNT, cStepCount));
                            break;
                    }
                }
                if (action == BleConfig.ACTION_TAG_OTA) {
                    switch (cmd) {
                        //准备升级
                        case BleConfig.CMD_READY_UPDATE:
//                            if (status == BleConfig.PRIVATE_STATUS_SUCCESS) {
                            EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_READY_OTA_OK));
//                            } else {
//                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_READY_UPDATE);
//                            }
                            break;
                    }
                    return;
                }

                if (action == BleConfig.ACTION_TAG_ACTIVE) {
                    switch (cmd) {
                        //敲击
                        case BleConfig.ACTION_CMD_ENTER:
                            if (status == BleConfig.PRIVATE_STATUS_SUCCESS) {
                                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_SHOCK_OK, address, name));
                            } else {
//                                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_SHOCK_OK, address, name));
//                                EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_SHOCK_ERROR, address, name));
                            }
                            break;
                    }
                    return;
                }
                //若是同步workout命令
                if (action == BleConfig.ACTION_TAG_SYNC_WORKOUT) {
                    if (mHasSyncFinish) {
                        return;
                    }
                    switch (cmd) {
                        //获取  workout数量
                        case BleConfig.CMD_SYNC_WORKOUT_GET_NUM:
                            int num = data[2] & 0xff;
                            Log.v(TAG, "history count :" + num);
                            mSyncLog += "\n" + TimeU.logTime() + "history count :" + num;
                            if (mHasGetHistoryNum) {
                                if (num == 0) {
                                    mHasSyncFinish = true;
                                    mSyncNow = false;
                                    EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH_WITHOUT_TOAST));
                                    mSyncLog += "\n" + TimeU.logTime() + "正常结束";
                                    writeTitleLog();
                                }
                                return;
                            }
                            mHasGetHistoryNum = true;
                            //若历史数量大于 0
                            if (num > 0) {
                                if (!mSyncNow) {
                                    mTotalSyncCount = num;
                                    mSyncNow = true;
                                }
                                mNeedSyncCount = num;
                                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_COUNT, num));
                                //若已经显示同步历史
                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_HISTORY_TITLE);
                            } else {
                                if (!mHasSyncFinish) {
                                    EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, num));
                                    mHasSyncFinish = true;
                                    mSyncLog += "\n" + TimeU.logTime() + "正常结束";
                                    writeTitleLog();
                                }
                                mSyncNow = false;
                                UserSPData.setLastSyncTime(mContext, TimeU.nowTime());
                            }
                            break;
                        //获取workout 头信息
                        case BleConfig.CMD_SYNC_WORKOUT_GET_TITLE:
                            Log.v(TAG, "title:" + BleUtils.bytesToHexString(characteristic.getValue()));
                            mSyncLog += "\n" + TimeU.logTime() + "title:" + BleUtils.bytesToHexString(characteristic.getValue());
                            byte[] startTimeB = new byte[]{data[9], data[8], data[7], data[6]};
                            byte[] stepCountB = new byte[]{data[13], data[12], data[11], data[10]};
                            mHistoryTitleBytes = startTimeB;
                            long startTime = BleUtils.byteToLong(startTimeB);
                            if (startTime == mCurrHistoryName) {
                                return;
                            }
                            mStepCount = BleUtils.byteToLong(stepCountB);
                            //若是一个新的历史
                            if (mLastStartTime != startTime) {
                                mCurrIndex = 0;
                                splitId = 0;
                                mAllHrSize = 0;
                                mAllHrTotal = 0;
                                heatbeatId = mLastStartTime * 1000;
                                mHistoryWatchData.clear();
                                mLastStartTime = startTime;
                                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_PERCENT, 0));
                                mTotalIndex = ((data[16] & 0xff) | ((data[17] & 0xff) << 8)) / 16 - 2;
                                Log.v(TAG, "mTotalIndex:" + mTotalIndex);
                                mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_HISTORY_DATA);
                            } else {
//                                mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_HISTORY_NUM, 14 * 1000);
                            }
//                            Log.v(TAG, "data[14]:" + data[14]);
//                            Log.v(TAG, "data[15]:" + data[15]);
//                            Log.v(TAG, "length:" + ((data[14] & 0xff) | ((data[15] & 0xff) << 8)));
//                            Log.v(TAG, "startTime:" + startTime);

                            break;
                        //历史信息
                        case BleConfig.CMD_SYNC_WORKOUT_GET_DATA:
                            mCurrIndex++;
//                            if (mCurrIndex % 30 == 0) {
//                                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_PERCENT, mCurrIndex * 80 / mTotalIndex));
//                            }
                            mHistoryWatchData.add(data);
                            break;
                        //结束一条历史的传输
                        case BleConfig.CMD_SYNC_WORKOUT_ACK_DATA_END:
                            mSyncLog += "\n" + TimeU.logTime() + "传输结束";
                            mHasGetHistoryNum = false;
                            mLocalHandler.sendEmptyMessage(MSG_ANALYSIS_WORKOUT);
                            break;
                        //删除回复
                        case BleConfig.CMD_SYNC_WORKOUT_DELETE_DATA:
                            mSyncLog += "\n" + TimeU.logTime() + "得到删除记录回复";
                            mLocalHandler.removeMessages(MSG_DELETE_OUT_OF_TIME);
                            mLocalHandler.sendEmptyMessage(MSG_REPEAT_GET_HISTORY_NUM);
                            break;
                        case BleConfig.CMD_SYNC_WORKOUT_ACK_DATA_ERROR:
                            mSyncNow = false;
                            EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, -1));
                            mSyncLog += "\n" + TimeU.logTime() + "错误结束";
                            writeTitleLog();
                            break;
                        //获取步数历史数量
                        case BleConfig.CMD_SYNC_GET_STEP_HISTORY_COUNT:
                            //若当前不可读步数历史数量
                            if ((data[3] == 0xff) && (data[4] == 0xff)) {
                                mHasSyncFinish = true;
                                mSyncNow = false;
                                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, -1));
                                return;
                            }
                            int stepHistoryCount = ((data[3] & 0xff) | ((data[4] & 0xff) << 8));
//                            Log.v(TAG, "stepHistoryCount:" + stepHistoryCount);
                            if (mHasGetStepCountNum) {
                                if (stepHistoryCount == 0) {
                                    mHasSyncFinish = true;
                                    mSyncNow = false;
                                    EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, 0));
                                    mSyncLog += "\n" + TimeU.logTime() + "正常结束";
                                }
                                return;
                            }
                            mHasGetStepCountNum = true;
                            //若历史数量大于 0
                            if (stepHistoryCount > 0) {
                                if (!mSyncNow) {
                                    mTotalStepHistoryCount = stepHistoryCount;
                                    mSyncNow = true;
                                }
                                mStepData.clear();
                                //若已经显示同步历史
                                mLocalHandler.sendEmptyMessage(MSG_GET_STEP_HISTORY_DATA);
                            } else {
                                mHasSyncFinish = true;
                                mSyncNow = false;
                                UserSPData.setLastSyncTime(mContext, TimeU.nowTime());
                                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH));
                            }
                            break;
                        //获取步数历史数据
                        case BleConfig.CMD_SYNC_GET_STEP_HISTORY_DATA:
                            //数据错误
                            if (data[3] == 0xff) {
                                mSyncNow = false;
                                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, -1));
                                return;
                            }
                            //数据收集结束
                            if (data[3] == 0x02) {
                                if (mStepData.size() == 0) {
                                    EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, 0));
                                } else {
                                    analysisStepData();
                                }
                                return;
                            }
                            //数据
                            if (data[3] == 0x00){
                                mStepData.add(data);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * 若写入成功
     *
     * @param characteristic
     */
    private void writeCharacteristicSuccess(BluetoothGattCharacteristic characteristic) {
//        Log.v(TAG,"writeCharacteristicSuccess");
    }

    /**
     * 若写入成功
     *
     * @param characteristic
     */
    private void writeCharacteristicFail(BluetoothGattCharacteristic characteristic) {
//        Log.v(TAG,"writeCharacteristicFail");
        //写入私有服务命令
        if (BleConfig.UUID_FIZZO_PRIVATE_NOTIFY_C.equals(characteristic.getUuid())) {
            byte[] data = characteristic.getValue();
            byte action = data[0];
            byte cmd = data[1];
            //若是设置失败
            if (action == BleConfig.ACTION_TAG_SETTING) {
                switch (cmd) {
                    case BleConfig.CMD_SETTING_UTC:
                        mLocalHandler.sendEmptyMessage(MSG_REPEAT_WRITE_UTC);
                        break;
                }
            }
        }
    }

    /**
     * 设置当指定characteristic值变化时，发出通知
     *
     * @param characteristic
     * @param enable
     */
    private boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        boolean notifySuccess = true;
        boolean writeDescriptor = true;
        notifySuccess = mBluetoothGatt.setCharacteristicNotification(characteristic, enable);

        if (enable) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID.fromString(BleConfig.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            writeDescriptor = mBluetoothGatt.writeDescriptor(descriptor);
        }
        return (notifySuccess && writeDescriptor);
    }


    /**
     * 写入UTC
     */
    private void writeUTC() {
        mLocalHandler.removeMessages(MSG_REPEAT_WRITE_UTC);
        long time = System.currentTimeMillis() / 1000;
//        Log.v(TAG,"time:" + time);
        byte[] timeB = new byte[6];
        timeB[0] = BleConfig.ACTION_TAG_SETTING;
        timeB[1] = BleConfig.CMD_SETTING_UTC;
        timeB[2] = (byte) (time & 0xFF);
        timeB[3] = (byte) ((time >> 8) & 0xFF);
        timeB[4] = (byte) ((time >> 16) & 0xFF);
        timeB[5] = (byte) ((time >> 24) & 0xFF);
        mFizzoPrivateCMDCharacteristic.setValue(timeB);
        boolean success = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        Log.v(TAG, "writeUTC success:" + success);
        if (!success) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_WRITE_UTC, INTERVAL_WRITE_DELAY);
        } else {
            mLocalHandler.sendEmptyMessage(MSG_REPEAT_WRITE_USER_HR);
        }
    }


    /**
     * 写入个人用户心率信息
     */
    public void writeUserHr() {
        mLocalHandler.removeMessages(MSG_REPEAT_WRITE_USER_HR);
        byte[] hrInfo = new byte[6];
        hrInfo[0] = BleConfig.ACTION_TAG_SETTING;
        hrInfo[1] = BleConfig.CMD_SETTING_HR_RANGE;
        hrInfo[2] = (byte) (LocalApplication.getInstance().getLoginUser(mContext).targetHrLow & 0xFF);
        hrInfo[3] = (byte) (LocalApplication.getInstance().getLoginUser(mContext).targetHrHigh & 0xFF);
        hrInfo[4] = (byte) (LocalApplication.getInstance().getLoginUser(mContext).alertHr & 0xFF);
        mFizzoPrivateCMDCharacteristic.setValue(hrInfo);
        boolean success = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        Log.v(TAG, "writeUserHr success:" + success);
        if (!success) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_WRITE_USER_HR, INTERVAL_WRITE_DELAY);
        } else {
            sendMsg(MSG_REPEAT_NOTIFY_HR, true, 500);
        }
    }


    /**
     * 控制光管
     *
     * @param open
     */
    public void controlLight(final boolean open) {
        mLocalHandler.removeMessages(MSG_REPEAT_CONTROL_LIGHT);

        byte[] control = new byte[3];
        control[0] = BleConfig.ACTION_TAG_SETTING;
        control[1] = BleConfig.CMD_LIGHT_CONTROL;
        if (open) {
            control[2] = (byte) (0x01);//开光管
        } else {
            control[2] = (byte) (0x00);//关光管
        }

        mFizzoPrivateCMDCharacteristic.setValue(control);
        boolean success = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
//        Log.v(TAG, "controlLight success:" + success + ",open:" + open);
        if (!success) {
            sendMsg(MSG_REPEAT_CONTROL_LIGHT, open, INTERVAL_WRITE_DELAY);
        }
    }

    /**
     * 读取手表历史的个数
     */
    private boolean getWatchHistoryNum() {
        if (mFizzoPrivateCMDCharacteristic == null) {
            mSyncNow = false;
            EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_GET_HISTORY_NUM_ERROR));
            return true;
        }
        byte[] getNum = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_WORKOUT_GET_NUM};
        mFizzoPrivateCMDCharacteristic.setValue(getNum);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
//        Log.v(TAG, "getWatchHistoryNum writeSuccess:" + writeSuccess);
        return writeSuccess;
    }

    /**
     * 读手表历史的头
     *
     * @return
     */
    private boolean getHistoryTitle() {
        mLocalHandler.removeMessages(MSG_REPEAT_GET_HISTORY_TITLE);
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_WORKOUT_GET_TITLE};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        return writeSuccess;
    }

    /**
     * 获取历史数据
     *
     * @return
     */
    private boolean getHistoryData() {
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_WORKOUT_GET_DATA,
                (byte) (mCurrIndex & 0xff), (byte) ((mCurrIndex >> 8) & 0xff)};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        return writeSuccess;
    }

    /**
     * 删除历史
     *
     * @return
     */
    private boolean deleteHistory() {
        mLocalHandler.removeMessages(MSG_DELETE_OUT_OF_TIME);
        mLocalHandler.sendEmptyMessageDelayed(MSG_DELETE_OUT_OF_TIME, OUT_OF_TIME_DELETE);
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_WORKOUT_DELETE_DATA,
                mHistoryTitleBytes[3], mHistoryTitleBytes[2], mHistoryTitleBytes[1], mHistoryTitleBytes[0]};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (writeSuccess) {
            mSyncLog += "\n" + TimeU.logTime() + "发送删除记录：" + BleUtils.bytesToHexString(mHistoryTitleBytes);
        }
        return writeSuccess;
    }

    /**
     * 停止同步历史
     *
     * @return
     */
    private boolean stopSyncHistoryData() {
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_WORKOUT_STOP_SYNC};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        return writeSuccess;
    }

    /**
     * 解析历史记录
     */
    private void analysisWorkoutData() {
        Log.v(TAG, "analysisWorkoutData");
        String startTime = TimeU.formatDateToStr(new Date(mLastStartTime * 1000), TimeU.FORMAT_TYPE_1);
        UserDE user = LocalApplication.getInstance().getLoginUser(mContext);
//        Log.v(TAG, "startTime:" + startTime);

        int duration = 0;
        int splitHrSize = 0;
        int splitHrTotal = 0;
//        Log.v(TAG, "mStepCount:" + mStepCount);
        WorkoutDE sport = SyncWatchDBData.createWatchWorkout(user.userId, startTime);
        LapDE session = SyncWatchDBData.createWatchLap(sport.startTime, user.userId);
        List<HrDE> hrList = new ArrayList<>();
//        Log.v(TAG, " mHistoryWatchData.size():" + mHistoryWatchData.size());

        for (int g = 0, sizeG = mHistoryWatchData.size(); g < sizeG; g++) {
            byte[] data = mHistoryWatchData.get(g);
            duration = ((data[12] & 0xff) | ((data[13] & 0xff) << 8)) - 10;
//            Log.v(TAG, "duration:" + duration);
            //ee fb fe 00 00 00 e2 86 5f 58 03 00 00 00 00 00 60 00 00 00
            //ee fb 29 29 2b 2d 2e 2e 2e 2f 2f 2f 1b 00 01 00 00 00 01 00
            for (int i = 2, size = 12; i < size; i++) {
                duration++;
                //若进入一个新的split
                if ((duration / 60) > splitId) {
                    int avgHeartbeat = 0;
                    if (splitHrSize != 0) {
                        avgHeartbeat = splitHrTotal / splitHrSize;
                    }
                    splitHrSize = 0;
                    splitHrTotal = 0;
                    for (int j = 0, sizeJ = (duration / 60) - splitId; j < sizeJ; j++) {
                        SyncWatchDBData.createWatchSplit(startTime, splitId, user.userId, avgHeartbeat, hrList, session);
                        sport.calorie += CalorieU.getMinutesCalorie(user, avgHeartbeat);
                        sport.effortPoint += EffortPointU.getMinutesEffortPoint(avgHeartbeat, user.maxHr);
                        hrList.clear();
                        splitId++;
                    }
                }
                int hr = (0x00ff & data[i]);

                int actionType = (int) data[14] & 0x0f;
                int actionCount = (data[14] >> 4 & 0x0f) | ((data[15] & 0xff) << 4);

//                Log.v(TAG,"actionType: "+ actionType + ",actionCount:"+ actionCount);
                HrDE heartbeat = new HrDE(heatbeatId++,
                        sport.startTime, session.startTime, duration,duration, hr, 0, 0, 0);
                heartbeat.actionCount = actionCount;
                heartbeat.actionType = actionType;

                hrList.add(heartbeat);
                if (sport.maxHr < hr) {
                    sport.maxHr = hr;
                }
                if (sport.minHr > hr) {
                    sport.minHr = hr;
                }
                splitHrSize++;
                splitHrTotal += hr;
                mAllHrSize++;
                mAllHrTotal += hr;
            }
        }
        if (hrList.size() > 0) {
            for (int j = 0, sizeJ = (duration / 60) - splitId; j < sizeJ; j++) {
                int avgHeartbeat;
                if (splitHrSize == 0) {
                    avgHeartbeat = 0;
                } else {
                    avgHeartbeat = splitHrTotal / splitHrSize;
                }
                SyncWatchDBData.createWatchSplit(startTime, splitId, user.userId, avgHeartbeat, hrList, session);
                sport.calorie += CalorieU.getMinutesCalorie(user, avgHeartbeat);
                sport.effortPoint += EffortPointU.getMinutesEffortPoint(avgHeartbeat, user.maxHr);
                hrList.clear();
                splitId++;
            }
        }
        EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_PERCENT, 98));
        // FINISH WORKOUT AND SESSION
        session.status = SportEnum.SportStatus.FINISH;
        SyncWatchDBData.finishWatchLap(session);

        sport.duration = duration;
        sport.status = SportEnum.SportStatus.FINISH;
        if (mAllHrSize == 0) {
            sport.avgHr = 0;
        } else {
            sport.avgHr = mAllHrTotal / mAllHrSize;
        }
        if (sport.minHr == 999) {
            sport.minHr = 0;
        }
        sport.startStep = 0;
        sport.endStep = (int) mStepCount;
        sport.totalHrSize = mAllHrSize;

        SyncWatchDBData.finishWatchWorkout(sport, mStepCount);
//        Log.v(TAG, "analysisWorkoutData over duration:" + duration);
        //解析完毕，准备上传网络
        mLocalHandler.sendEmptyMessage(MSG_UPDATE_DATA);

    }


    /**
     * 解析步数历史信息
     */
    private void analysisStepData() {
        Log.v(TAG, "analysisStepData");
        final JSONArray updateJson = new JSONArray();
        for (int i = 0; i < mStepData.size(); i++) {
            byte[] stepByte = mStepData.get(i);
            byte[] startTimeB = new byte[]{stepByte[7], stepByte[6], stepByte[5], stepByte[4]};
            byte[] stepCountB = new byte[]{stepByte[11], stepByte[10], stepByte[9], stepByte[8]};
            Log.v(TAG, "startTimeB:" + BleUtils.bytesToHexString(startTimeB));
            long utc = BleUtils.byteToLong(startTimeB) * 1000;
            Log.v(TAG,"utc:" + utc);
            String startTime = TimeU.formatDateToStr(new Date(utc), TimeU.FORMAT_TYPE_3);
            Log.v(TAG,"startTime:" + startTime);
            long stepCount = BleUtils.byteToLong(stepCountB);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", startTime);
            jsonObject.put("stepcount", stepCount);
            updateJson.add(jsonObject);
        }
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUpdateStepHistory(mContext,
                        UrlConfig.URL_UPLOAD_DAY_STEP_COUNTS, updateJson.toJSONString(),
                        LocalApplication.getInstance().getLoginUser(mContext).userId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        //上传成功
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH));
//                            mLocalHandler.sendEmptyMessage(MSG_DELETE_STEP_HISTORY_DATA);
                        } else {
                            EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, -1));
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_FINISH, -1));
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });

    }

    /**
     * 更新电池信息
     */
    public void readBattery() {
        mLocalHandler.removeMessages(MSG_REPEAT_GET_BATTERY);
        if (mBatteryCharacteristic != null) {
            boolean success = mBluetoothGatt.readCharacteristic(mBatteryCharacteristic);
            if (!success) {
                mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_BATTERY, INTERVAL_WRITE_DELAY);
            }
        }
    }


    /**
     * 写入工作模式
     */
    public void writeMode(final int mode) {
        if (mFizzoPrivateCMDCharacteristic == null) {
            return;
        }
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SETTING, BleConfig.CMD_READ_WORKOUT_MODE, (byte) (mode & 0xff)};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_WORKOUT_MODE, INTERVAL_WRITE_DELAY);
        }
    }


    /**
     * 获取工作模式
     */
    public void readWorkoutMode() {
        if (mFizzoPrivateCMDCharacteristic == null) {
            return;
        }
        byte[] data = new byte[]{BleConfig.ACTION_TAG_READ, BleConfig.CMD_READ_WORKOUT_MODE};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_WORKOUT_MODE, INTERVAL_WRITE_DELAY);
        }
    }

    /**
     * 获取自动模式
     */
    public void readAutoMode() {
        byte[] data = new byte[]{BleConfig.ACTION_TAG_READ, BleConfig.CMD_READ_AUTO_MODE};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_GET_AUTO_MODE, INTERVAL_WRITE_DELAY);
        }
    }


    /**
     * 发送准备升级的命令
     */
    public void readyUpdate() {
//        Log.v(TAG,"readyUpdate");
        byte[] data = new byte[]{BleConfig.ACTION_TAG_OTA, BleConfig.CMD_READY_UPDATE};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_READY_UPDATE, INTERVAL_WRITE_DELAY);
        }
    }

    /**
     * 让手环反馈震动
     */
    public void shock() {
//        Log.v(TAG, "shock");
        byte[] data = new byte[]{BleConfig.ACTION_TAG_ACTIVE, BleConfig.ACTION_CMD_ENTER};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_SHOCK, INTERVAL_WRITE_DELAY);
        }
    }


    /**
     * 读取当前步数
     */
    public void readStepCount() {
//        Log.v(TAG, "readStepCount");
        byte[] data = new byte[]{BleConfig.ACTION_TAG_READ, BleConfig.CMD_READ_STEP_COUNT};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_READ_CURR_STEP_COUNT, INTERVAL_WRITE_DELAY);
        }
    }


    /**
     * 读取步数历史数量
     */
    public void readStepHistoryCount() {
        Log.v(TAG, "readStepHistoryCount");
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_GET_STEP_HISTORY_COUNT};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_GET_STEP_HISTORY_COUNT, INTERVAL_WRITE_DELAY);
        }
    }

    /**
     * 读取步数历史数据
     */
    public void readStepHistoryData() {
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_GET_STEP_HISTORY_DATA};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_GET_STEP_HISTORY_DATA, INTERVAL_WRITE_DELAY);
        }
    }


    /**
     * 删除步数历史数据
     */
    public void deleteStepHistory() {
        Log.v(TAG,"deleteStepHistory");
        byte[] data = new byte[]{BleConfig.ACTION_TAG_SYNC_WORKOUT, BleConfig.CMD_SYNC_DELETE_STEP_HISTORY};
        mFizzoPrivateCMDCharacteristic.setValue(data);
        boolean writeSuccess = mBluetoothGatt.writeCharacteristic(mFizzoPrivateCMDCharacteristic);
        if (!writeSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_DELETE_STEP_HISTORY_DATA, INTERVAL_WRITE_DELAY);
        }
    }

    /**
     * 读取设备版本信息
     */
    public void readFirmVersion() {
        boolean readSuccess = mBluetoothGatt.readCharacteristic(mFirmwareCharacteristic);
        if (!readSuccess) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_READ_FIRMWARE, 200);
        }
    }


    /**
     * 重新连接
     */
    private void repeatConnect(final long delayTime) {
        if (!mNeedConnect) {
            return;
        }
        mLocalHandler.removeMessages(MSG_REPEAT_CONNECT);
        mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_CONNECT, delayTime);
    }

    /**
     * 重新连接
     */
    private void repeatDiscover(final long delayTime) {
        mLocalHandler.removeMessages(MSG_REPEAT_DISCOVER);
        mLocalHandler.sendEmptyMessageDelayed(MSG_REPEAT_DISCOVER, delayTime);
    }

    /**
     * 执行获取服务失败
     */
    private void doRepeatDiscover() {
        Log.v(TAG, "MSG_REPEAT_DISCOVER");
        if (mBluetoothGatt != null){
            mBluetoothGatt.discoverServices();
            repeatDiscover(7000);
        }else {
            repeatConnect(0);
        }


//        mHasWriteUTC = false;
//        if (mBluetoothGatt != null) {
//            disConnect();
//            mBluetoothGatt.close();
//            mBluetoothGatt = null;
//        }
//        initCallback();
//        mBluetoothGatt = mBluetoothAdapter.getRemoteDevice(address)
//                .connectGatt(mContext, false, mGattCallback);

//        disConnect();
//        BleManager.getBleManager().destroy();
//        BleManager.getBleManager().replaceConnect(address);
    }

    /**
     * 主动断开连接
     */
    public void disConnect() {
        refreshDeviceCache();
        mNeedConnect = false;
        mIsConnected = false;
        mGattCallback = null;
        EventBus.getDefault().post(new BleConnectEE(BleManager.MSG_DISCONNECT));
        destroyHandler();
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    /**
     * 销毁Handler
     */
    private void destroyHandler() {
        if (mLocalHandler != null) {
            mLocalHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Method to clear the device cache
     *
     * @return boolean
     */
    public boolean refreshDeviceCache() {
//        Log.v(TAG, "refreshDeviceCache");
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                Log.i(TAG, "An exception occured while refreshing device");
            }
        }
        return false;
    }

    /**
     * 发送消息给Handler
     *
     * @param what
     * @param object
     * @param delay
     */
    private void sendMsg(final int what, final Object object, final long delay) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = object;
        mLocalHandler.sendMessageDelayed(msg, delay);
    }

    /**
     * 显示低电量window
     */
    private void showLowPowerWindow() {
//        Log.e(TAG,"showLowPowerWindow");
        if (LocalApplication.getInstance().needShowLowPowerWindow) {
            WindowUtils.showPopupWindow(mContext);
        }
    }


    private void showLowPower() {
        showBannerToast();
        if (mNeedShowNotify) {
            showLowPowerNotification();
            mNeedShowNotify = false;
        }
    }

    /**
     * 显示低电量的通知
     */
    private void showLowPowerNotification() {
        Log.v(TAG, "showLowPowerNotification");
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("FIZZO COR ")//设置通知栏标题
                .setContentText("手环电量只剩" + mCurrBattery + "%, 请及时充电") //
                .setTicker("") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果
                .setSmallIcon(R.drawable.ic_battery_low)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
        mNotificationManager.notify(1000, mBuilder.build());
    }

    private void showBannerToast() {
        TipViewController mTipViewController;
        mTipViewController = new TipViewController(mContext.getApplicationContext(), "");
        mTipViewController.show();
    }

    /**
     * 获取最新的版本信息
     */
    private void postGetLeastVersion() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new MyRequestParams(mContext, UrlConfig.URL_GET_LEAST_FIRMWARE_VERSION);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (BaseResponseParser.ERROR_CODE_NONE == result.errorcode) {
                            GetLatestFirmwareRE mLatestFirmwareRE = JSON.parseObject(result.result, GetLatestFirmwareRE.class);
                            //若必须升级
                            if (StringU.mustUpdateForSupportWatch(firmwareVer, MyBuildConfig.SUPPORT_WATCH_VERSION)) {
                                sendMsg(MSG_NEED_UPDATE_FIRMWARE, mLatestFirmwareRE.ftpurl, 0);
                                return;
                            }

//                            //需要升级
//                            if (StringU.firmwareNeedUpdate(firmwareVer, mLatestFirmwareRE.name)) {
//                                sendMsg(MSG_NEED_UPDATE_FIRMWARE, mLatestFirmwareRE.ftpurl, 0);
//                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 上传同步数据
     */
    private void uploadSyncWatchData() {
        final List<SyncWatchDE> syncList = SyncWatchDBData.getWorkoutData(LocalApplication.getInstance()
                .getLoginUser(mContext).userId);
        //若没有需要同步的数据
        if (syncList == null || syncList.size() == 0) {
            EventBus.getDefault().post(new WorkoutEndEE());
            mSyncLog += "\n" + TimeU.logTime() + "上传记录成功";
            mLocalHandler.sendEmptyMessage(MSG_REPEAT_DELETE_HISTORY_DATA);
            return;
        } else {
            mSyncLog += "\n" + TimeU.logTime() + "上传记录," + "startTime:" + syncList.get(0).workoutStartTime;
        }
        String uploadString = "[";
        for (SyncWatchDE uploadDE : syncList) {
            uploadString += uploadDE.info + ",";
        }
        uploadString = uploadString.substring(0, uploadString.length() - 1);
        uploadString += "]";

        RequestParams requestParams = RequestParamsBuilder.buildSaveWorkoutSegmentArrayRP(mContext
                , UrlConfig.URL_SAVE_DATA_SEGMENT_ARRAY, uploadString);
//        Log.v(TAG,"postWorkout:" + uploadEntity.info);
        mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

            @Override
            public void onSuccess(BaseRE s) {
                if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    SyncWatchDBData.delete(syncList);
                    mLocalHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                } else {
                    SyncWatchDBData.delete(syncList);
                    String msg = s.errormsg;
                    mSyncNow = false;
                    EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_SYNC_ERROR, msg));
                    mSyncLog += "\n" + TimeU.logTime() + "上传记录发生错误";
                    writeTitleLog();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                //同步失败
                String msg = HttpExceptionHelper.getErrorMsg(throwable);
                mSyncNow = false;
                EventBus.getDefault().post(new SyncWatchWorkoutEE(SyncWatchWorkoutEE.MSG_SYNC_ERROR, msg));
                mSyncLog += "\n" + TimeU.logTime() + "上传记录发生错误";
                writeTitleLog();
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 写入日志
     */
    private void writeTitleLog() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                // Save the log on SD card if available
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    String sdcardPath = FileConfig.SYNC_PATH;
                    File crashPath = new File(sdcardPath);
                    if (!crashPath.exists()) {
                        crashPath.mkdir();
                    }

                    String logInfo = mSyncLog;
                    FileU.writeTxt(logInfo, sdcardPath + "/");
                    mSyncLog = "";
                }
            }
        });
    }
}
