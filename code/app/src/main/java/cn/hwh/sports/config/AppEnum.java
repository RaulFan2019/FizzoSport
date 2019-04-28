package cn.hwh.sports.config;

/**
 * Created by Raul.Fan on 2016/11/10.
 */

public class AppEnum {

    public static final int DEFAULT_USER_ID = 14;//默认User Id , user id 为0时是游客帐号
    public static final String DEFAULT_CHECK_ERROR = "";//检查没有错误时，字符串返回结果

    /**
     * App Build 类型配置
     */
    public class BuildType {
        public static final int BUILD_ALPHA = 1;// 内部测试版
        public static final int BUILD_BETA = 2;// 对外测试版
        public static final int BUILD_RELEASE = 3;// 对外正式版
    }

    /**
     * 上传类型
     */
    public class UploadType{
        public static final int WORKOUT = 0x01;//workout
        public static final int UPDATE_BLE_DEVICE = 0x02;//更新个人蓝牙设备信息
        public static final int REMOVE_DEVICE = 0x03;//更新个人蓝牙设备信息
        public static final int UPDATE_USER_HR = 0x04;//更新个人心率信息
    }

    /**
     * Gps 设置
     */
    public class Gps {
        public static final int TIME_FREQ = 2000;// 时间频率
        public static final int LENGTH_FREQ = 5;// 距离频率

        public static final int ACCURACY_POWER_HIGH = 50;//GPS强弱判断标准
        public static final int ACCURACY_POWER_LOW = 250;//GPS强弱判断标准
    }

    /**
     * 手机类型
     */
    public class PhoneType {
        public static final int DEFAULT = 0;
        public static final int SAMSUNG = 1;
        public static final int HUAWEI = 2;
        public static final int XIAOMI = 3;
        public static final int OPPO = 4;
        public static final int VIVO = 5;
        public static final int COOLPAD = 6;
        public static final int QIKU = 7;
        public static final int GIONEE = 8;
        public static final int MEIZU = 9;
        public static final int LENOVO = 10;
        public static final int HTC = 11;
        public static final int ONEPLUS = 8;
        public static final int SMARTISAN = 8;
    }

    /**
     * 用户性别
     */
    public class UserGender{
        public static final int MAN = 1;//男
        public static final int WOMEN = 2;//女
    }

    /**
     * 用户权限
     */
    public class UserRoles{
        public static final int NO_ROLE = 0;//初次登录时
        public static final int NORMAL = 1;
        public static final int COACH = 2;
        public static final int MANAGE = 3;
    }
}
