package cn.hwh.sports.data.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Random;

import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.SPEnum;
import cn.hwh.sports.utils.Log;

/**
 * Created by Administrator on 2016/6/27.
 * 和用户相关的 Preference数据处理
 */
public class UserSPData {


    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 保存登录用户ID
     *
     * @param context
     * @param userId
     */
    public static void setUserId(final Context context, final int userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_USER_ID, userId);
        editor.commit();
    }

    /**
     * 获取登录用户ID
     *
     * @param context
     * @return
     */
    public static int getUserId(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_USER_ID, AppEnum.DEFAULT_USER_ID);
    }


    /**
     * 获取上次进入boot状态的时间
     * @param context
     * @return
     */
    public static long getBootResumeTime(final Context context){
        return getSharedPreferences(context).getLong(SPEnum.SP_BOOT_STATE, 0);
    }


    /**
     * 设置进入boot的时间
     * @param context
     * @param bootTime
     */
    public static void setBootResumeTime(final Context context, final long bootTime){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(SPEnum.SP_BOOT_STATE, bootTime);
        editor.commit();
    }

    /**
     * 设置用户名
     *
     * @param context
     * @param userName
     */
    public static void setUserName(final Context context, final String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPEnum.SP_USER_NAME, userName);
        editor.commit();
    }

    /**
     * 获取用户名
     *
     * @param context
     * @return
     */
    public static String getUserName(final Context context) {
        return getSharedPreferences(context).getString(SPEnum.SP_USER_NAME, "");
    }

    /**
     * 设置用户名
     *
     * @param context
     * @param pwd
     */
    public static void setUserPwd(final Context context, final String pwd) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        char[] oldChars = pwd.toCharArray();
        char[] newChars = new char[oldChars.length * 3];
        for (int i = 0, size = oldChars.length; i < size; i++) {
            newChars[i * 3] = (char) (oldChars[i] + i);
            newChars[i * 3 + 1] = (char) new Random(70).nextInt();
            newChars[i * 3 + 2] = (char) new Random(70).nextInt();
        }
        String result = new String(newChars);
//        Log.v("setUserPwd", "result" + result);
        editor.putString(SPEnum.SP_USER_PWD, result);
        editor.commit();
    }

    /**
     * 获取用户名
     *
     * @param context
     * @return
     */
    public static String getUserPwd(final Context context) {
        String pwd = getSharedPreferences(context).getString(SPEnum.SP_USER_PWD, "");
        char[] oldChars = pwd.toCharArray();
        char[] newChars = new char[oldChars.length / 3];
        for (int i = 0, size = newChars.length; i < size; i++) {
            newChars[i] = (char) (oldChars[i * 3] - i);
        }

        String result = new String(newChars);
//        Log.v("getUserPwd", "result:" + result);
        return result;
    }

    /**
     * 获取是否有用户已登陆
     *
     * @param context
     * @return
     */
    public static boolean hasLogin(final Context context) {
        return getSharedPreferences(context).getBoolean(SPEnum.SP_HAS_LOGIN, false);
    }

    /**
     * 设置用户是否已登录
     *
     * @param context
     * @param hasLogin
     */
    public static void setHasLogin(final Context context, final boolean hasLogin) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPEnum.SP_HAS_LOGIN, hasLogin);
        editor.commit();
    }

    /**
     * 设置用户头像地址
     *
     * @param context
     * @param avatar
     */
    public static void setUserAvatar(final Context context, final String avatar) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPEnum.SP_USER_AVATAR, avatar);
        editor.commit();
    }

    /**
     * 获取用户头像地址
     *
     * @param context
     * @return
     */
    public static String getUserAvatar(final Context context) {
        return getSharedPreferences(context).getString(SPEnum.SP_USER_AVATAR, "");
    }


    /**
     * 设置用户昵称
     *
     * @param context
     * @param nickName
     */
    public static void setUserNickName(final Context context, final String nickName) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPEnum.SP_USER_NICK_NAME, nickName);
        editor.commit();
    }

    /**
     * 获取用户昵称
     *
     * @param context
     * @return
     */
    public static String getUserNickName(final Context context) {
        return getSharedPreferences(context).getString(SPEnum.SP_USER_NICK_NAME, "");
    }

    /**
     * 设置手机类型
     *
     * @param context
     * @param type
     */
    public static void setPhoneType(final Context context, final int type) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_PHONE_TYPE, type);
        editor.commit();
    }

    /**
     * 获取手机类型
     *
     * @param context
     * @return
     */
    public static int getPhoneType(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_PHONE_TYPE, AppEnum.PhoneType.DEFAULT);
    }

    /**
     * 保存用户的权限角色
     *
     * @param context
     * @param role
     */
    public static void setUserRole(final Context context, final int role) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_USER_ROLE, role);
        editor.commit();
    }

    /**
     * 获取当前用户的权限角色
     *
     * @param context
     * @return
     */
    public static int getUserRole(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_USER_ROLE, AppEnum.UserRoles.NORMAL);
    }

    /**
     * 保存当前用户当前角色页面
     *
     * @param context
     * @param role    权限 【决定于刚进入时显示的页面】
     */
    public static void setUserPage(final Context context, final int role) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_USER_PAGE, role);
        editor.commit();
    }

    /**
     * 获取当前用户要显示的页面权限
     *
     * @param context
     * @return
     */
    public static int getUserPage(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_USER_PAGE, AppEnum.UserRoles.NO_ROLE);
    }

    /**
     * 获取是否显示角色切换的提示
     *
     * @param context
     * @return
     */
    public static boolean getShowRoleChangeTip(final Context context) {
        return getSharedPreferences(context).getBoolean(SPEnum.SP_SHOW_CHANGE_ROLE_TIP, true);
    }

    /**
     * 设置是否显示角色切换的提示
     *
     * @param context
     * @param hasShow
     */
    public static void setShowRoleChangeTip(final Context context, final boolean hasShow) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPEnum.SP_SHOW_CHANGE_ROLE_TIP, hasShow);
        editor.commit();
    }

    /**
     * 获取是否显示角色切换的提示
     *
     * @param context
     * @return
     */
    public static String getLastSyncTime(final Context context) {
        return getSharedPreferences(context).getString(SPEnum.SP_SYNC_TIME, "");
    }

    /**
     * 设置是否显示角色切换的提示
     *
     * @param context
     * @param syncTime
     */
    public static void setLastSyncTime(final Context context, final String syncTime) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPEnum.SP_SYNC_TIME, syncTime);
        editor.commit();
    }

}
