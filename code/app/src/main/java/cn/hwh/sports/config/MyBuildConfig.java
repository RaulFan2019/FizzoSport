package cn.hwh.sports.config;

/**
 * Created by Raul.Fan on 2016/11/10.
 */

public class MyBuildConfig {


    /**
     * 是否跳转到Test页面
     */
    public static final boolean TEST = false;

    /**
     * 本次Build的类型
     */
    public static final int BUILD_TYPE = AppEnum.BuildType.BUILD_BETA;

    /**
     * App 的版本信息
     * 用于发送给服务器版本信息
     */
    public static final String Version = "FIZZO Ver.2.2.4";


    /**
     *  app版本号
     */
    public static final int VersionCode = 16;

    /**
     * 数据库版本号
     */
    public static final int DB_VERSION = 41;


    /**
     * 手表支持的版本号
     */
    public static final String SUPPORT_WATCH_VERSION = "1.2";


    /**
     * 数据库名称
     */
    public static final String DB_NAME = "fizo.db";

    /**
     * 是否是DEBUG
     */
    public static final boolean DEBUG = false;

    /**
     * Android 设备类型
     */
    public static final String DEVICEOS = "2";

}
