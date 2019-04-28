package cn.hwh.sports.network;

import android.content.Context;
import android.support.annotation.Nullable;

import org.xutils.http.RequestParams;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hwh.sports.activity.settings.EventTargetSetActivity;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.utils.Log;

/**
 * Created by Administrator on 2016/6/28.
 * 创建网络交互的参数
 */
public class RequestParamsBuilder {


    /**
     * APP启动标记参数
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildMarkStartupRP(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("deviceos", MyBuildConfig.DEVICEOS);
        requestParams.addBodyParameter("appversion", UrlConfig.USER_ANGENT);
        return requestParams;
    }


    /**
     * 上传Crash参数
     *
     * @param context
     * @param info
     * @param time
     * @return
     */
    public static RequestParams buildReportCrashRP(final Context context, final String url,
                                                   final String info, final String time) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String timeStr = "";
        try {
            timeStr = df.format(new Date(Long.parseLong(time)));
        } catch (NumberFormatException ex) {
            timeStr = time + "";
        }
        requestParams.addBodyParameter("debuginfo", info);
        requestParams.addBodyParameter("deviceos", "android " + android.os.Build.VERSION.RELEASE);
        requestParams.addBodyParameter("deviceinfo", android.os.Build.MODEL);
        requestParams.addBodyParameter("appversion", MyBuildConfig.Version);
        requestParams.addBodyParameter("time", timeStr);
        return requestParams;
    }

    /*- workout 相关 -*/

    /**
     * 上传跑步记录
     *
     * @param context
     * @param url
     * @param uploadInfo
     * @return
     */
    public static RequestParams buildSaveWorkoutSegmentRP(final Context context,
                                                          final String url, final String uploadInfo) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("workout", uploadInfo);
        return requestParams;
    }

    /**
     * 上传跑步记录列表
     *
     * @param context
     * @param url
     * @param uploadInfo
     * @return
     */
    public static RequestParams buildSaveWorkoutSegmentArrayRP(final Context context,
                                                               final String url, final String uploadInfo) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("workout", uploadInfo);
        return requestParams;
    }

    /**
     * 更改记录名称
     * @param context
     * @param url
     * @param workoutId
     * @param name
     * @return
     */
    public static RequestParams buildUpdateWorkoutName(final Context context,final String url,
                                                       final int workoutId,final String name){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", workoutId+"");
        requestParams.addBodyParameter("name", name);
        return requestParams;
    }

    /**
     * 获取锻炼记录列表
     *
     * @param context
     * @param url
     * @param userId
     * @param date
     * @param limit
     * @return
     */
    public static RequestParams buildGetWorkoutListRP(final Context context, final String url,
                                                      final int userId, final String date, final String limit) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        requestParams.addBodyParameter("date", date);
        requestParams.addBodyParameter("limit", limit);
        return requestParams;
    }

    /*- 登录 ， 注册 , 修改用户信息 --*/

    /**
     * 微信登录
     *
     * @param context
     * @param url
     * @param unionId
     * @param gender
     * @param avatar
     * @param pushId
     * @param nickName
     * @return
     */
    public static RequestParams buildLoginWithWxRP(final Context context, final String url, final String unionId,
                                                   final int gender, final String avatar, final String pushId,
                                                   final String nickName) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("unionid", unionId);
        requestParams.addBodyParameter("gender", gender + "");
        requestParams.addBodyParameter("avatar", avatar);
        requestParams.addBodyParameter("devicepushid", pushId);
        requestParams.addBodyParameter("deviceos", "2");
        requestParams.addBodyParameter("nickname", nickName);
        requestParams.addBodyParameter("appversion", MyBuildConfig.Version);
        return requestParams;
    }


    /**
     * 通过手机号验证码登录
     *
     * @param context
     * @param url
     * @param phone
     * @param code
     * @param pushId
     * @return
     */
    public static RequestParams buildLoginByVcCodeRP(final Context context, final String url, final String phone,
                                                     final String code, final String pushId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("mobile", phone);
        requestParams.addBodyParameter("code", code);
        requestParams.addBodyParameter("devicepushid", pushId);
        requestParams.addBodyParameter("deviceos", "2");
        requestParams.addBodyParameter("appversion", MyBuildConfig.Version);
        return requestParams;
    }


    /**
     * 绑定手机号
     *
     * @param context
     * @param url
     * @param phone
     * @param code
     * @param sessionId
     * @return
     */
    public static RequestParams buildBindPhoneRP(final Context context, final String url, final int userId,
                                                 final String phone, final String code, final String sessionId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("userid", userId + "");
        requestParams.addBodyParameter("sessionid", sessionId);
        requestParams.addBodyParameter("mobile", phone);
        requestParams.addBodyParameter("vcode", code);
        return requestParams;
    }

    /**
     * 普通登录
     *
     * @param context
     * @param url
     * @param phoneNum
     * @param password
     * @param devicepushid
     * @param Deviceos
     * @return
     */
    public static RequestParams buildCommonLoginRequest(final Context context, final String url,
                                                        final String phoneNum, final String password,
                                                        final String devicepushid, final String Deviceos) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("name", phoneNum);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("devicepushid", devicepushid);
        requestParams.addBodyParameter("deviceos", Deviceos);
        requestParams.addBodyParameter("appversion", UrlConfig.USER_ANGENT);
        return requestParams;
    }

    /**
     * 获取验证码
     *
     * @param context
     * @param url
     * @param phoneNum
     * @return
     */
    public static RequestParams buildGetVCodeRP(final Context context, final String url, final String phoneNum) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("mobile", phoneNum);
        return requestParams;
    }

    /**
     * 检查手机号码是否符合服务要求
     *
     * @param context
     * @param url
     * @param phoneNum
     * @return
     */
    public static RequestParams buildCheckPhoneRP(final Context context, final String url, final String phoneNum) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("name", phoneNum);
        return requestParams;
    }

    /**
     * 验证手机验证码并修改密码
     *
     * @param Mobile 手机号
     * @param Code   验证码
     * @param pwd    密码
     * @return
     */
    public static RequestParams buildResetPwdRP(final Context context, final String url,
                                                final String Mobile, final String Code, final String pwd) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("mobile", Mobile);
        requestParams.addBodyParameter("code", Code);
        requestParams.addBodyParameter("password", pwd);
        return requestParams;
    }

    /**
     * 验证手机验证码是否正确
     *
     * @param Mobile 手机号
     * @param Code   验证码
     * @return
     */
    public static RequestParams buildCheckVCCodeRP(final Context context, final String url,
                                                   final String Mobile, final String Code) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("mobile", Mobile);
        requestParams.addBodyParameter("code", Code);
        return requestParams;
    }

    /**
     * 注册用户
     *
     * @param context
     * @param url
     * @param userName
     * @param pwd
     * @param nickname
     * @param avatar
     * @param devicePushId
     * @param weight
     * @param height
     * @param birthday
     * @param province
     * @param city
     * @return
     */
    public static RequestParams buildRegisterRP(final Context context, final String url,
                                                final String userName, final String pwd, final String nickname,
                                                final String avatar, final String devicePushId,
                                                final float weight, final int height, final String birthday,
                                                final String province, final String city, final int gender) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("name", userName);
        requestParams.addBodyParameter("nickname", nickname);
        requestParams.addBodyParameter("password", pwd);
        requestParams.addBodyParameter("avatar", avatar);
        requestParams.addBodyParameter("devicepushid", devicePushId);
        requestParams.addBodyParameter("deviceos", MyBuildConfig.DEVICEOS);
        requestParams.addBodyParameter("appversion", UrlConfig.USER_ANGENT);
        requestParams.addBodyParameter("weight", weight + "");
        requestParams.addBodyParameter("height", height + "");
        requestParams.addBodyParameter("gender", gender + "");
        requestParams.addBodyParameter("birthdate", birthday);
        requestParams.addBodyParameter("locationprovince", province);
        requestParams.addBodyParameter("locationcity", city);
        return requestParams;
    }


    /**
     * 获取用户信息
     *
     * @param context
     * @param url
     * @param toId
     * @return
     */
    public static RequestParams buildGetUserInfoRP(final Context context, final String url, final int toId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(toId));
        return requestParams;
    }


    /**
     * 获取用户健康信息汇总
     *
     * @param context
     * @param url
     * @param toId
     * @return
     */
    public static RequestParams buildGetDayHealthSummaryRP(final Context context, final String url, final int toId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(toId));
        return requestParams;
    }

    /**
     * 上传用户头像
     *
     * @param context
     * @param url
     * @param avatarF
     * @return
     */
    public static RequestParams buildUploadAvatarRP(final Context context, final String url,
                                                    final File avatarF) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("upload", avatarF);
        return requestParams;
    }

    /**
     * 加入门店
     *
     * @param context
     * @param url
     * @param store
     * @return
     */
    public static RequestParams buildLoginStoreRP(final Context context, final String url, final int store) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", store + "");
        return requestParams;
    }


    /**
     * 通过关键字找门店
     *
     * @param context
     * @param url
     * @param storeName
     * @return
     */
    public static RequestParams BuildSearchStoreParams(final Context context, final String url, final String storeName) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storename", storeName);
        return requestParams;
    }

    /**
     * 获取用户每日运动汇总
     *
     * @param context
     * @param url
     * @param fromDay 查询开始日，格式：2016-11-17
     * @param toDay   查询结束日，格式：2016-11-19，不填写就是默认今天
     * @return
     */
    public static RequestParams buildGetDayMotionSummaryRP(final Context context, final String url, final String fromDay,
                                                           final String toDay, final int searchId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("fromday", fromDay);
        if (toDay != null) {
            requestParams.addBodyParameter("to_day", toDay);
        }
        requestParams.addBodyParameter("id", String.valueOf(searchId));
        return requestParams;
    }


    /**
     * 获取用户每日锻炼目标
     *
     * @param context
     * @param url
     * @param searchId 待查用户id
     * @return
     */
    public static RequestParams buildGetDailyExerciseTargetRP(final Context context, final String url, final int searchId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(searchId));
        return requestParams;
    }

    /**
     * 设置用户每日锻炼目标
     *
     * @param context
     * @param url
     * @param updateId
     * @param value
     * @param type
     * @return
     */
    public static RequestParams buildUpdateDailyExerciseTargetRP(final Context context, final String url,
                                                                 final int updateId, final String value, final int type) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(updateId));
        if (type == EventTargetSetActivity.TYPE_STEP) {
            requestParams.addBodyParameter("stepcount", value);
        }
        if (type == EventTargetSetActivity.TYPE_LENGTH) {
            requestParams.addBodyParameter("length", value);
        }
        if (type == EventTargetSetActivity.TYPE_CALORIE) {
            requestParams.addBodyParameter("calorie", value);
        }
        if (type == EventTargetSetActivity.TYPE_POINT) {
            requestParams.addBodyParameter("effort_point", value);
        }
        if (type == EventTargetSetActivity.TYPE_SPORT_TIME) {
            requestParams.addBodyParameter("exercise_minutes", value);
        }
        return requestParams;
    }

    /**
     * 获取用户每周锻炼目标
     *
     * @param context
     * @param url
     * @param searchId
     * @return
     */
    public static RequestParams buildGetWeeklyExerciseTargetRP(final Context context, final String url, final int searchId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(searchId));
        return requestParams;
    }

    /**
     * 设置用户每周锻炼目标
     *
     * @param context
     * @param url
     * @param updateId
     * @param weekDays
     * @return
     */
    public static RequestParams buildUpdateWeeklyExerciseTargetRP(final Context context, final String url, final int updateId, final int weekDays) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(updateId));
        requestParams.addBodyParameter("exercise_days", String.valueOf(weekDays));
        return requestParams;
    }

    /**
     * 获取用户体征目标
     *
     * @param context
     * @param url
     * @param searchId
     * @return
     */
    public static RequestParams buildGetCharacteristicTargetRP(final Context context, final String url, final int searchId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(searchId));
        return requestParams;
    }

    /**
     * 设置用户体征目标
     *
     * @param context
     * @param url
     * @param updateId
     * @param weight
     * @param fatrate
     * @return
     */
    public static RequestParams buildUpdateCharacteristicTargetRP(final Context context, final String url, final int updateId, final float weight, final float fatrate) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(updateId));

        if (weight != 0) {
            requestParams.addBodyParameter("weight", String.valueOf(weight));
        }
        if (fatrate != 0) {
            requestParams.addBodyParameter("fatrate", String.valueOf(fatrate));
        }
        return requestParams;
    }

    /**
     * 获取用户每日睡眠目标
     *
     * @param context
     * @param url
     * @param searchId
     * @return
     */
    public static RequestParams buildGetDailySleepTargetRP(final Context context, final String url, final int searchId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(searchId));
        return requestParams;
    }

    /**
     * 设置用户每日睡眠目标
     *
     * @param context
     * @param url
     * @param updateId
     * @param minutes
     * @return
     */
    public static RequestParams buildUpdateDailySleepTargetRP(final Context context, final String url, final int updateId, final int minutes) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(updateId));
        requestParams.addBodyParameter("minutes", String.valueOf(minutes));
        return requestParams;
    }

    /**
     * 修改用户信息
     *
     * @param context
     * @param url
     * @param avatar
     * @return
     */
    public static RequestParams buildUpdateUserInfoRP(final Context context, final String url, final String avatar, final int gender,
                                                      final String province, final String city, final int height, final float weight,
                                                      final String birthday, final String nickname, final int maxhr, final int resthr) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        if (avatar != null) {
            requestParams.addBodyParameter("avatar", avatar);
        }
        if (gender != 0) {
            requestParams.addBodyParameter("gender", String.valueOf(gender));
        }
        if (province != null && city != null) {
            requestParams.addBodyParameter("locationprovince", province);
            requestParams.addBodyParameter("locationcity", city);
        }
        if (height != 0) {
            requestParams.addBodyParameter("height", String.valueOf(height));
        }
        if (weight != 0) {
            requestParams.addBodyParameter("weight", String.valueOf(weight));
        }
        if (birthday != null) {
            requestParams.addBodyParameter("birthdate", birthday);
        }
        if (nickname != null) {
            requestParams.addBodyParameter("nickname", nickname);
        }
        if (maxhr != 0) {
            requestParams.addBodyParameter("maxhr", String.valueOf(maxhr));
        }
        if (resthr != 0) {
            requestParams.addBodyParameter("resthr", String.valueOf(resthr));
        }
        return requestParams;
    }

    /**
     * 获取用户每日步数
     *
     * @param context
     * @param url
     * @param searchId
     * @param fromday
     * @return
     */
    public static RequestParams buildGetDayStepCountRP(final Context context, final String url,
                                                       final int searchId, final String fromday) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(searchId));
        requestParams.addBodyParameter("fromday", fromday);
        return requestParams;
    }


    /**
     * 获取教练下的学员列表
     *
     * @param context
     * @param url
     * @param userId
     * @param searchStr
     * @return
     */
    public static RequestParams BuildSearchMoverParams(final Context context, final String url,
                                                       final int userId, final String searchStr) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        requestParams.addBodyParameter("name", searchStr);
        return requestParams;
    }

    /**
     * 添加用户体重数据
     *
     * @param context
     * @param url
     * @param userId
     * @param weight  体重 -1时代表不需改此项
     * @param fatRate 体制率 -1时代表不需改此项
     * @return
     */
    public static RequestParams buildAddRecordWeightParamRP(final Context context, final String url, final int userId, final String date,
                                                            final float weight, final float fatRate) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(userId));
        requestParams.addBodyParameter("day", date);
        if (weight != -1) {
            requestParams.addBodyParameter("weight", String.valueOf(weight));
        }
        if (fatRate != -1) {
            requestParams.addBodyParameter("fatrate", String.valueOf(fatRate));
        }
        return requestParams;
    }

    /**
     * 添加用户睡眠记录
     *
     * @param context
     * @param url
     * @param userId
     * @param starttime
     * @param finishtime
     * @return
     */
    public static RequestParams buildAddRecordSleepParamRP(final Context context, final String url,
                                                           final int userId, final String starttime, final String finishtime) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", String.valueOf(userId));
        requestParams.addBodyParameter("starttime", starttime);
        requestParams.addBodyParameter("finishtime", finishtime);
        return requestParams;
    }

    /**
     * 获取本周运动天数
     *
     * @param context
     * @param url
     * @param userId
     * @return
     */
    public static RequestParams buildGetWeekExercisedDays(final Context context, final String url, final int userId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        return requestParams;
    }

    /**
     * 获取锻炼记录详情
     *
     * @param context
     * @param url
     * @param workoutId
     * @return
     */
    public static RequestParams buildGetWorkoutInfoRP(final Context context, final String url, final int workoutId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", workoutId + "");
        return requestParams;
    }

    /**
     * 解除绑定
     *
     * @param context
     * @param url
     * @param userId
     * @return
     */
    public static RequestParams buildRemoveDevice(final Context context, final String url, final int userId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        return requestParams;
    }

    /**
     * 更新用户绑定设备
     *
     * @param context
     * @param url
     * @param mac
     * @param name
     * @param userId
     * @return
     */
    public static RequestParams buildUpdateBleDevice(final Context context, final String url,
                                                     final String mac, final String name, final int userId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("macaddr", mac);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("id", userId + "");
        return requestParams;
    }


    /**
     * 获取学员数量变化
     *
     * @param context
     * @param url
     * @param userId
     * @return
     */
    public static RequestParams buildGetMoverDayCount(final Context context, final String url, final int userId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        return requestParams;
    }


    /**
     * 获取学员天锻炼点数变化
     *
     * @param context
     * @param url
     * @param userId
     * @return
     */
    public static RequestParams buildGetMoverDayEffort(final Context context, final String url, final int userId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        return requestParams;
    }

    /**
     * 获取微信分享链接
     *
     * @param context
     * @param url
     * @param id
     * @return
     */
    public static RequestParams buildGetWeixinShareWorkoutText(final Context context, final String url, final int id) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", id + "");
        return requestParams;
    }


    /**
     * 获取更新用户心率
     *
     * @param context
     * @param url
     * @param targetLow
     * @param targetHigh
     * @param alertHr
     * @return
     */
    public static RequestParams buildUpdateUserHrRP(final Context context, final String url,
                                                    final String targetLow, final String targetHigh, final String alertHr) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("targethrlow", targetLow);
        requestParams.addBodyParameter("targethrhigh", targetHigh);
        requestParams.addBodyParameter("alerthr", alertHr);
        return requestParams;

    }

    /**
     * 提交建议评论
     *
     * @param context
     * @param url
     * @param advise
     * @return
     */
    public static RequestParams buildPublishAdvise(final Context context, final String url, final String advise) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("comment", advise);
        return requestParams;
    }


    /**
     * 获取文章列表请求
     *
     * @param context
     * @param url
     * @param phoneMode
     * @param uiVer
     * @return
     */
    public static RequestParams buildGetArticleList(final Context context, final String url,
                                                    final String phoneMode, final String uiVer) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("phonemodel", phoneMode);
        requestParams.addBodyParameter("uiversion", uiVer);
        return requestParams;
    }

    /**
     * 上传历史步数信息
     * @param context
     * @param url
     * @param days
     * @param userId
     * @return
     */
    public static RequestParams buildUpdateStepHistory(final Context context, final String url, final String days, final int userId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("days", days);
        requestParams.addBodyParameter("id", userId + "");
        return requestParams;
    }


    /**
     * 获取日历信息
     * @param context
     * @param url
     * @param userId
     * @param startDay
     * @param endDay
     * @return
     */
    public static RequestParams buildGetCalendarData(final Context context,final String url,
                                                     final int userId, final String startDay, final String endDay){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        requestParams.addBodyParameter("from_day", startDay);
        requestParams.addBodyParameter("to_day", endDay);
        return requestParams;
    }

    /**
     * 上传个人实时心率
     * @param context
     * @param url
     * @param userId
     * @param bpm
     * @return
     */
    public static RequestParams buildUploadRecentHr(final Context context,final String url,final int userId ,final int bpm){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", userId + "");
        requestParams.addBodyParameter("bpm", bpm + "");
        return requestParams;
    }
}
