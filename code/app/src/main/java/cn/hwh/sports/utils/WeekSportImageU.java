package cn.hwh.sports.utils;

import java.util.List;

import cn.hwh.sports.R;

/**
 * Created by Administrator on 2016/11/18.
 */

public class WeekSportImageU {
    private static int[][] mTargetAll = {{R.drawable.ic_week_sport_1_0, R.drawable.ic_week_sport_1_1},
            {R.drawable.ic_week_sport_2_0, R.drawable.ic_week_sport_2_1, R.drawable.ic_week_sport_2_2},
            {R.drawable.ic_week_sport_3_0, R.drawable.ic_week_sport_3_1, R.drawable.ic_week_sport_3_2, R.drawable.ic_week_sport_3_3},
            {R.drawable.ic_week_sport_4_0, R.drawable.ic_week_sport_4_1, R.drawable.ic_week_sport_4_2, R.drawable.ic_week_sport_4_3, R.drawable.ic_week_sport_4_4},
            {R.drawable.ic_week_sport_5_0, R.drawable.ic_week_sport_5_1, R.drawable.ic_week_sport_5_2, R.drawable.ic_week_sport_5_3, R.drawable.ic_week_sport_5_4, R.drawable.ic_week_sport_5_5},
            {R.drawable.ic_week_sport_6_0, R.drawable.ic_week_sport_6_1, R.drawable.ic_week_sport_6_2, R.drawable.ic_week_sport_6_3, R.drawable.ic_week_sport_6_4, R.drawable.ic_week_sport_6_5, R.drawable.ic_week_sport_6_6},
            {R.drawable.ic_week_sport_7_0, R.drawable.ic_week_sport_7_1, R.drawable.ic_week_sport_7_2, R.drawable.ic_week_sport_7_3, R.drawable.ic_week_sport_7_4, R.drawable.ic_week_sport_7_5, R.drawable.ic_week_sport_7_6, R.drawable.ic_week_sport_7_7}};


    private static int[][] mTargetDarkAll = {{R.drawable.ic_week_target_1_0, R.drawable.ic_week_target_1_1},
            {R.drawable.ic_week_target_2_0, R.drawable.ic_week_target_2_1, R.drawable.ic_week_target_2_2},
            {R.drawable.ic_week_target_3_0, R.drawable.ic_week_target_3_1, R.drawable.ic_week_target_3_2, R.drawable.ic_week_target_3_3},
            {R.drawable.ic_week_target_4_0, R.drawable.ic_week_target_4_1, R.drawable.ic_week_target_4_2, R.drawable.ic_week_target_4_3, R.drawable.ic_week_target_4_4},
            {R.drawable.ic_week_target_5_0, R.drawable.ic_week_target_5_1, R.drawable.ic_week_target_5_2, R.drawable.ic_week_target_5_3, R.drawable.ic_week_target_5_4, R.drawable.ic_week_target_5_5},
            {R.drawable.ic_week_target_6_0, R.drawable.ic_week_target_6_1, R.drawable.ic_week_target_6_2, R.drawable.ic_week_target_6_3, R.drawable.ic_week_target_6_4, R.drawable.ic_week_target_6_5, R.drawable.ic_week_target_6_6},
            {R.drawable.ic_week_target_7_0, R.drawable.ic_week_target_7_1, R.drawable.ic_week_target_7_2, R.drawable.ic_week_target_7_3, R.drawable.ic_week_target_7_4, R.drawable.ic_week_target_7_5, R.drawable.ic_week_target_7_6, R.drawable.ic_week_target_7_7}};


    /**
     * 获取指定目标及完成天数的图标
     *
     * @param weekTarget 1～7
     * @param sportDay   当weekTarget == 1 时 sportDay =（0～1）
     * @return int image Resource
     */
    public static int getWeekSportImage(int weekTarget, int sportDay) {
        if (weekTarget <= 0 || weekTarget > 7) {
            return mTargetAll[0][0];
        }
        if (sportDay < 0) {
            return mTargetAll[0][0];
        }
        if (sportDay > weekTarget) {
            return mTargetAll[weekTarget - 1][mTargetAll[weekTarget - 1].length - 1];
        }
        return mTargetAll[weekTarget - 1][sportDay];
    }

    public static int getWeekTargetImage(int weekTarget, int sportDay) {
        if (weekTarget <= 0 || weekTarget > 7) {
            return mTargetDarkAll[0][0];
        }
        if (sportDay < 0) {
            return mTargetDarkAll[0][0];
        }
        if (sportDay > weekTarget) {
            return mTargetDarkAll[weekTarget - 1][mTargetAll[weekTarget - 1].length - 1];
        }
        return mTargetDarkAll[weekTarget - 1][sportDay];
    }
}
