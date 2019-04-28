package cn.hwh.sports.data.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.hwh.sports.config.SPEnum;
import cn.hwh.sports.config.SportEnum;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class SportSpData {


    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 获取运动TTS开关状态
     *
     * @param context
     * @return
     */
    public static boolean getTtsEnable(final Context context) {
        return getSharedPreferences(context).getBoolean(SPEnum.SP_TTS, true);
    }

    /**
     * 设置运动TTS开关状态
     *
     * @param context
     * @param isOpen
     */
    public static void setTtsEnable(final Context context, final boolean isOpen) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPEnum.SP_TTS, isOpen);
        editor.commit();
    }

    /**
     * 获取运动TTS开关状态
     *
     * @param context
     * @return
     */
    public static boolean getAlertTtsEnable(final Context context) {
        return getSharedPreferences(context).getBoolean(SPEnum.SP_ALERT_TTS, true);
    }

    /**
     * 设置运动TTS开关状态
     *
     * @param context
     * @param isOpen
     */
    public static void setAlertTtsEnable(final Context context, final boolean isOpen) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPEnum.SP_ALERT_TTS, isOpen);
        editor.commit();
    }

    /**
     * 获取实时心率播报频率
     *
     * @param context
     * @return
     */
    public static int getCurrHrFreqTts(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_CURR_HR_FREQ_TTS, 1);
    }

    /**
     * 设置实时心率播报频率
     *
     * @param context
     * @param freq
     */
    public static void setCurrHrFreqTts(final Context context, final int freq) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_CURR_HR_FREQ_TTS, freq);
        editor.commit();
    }



    /**
     * 设置运动模式选择习惯
     * @param context
     * @param index
     */
    public static void setLastSportSelectIndex(final Context context,final int index){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_SPORT_SELECT_INDEX, index);
        editor.commit();
    }

    /**
     * 获取运动模式选择习惯
     * @param context
     * @return
     */
    public static int getLastSportSelectIndex(final Context context){
        return getSharedPreferences(context).getInt(SPEnum.SP_SPORT_SELECT_INDEX, 1);
    }

    /**
     * 获取用户运动目标习惯
     *
     * @param context
     * @return
     */
    public static int getEffortCustomTargetMode(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_EFFORT_TARGET_TYPE, SportEnum.TargetType.FAT);
    }

    /**
     * 设置用户运动目标习惯
     *
     * @param context
     * @param type
     */
    public static void setEffortCustomTargetMode(final Context context, final int type) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_EFFORT_TARGET_TYPE, type);
        editor.commit();
    }


    /**
     * 获取用户运动时间
     *
     * @param context
     * @return
     */
    public static int getEffortCustomTargetTime(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_EFFORT_TARGET_TIME, 30);
    }

    /**
     * 设置用户运动时间
     *
     * @param context
     * @param time
     */
    public static void setEffortCustomTargetTime(final Context context, final int time) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_EFFORT_TARGET_TIME, time);
        editor.commit();
    }


    public static void setStepRecordDate(final Context context, final String date) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPEnum.SP_STEP_RECORD_DATE, date);
        editor.commit();
    }

    public static String getStepRecordDate(final Context context) {
        return getSharedPreferences(context).getString(SPEnum.SP_STEP_RECORD_DATE, "");

    }

    public static void setStepCount(final Context context, final int count) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnum.SP_STEP_RECORD_COUNT, count);
        editor.commit();
    }

    public static int getStepCount(final Context context) {
        return getSharedPreferences(context).getInt(SPEnum.SP_STEP_RECORD_COUNT, 0);
    }

}
