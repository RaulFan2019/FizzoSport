package cn.hwh.sports.ble;

import java.util.UUID;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class BleConfig {


//    04-20 19:24:37.786 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 00001800-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.786 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 00001801-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.786 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 0000180d-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.786 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 0000c0e0-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.786 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 0000c0d0-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.787 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 0000180f-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.787 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 0000180a-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.787 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 0000c0b0-0000-1000-8000-00805f9b34fb
//    04-20 19:24:37.787 8130-8358/cn.hwh.sports E/BleConnectEntity: <FC:F1:A8:B5:E3:3F>UUID 00001530-1212-efde-1523-785feabcd123


    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    /* 心率相关 */
    public static final String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static final String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_HEART_RATE_SERVICE = UUID
            .fromString(HEART_RATE_SERVICE);
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
            .fromString(HEART_RATE_MEASUREMENT);


    /* Device Info */
    public static final String DEVICE_INFO_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    //制造商
    public static final String MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
    //Firmware 版本
    public static final String FIRMWARE_REVISION = "00002a26-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_DEVICE_INFO_SERVICE = UUID
            .fromString(DEVICE_INFO_SERVICE);
    public final static UUID UUID_MANUFACTURER_NAME = UUID
            .fromString(MANUFACTURER_NAME);
    public final static UUID UUID_FIRMWARE_REVISION = UUID
            .fromString(FIRMWARE_REVISION);

    /* 电量相关 */
    public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String BATTERY_C = "00002a19-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_BATTERY_SERVICE = UUID
            .fromString(BATTERY_SERVICE);
    public final static UUID UUID_BATTERY_C = UUID
            .fromString(BATTERY_C);

    /* private Fizzo service */
    public static final String FIZZO_PRIVATE_SERVICE = "0000c0d0-0000-1000-8000-00805f9b34fb";
    public static final String FIZZO_PRIVATE_CMD_C = "0000c0d1-0000-1000-8000-00805f9b34fb";       //写入命令的特征值
    public static final String FIZZO_PRIVATE_NOTIFY_C = "0000c0d2-0000-1000-8000-00805f9b34fb";   //notify命令结果的特征值

    public static final byte ACTION_TAG_SETTING = (byte) 0xdd;                           //TAG 设置
    public static final byte CMD_SETTING_UTC = (byte) 0xfa;                              //CMD UTC
    public static final byte CMD_SETTING_HR_RANGE = (byte) 0xfb;                        //CMD 心率范围
    public static final byte CMD_LIGHT_CONTROL = (byte) 0xfc;                           //CMD 光管控制

    public static final byte ACTION_TAG_SYNC_WORKOUT = (byte) 0xee;                    //TAG 同步记录
    public static final byte CMD_SYNC_WORKOUT_GET_NUM = (byte) 0xfc;                   //CMD 获取记录数量
    public static final byte CMD_SYNC_WORKOUT_GET_TITLE = (byte) 0xfa;                 //CMD 获取头信息
    public static final byte CMD_SYNC_WORKOUT_GET_DATA = (byte) 0xfb;                  //CMD 获取数据
    public static final byte CMD_SYNC_WORKOUT_DELETE_DATA = (byte) 0xfd;               //CMD 删除记录
    public static final byte CMD_SYNC_WORKOUT_STOP_SYNC = (byte) 0xf9;                 //CMD 停止同步
    public static final byte CMD_SYNC_GET_STEP_HISTORY_COUNT = (byte) 0xa0;           //CMD 获取步数历史数量
    public static final byte CMD_SYNC_GET_STEP_HISTORY_DATA = (byte) 0xa1;             //CMD 获取步数数据
    public static final byte CMD_SYNC_DELETE_STEP_HISTORY = (byte) 0xa3;               //CMD 删除步数历史数据

    public static final byte CMD_SYNC_WORKOUT_ACK_DATA_END = (byte) 0xee;
    public static final byte CMD_SYNC_WORKOUT_ACK_DATA_ERROR = (byte) 0xbb;

    public static final byte ACTION_TAG_READ = (byte) 0xcc;
    public static final byte CMD_READ_AUTO_MODE = (byte) 0xfd;
    public static final byte CMD_READ_WORKOUT_MODE = (byte) 0xfc;
    public static final byte CMD_READ_STEP_COUNT = (byte) 0xd1;//读取当前步数

    public static final byte ACTION_TAG_OTA = (byte) 0xbb;                                //TAG OTA
    public static final byte CMD_READY_UPDATE = (byte) 0xfc;                              //CMD 准备升级
    public static final byte CMD_READY_UPDATE_120_LOW = (byte) 0xfe;                     //CMD 1.2以下准备升级

    public static final byte ACTION_TAG_ACTIVE = (byte) 0xaa;                             //TAG 激活
    public static final byte ACTION_CMD_ENTER = (byte) 0xfe;                              //CMD 敲击


    public static final byte WORK_AUTO_YES = 0x02;//自动
    public static final byte WORK_AUTO_NO = 0x01;//手动
    public static final byte WORK_MODE_EFFORT = 0x02;//健身
    public static final byte WORK_MODE_RUNNING = 0x01;//跑步模式


    public final static UUID UUID_FIZZO_PRIVATE_SERVICE = UUID
            .fromString(FIZZO_PRIVATE_SERVICE);
    public final static UUID UUID_FIZZO_PRIVATE_CMD_C = UUID
            .fromString(FIZZO_PRIVATE_CMD_C);
    public final static UUID UUID_FIZZO_PRIVATE_NOTIFY_C = UUID
            .fromString(FIZZO_PRIVATE_NOTIFY_C);


    public final static byte PRIVATE_STATUS_SUCCESS = 0;

    public static final int TYPE_SOFTWARE = 1;
}
