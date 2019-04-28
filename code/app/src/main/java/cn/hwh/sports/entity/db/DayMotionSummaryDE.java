package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/11/24.
 * 用户每日运动汇总 即主页数据
 */

@Table(name = "DayMotionSummary")
public class DayMotionSummaryDE {

    @Column(name = "dayMotionSummaryId",isId = true,autoGen = false)
    public String dayMotionSummaryId;
    @Column(name = "userId")
    public int userId;      //用户ID
    @Column(name = "date")
    public String date;     //日期
    @Column(name = "weekday")
    public int weekday;     //星期几
    @Column(name = "weight")
    public float weight;    //体重
    @Column(name = "rest_hr")
    public int rest_hr;   //静息心率

    @Column(name = "stepcount")
    public int stepcount;     //实际运动步数
    @Column(name = "target_step")
    public int target_step;   //目标运动步数
    @Column(name = "step_details")
    public String step_details; //实际步数详情

    @Column(name = "length")
    public int length;        //实际运动公里数(米)
    @Column(name = "target_length")
    public int target_length; //目标运动公里数（米）
    @Column(name = "length_details")
    public String length_details; //实际公里数详情

    @Column(name = "effort_point")
    public int effort_point;  //实际运动点数
    @Column(name = "target_point")
    public int target_point;  //目标运动点数
    @Column(name = "point_details")
    public String point_details;//实际运动点数详情

    @Column(name = "exercised_minutes")
    public int exercised_minutes;     //实际运动分钟数
    @Column(name = "target_exercised_minutes")
    public int target_exercised_minutes;  //目标运动分钟数
    @Column(name = "exercised_minutes_details")
    public String exercised_minutes_details;    //实际运动点数详情

    @Column(name = "calorie")
    public int calorie;   //实际消耗卡路里数
    @Column(name = "target_calorie")
    public int target_calorie;    //目标消耗卡路里数
    @Column(name = "calorie_details")
    public String calorie_details;  //实际消耗卡路里数详情

    @Column(name = "week_exercised_days")
    public int week_exercised_days;   //实际运动天数
    @Column(name = "week_target_days")
    public int week_target_days;  //目标运动天数
    @Column(name = "week_days_details")
    public String week_days_details;//运动天数详情

    @Column(name = "sleepminutes")
    public int sleepminutes;      //睡眠分钟数
    @Column(name = "wakeuptimes")
    public int wakeuptimes;       //醒来次数
    @Column(name = "nremtimes")
    public int nremtimes;     //浅睡眠次数
    @Column(name = "target_sleep_minutes")
    public int target_sleep_minutes;    //目标睡眠时长

    public DayMotionSummaryDE() {
    }


    /**
     * 包含详情字段
     * @param dayMotionSummaryId
     * @param userId
     * @param date
     * @param weekday
     * @param weight
     * @param rest_hr
     * @param stepcount
     * @param target_step
     * @param step_details
     * @param length
     * @param target_length
     * @param length_details
     * @param effort_point
     * @param target_point
     * @param point_details
     * @param exercised_minutes
     * @param target_exercised_minutes
     * @param exercised_minutes_details
     * @param calorie
     * @param target_calorie
     * @param calorie_details
     * @param week_exercised_days
     * @param week_target_days
     * @param week_days_details
     * @param sleepminutes
     * @param wakeuptimes
     * @param nremtimes
     * @param target_sleep_minutes
     */
    public DayMotionSummaryDE(String dayMotionSummaryId ,int userId, String date, int weekday, float weight, int rest_hr, int stepcount, int target_step, String step_details, int length, int target_length, String length_details, int effort_point, int target_point, String point_details, int exercised_minutes, int target_exercised_minutes, String exercised_minutes_details, int calorie, int target_calorie, String calorie_details, int week_exercised_days, int week_target_days, String week_days_details, int sleepminutes, int wakeuptimes, int nremtimes, int target_sleep_minutes) {
        this.dayMotionSummaryId = dayMotionSummaryId;
        this.userId = userId;
        this.date = date;
        this.weekday = weekday;
        this.weight = weight;
        this.rest_hr = rest_hr;
        this.stepcount = stepcount;
        this.target_step = target_step;
        this.step_details = step_details;
        this.length = length;
        this.target_length = target_length;
        this.length_details = length_details;
        this.effort_point = effort_point;
        this.target_point = target_point;
        this.point_details = point_details;
        this.exercised_minutes = exercised_minutes;
        this.target_exercised_minutes = target_exercised_minutes;
        this.exercised_minutes_details = exercised_minutes_details;
        this.calorie = calorie;
        this.target_calorie = target_calorie;
        this.calorie_details = calorie_details;
        this.week_exercised_days = week_exercised_days;
        this.week_target_days = week_target_days;
        this.week_days_details = week_days_details;
        this.sleepminutes = sleepminutes;
        this.wakeuptimes = wakeuptimes;
        this.nremtimes = nremtimes;
        this.target_sleep_minutes = target_sleep_minutes;
    }
}
