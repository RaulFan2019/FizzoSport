package cn.hwh.sports.utils;

import android.graphics.Color;

import cn.hwh.sports.R;

/**
 * Created by Administrator on 2016/7/22.
 */
public class ColorU {


    /**
     * 通过心率强度获取颜色
     *
     * @param percent
     * @return
     */
    public static int getColorByHeartbeat(final int percent) {
        if (percent < 50) {
            return Color.parseColor("#cfcfcf");
        } else if (percent < 60) {
            return Color.parseColor("#00debf");
        } else if (percent < 70) {
            return Color.parseColor("#98e961");
        } else if (percent < 80) {
            return Color.parseColor("#ffb300");
        } else if (percent < 90) {
            return Color.parseColor("#ff7603");
        } else {
            return Color.parseColor("#fe482f");
        }
    }

    /**
     * 通过心率强度获取颜色
     *
     * @param percent
     * @return
     */
    public static int getBgByHeartbeat(final int percent) {
        if (percent < 50) {
            return R.drawable.bg_zone_1;
        } else if (percent < 60) {
            return R.drawable.bg_zone_2;
        } else if (percent < 70) {
            return R.drawable.bg_zone_3;
        } else if (percent < 80) {
            return R.drawable.bg_zone_4;
        } else if (percent < 90) {
            return R.drawable.bg_zone_5;
        } else {
            return R.drawable.bg_zone_6;
        }
    }

    /**
     * 通过区间获取颜色
     * @param zone
     * @return
     */
    public static int getColorByZone(final int zone){
        if (zone == 0){
            return Color.parseColor("#cfcfcf");
        }else if (zone == 1){
            return Color.parseColor("#00debf");
        }else if (zone == 2){
            return Color.parseColor("#98e961");
        }else if (zone == 3){
            return Color.parseColor("#ffb300");
        }else if (zone == 4){
            return Color.parseColor("#ff7603");
        }else {
            return Color.parseColor("#fe482f");
        }
    }
}
