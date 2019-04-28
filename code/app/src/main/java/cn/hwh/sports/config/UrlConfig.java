package cn.hwh.sports.config;

/**
 * Created by Raul.Fan on 2016/11/10.
 */

public class UrlConfig {

    /**
     * HTTP 头信息
     */
    public static final String USER_ANGENT = "Android " + android.os.Build.VERSION.RELEASE + ";"
            + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + ";" + MyBuildConfig.Version;

    /**
     * 根据编译版本获取WebView Host IP 地址
     *
     * @return
     */
    public static String getWebHostIp() {
        if (MyBuildConfig.BUILD_TYPE == AppEnum.BuildType.BUILD_ALPHA) {
            return "http://121.43.113.78/fizzoh5/";
        } else {
            return "http://www.123yd.cn/fizzoh5/";
        }
    }

    /**
     * 根据编译版本获取Host Ip 地址
     *
     * @return
     */
    public static String getHostIp() {
        //若编译版本为 Alpha 版本
        if (MyBuildConfig.BUILD_TYPE == AppEnum.BuildType.BUILD_ALPHA) {
            return "http://121.43.113.78/xingjiansport/V3/";
        } else {
            return "http://www.123yd.cn/xingjiansport/V3/";
        }
    }

    //上传Crash日志信息
    public static final String URL_REPORT_CRASH = getHostIp() + "Text/saveDebugInfo";

    /* 登录 ， 注册 , 修改用户信息*/
    //普通登录
    public static final String URL_COMMON_LOGIN = getHostIp() + "User/login";
    //微信登录
    public static final String URL_WX_LOGIN = getHostIp() + "User/loginWithWeixin";
    //验证码登录
    public static final String URL_LOGIN_BY_VC_CODE = getHostIp() + "User/loginByVerificationCode";
    //绑定手机号
    public static final String URL_BIND_PHONE = getHostIp() + "User/bindByMobileVcode";


    //检查手机号码是否已注册
    public static final String URL_CHECK_PHONE_IS_REGISTER = getHostIp() + "User/checkUserNameIsExist";

    //获取短信验证码
    public static final String URL_GET_VC_CODE = getHostIp() + "Sms/sendSms";

    //重置密码
    public static final String URL_RESET_PWD = getHostIp() + "User/resetPasswordWithVerificationCode";

    //检查验证码
    public static final String URL_CHECK_VC_CODE = getHostIp() + "Sms/verificationCode";

    //注册
    public static final String URL_REGISTER = getHostIp() + "User/registerUser/";

    //获取个人信息
    public static final String URL_GET_USER_INFO = getHostIp() + "User/getUserInfo/";

    //获取个人健康信息汇总
    public static final String URL_GET_HEALTH_SUMMARY = getHostIp() + "Health/getHealthSummary";

    //上传头像
    public static final String URL_UPLOAD_AVATAR = getHostIp() + "Avatar/upload";

    //进入门店
    public static final String URL_LOGIN_STORE = getHostIp() + "User/joinFitnessStore";

    //查询门店
    public static final String URL_QUERY_STORE = getHostIp() + "User/queryFitnessStore";

    //获取用户每日运动汇总-
    public static final String URL_GET_DAY_MOTION_SUMMARY = getHostIp() + "User/getDayMotionSummary";

    public static final String URL_PUBLISH_ADVISE = getHostIp() + "User/publishComment";

    //获取文章列表
    public static final String URL_GET_ARTICLE_LIST = getHostIp() + "Article/getArticleList";


    //获取日历信息
    public static final String URL_GET_CALENDAR_DATA = getHostIp() + "Workout/getWorkoutCalender";

    //获取用户每日锻炼目标
    public static final String URL_GET_DAILY_EXERCISE_TARGET = getHostIp() + "User/getDailyExerciseTarget";

    //设置用户每日锻炼目标
    public static final String URL_UPDATE_DAILY_EXERCISE_TARGET = getHostIp() + "User/updateDailyExerciseTarget";

    //获取用户每周锻炼项目目标
    public static final String URL_GET_WEEKLY_EXERCISE_TARGET = getHostIp() + "User/getWeeklyExerciseTarget";

    //s设置用户每周锻炼目标
    public static final String URL_UPDATE_WEEKLY_EXERCISE_TARGET = getHostIp() + "User/updateWeeklyExerciseTarget";

    //获取用户体征目标
    public static final String URL_GET_CHARACTERISTIC_TARGET = getHostIp() + "User/getCharacteristicTarget";

    //设置用户体征目标
    public static final String URL_UPDATE_CHARACTERISTIC_TARGET = getHostIp() + "User/updateCharacteristicTarget";

    //获取用户每日睡眠目标
    public static final String URL_GET_DAILY_SLEEP_TARGET = getHostIp() + "User/getDailySleepTarget";

    //设置用户每日睡眠目标
    public static final String URL_UPDATE_DAILY_SLEEP_TARGET = getHostIp() + "User/updateDailySleepTarget";

    //更新用户基本信息
    public static final String URL_UPDATE_USER_INFO = getHostIp() + "User/updateUserInfo/";

    //添加用户体重数据
    public static final String URL_ADD_DAY_WEIGHT = getHostIp() + "User/addDayWeight";

    //添加用户每日睡眠数据
    public static final String URL_ADD_DAY_SLEEP = getHostIp() + "User/addDaySleep";

    //获取每日步数
    public static final String URL_GET_DAY_STEP_COUNT = getHostIp() + "User/getDayStepcount";

    /* 运动记录相关 */
    public static final String URL_SAVE_DATA_SEGMENT = getHostIp() + "Workout/saveWorkoutSegment/";

    public static final String URL_SAVE_DATA_SEGMENT_ARRAY = getHostIp() + "Workout/saveWorkoutSegmentArray";

    public static final String URL_UPDATE_WORKOUT_NAME = getHostIp() + "Workout/updateWorkoutName";

    /* 教练模块 */
    public static final String URL_LIST_MOVER = getHostIp() + "Coach/listMover";


    //获取本周运动天数
    public static final String URL_GET_WEEK_EXERCISED_DAYS = getHostIp() + "User/getWeekExercisedDays";

    //获取锻炼记录列表
    public static final String URL_GET_WORKOUT_LIST = getHostIp() + "Workout/getUserWorkoutList";

    //上传每日步数
    public static final String URL_UPLOAD_DAY_STEP_COUNTS = getHostIp() + "Health/uploadDayStepcounts";
    public static final String URL_UPLOAD_RECENT_HR = getHostIp() + "User/uploadRecentHeartrate";

    //获取锻炼记录详情
    public static final String URL_GET_WORKOUT_INFO = getHostIp() + "Workout/getWorkoutBriefInfo";

    public static final String URL_GET_WORKOUT_DETAIL_INFO = getHostIp() + "Workout/getWorkoutDetailInfo";

    //获取微信分享锻炼记录
    public static final String URL_GET_WE_CHAT_SHARE_WORKOUT_TEXT = getHostIp() + "Text/getWeixinShareWorkoutText";

    //修改用户绑定设备
    public static final String URL_UPDATE_HR_DEVICE = getHostIp() + "User/updateUserHrDevice";
    //去除用户设备绑定
    public static final String URL_REMOVE_DEVICE = getHostIp() + "User/removeUserHrDevice";

    /* 教练 */
    //获取学员数量
    public static final String URL_GET_MOVER_DAY_COUNT = getHostIp() + "Coach/getDayMoverCount";
    //获取运动点数
    public static final String URL_GET_MOVER_DAY_EFFORT = getHostIp() + "Coach/getDayEffortPoint";

    /* 设备 */
    public static final String URL_GET_LEAST_FIRMWARE_VERSION = getHostIp() + "Firmware/getLatestFirmware";


    /*  检查app版本 */
    public static final String URL_CHECK_UPDATE = getHostIp() + "Androidrelease/getLatestRelease";


}
