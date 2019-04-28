package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2016/12/19.
 * 获取周锻炼天数
 */
public class GetWeekExercisedDaysRE {

    /**
     * week_target_days : 4
     * week_exercised_days : 1
     * week_exercised_minutes : 6
     */

    public int week_target_days;
    public int week_exercised_days;
    public int week_exercised_minutes;

    public int getWeek_target_days() {
        return week_target_days;
    }

    public void setWeek_target_days(int week_target_days) {
        this.week_target_days = week_target_days;
    }

    public int getWeek_exercised_days() {
        return week_exercised_days;
    }

    public void setWeek_exercised_days(int week_exercised_days) {
        this.week_exercised_days = week_exercised_days;
    }

    public int getWeek_exercised_minutes() {
        return week_exercised_minutes;
    }

    public void setWeek_exercised_minutes(int week_exercised_minutes) {
        this.week_exercised_minutes = week_exercised_minutes;
    }
}
