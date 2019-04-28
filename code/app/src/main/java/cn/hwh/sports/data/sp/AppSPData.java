package cn.hwh.sports.data.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.hwh.sports.config.SPEnum;

/**
 * Created by Raul.Fan on 2017/4/22.
 */

public class AppSPData {

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 保存app升级信息
     *
     * @param context
     * @param info
     */
    public static void setAppUpdateInfo(final Context context, final String info) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPEnum.SP_UPDATE_APP, info);
        editor.commit();
    }

    /**
     * 获取app升级信息
     *
     * @param context
     * @return
     */
    public static String getAppUpdateInfo(final Context context) {
        return getSharedPreferences(context).getString(SPEnum.SP_UPDATE_APP, "");
    }


    /**
     * 设置是否需要提示升级
     * @param context
     * @param isTip
     */
    public static void setAppUpdateNeedTip(final Context context, final boolean isTip){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPEnum.SP_UPDATE_APP_NEED_TIP, isTip);
        editor.commit();
    }

    /**
     * 获取是否需要升级
     * @param context
     * @return
     */
    public static boolean getAppUpdateNeedTip(final Context context){
        return getSharedPreferences(context).getBoolean(SPEnum.SP_UPDATE_APP_NEED_TIP, true);
    }

}
