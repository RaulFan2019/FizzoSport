package cn.hwh.sports.utils;

import android.graphics.Color;

/**
 * Created by Raul.Fan on 2017/4/19.
 */

public class HrZoneU {


    /**
     * 通过心率强度获取颜色
     *
     * @param percent
     * @return
     */
    public static String getDescribeByHrPercent(final int percent) {
        if (percent < 50) {
            return "非锻炼";
        } else if (percent < 60) {
            return "热身运动";
        } else if (percent < 70) {
            return "有氧减脂";
        } else if (percent < 80) {
            return "增强心肺";
        } else if (percent < 90) {
            return "提升耐力";
        } else {
            return "竞技训练";
        }
    }


    /**
     * 通过心率强度获取区间
     *
     * @param percent
     * @return
     */
    public static int getZoneByHrPercent(final int percent) {
        if (percent < 50) {
            return 0;
        } else if (percent < 60) {
            return 1;
        } else if (percent < 70) {
            return 2;
        } else if (percent < 80) {
            return 3;
        } else if (percent < 90) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 通过心率强度获取颜色
     *
     * @param zone
     * @return
     */
    public static String getDescribeByHrZone(final int zone) {
        if (zone == 0) {
            return "非锻炼";
        } else if (zone == 1) {
            return "热身运动";
        } else if (zone == 2) {
            return "有氧减脂";
        } else if (zone == 3) {
            return "增强心肺";
        } else if (zone == 4) {
            return "提升耐力";
        } else {
            return "竞技训练";
        }
    }
}
