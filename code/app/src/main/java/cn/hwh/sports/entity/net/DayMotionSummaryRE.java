package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */

public class DayMotionSummaryRE {


    public List<DaysBean> days;

    public List<DaysBean> getDays() {
        return days;
    }

    public void setDays(List<DaysBean> days) {
        this.days = days;
    }

    public static class DaysBean {
        /**
         * calorie : 0
         * date : 2016-10-19
         * effort_point : 0
         * exercised_minutes : 0
         * length : 0
         * nremtimes : 5
         * rest_hr : 0
         * sleepminutes : 320
         * stepcount : 0
         * target_calorie : 3000
         * target_exercised_minutes : 30
         * target_length : 3000
         * target_point : 90
         * target_step : 10000
         * wakeuptimes : 5
         * week_exercised_days : 0
         * week_target_days : 5
         * weekday : 3
         * weight : 0
         */

        public  int calorie;
        public String date;
        public int effort_point;
        public int exercised_minutes;
        public int length;
        public int nremtimes;
        public int rest_hr;
        public int sleepminutes;
        public int stepcount;
        public int target_calorie;
        public int target_exercised_minutes;
        public int target_length;
        public int target_point;
        public int target_step;
        public int wakeuptimes;
        public int week_exercised_days;
        public int week_target_days;
        public int weekday;
        public float weight;
        public int target_sleep_minutes;

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }

        public int getExercised_minutes() {
            return exercised_minutes;
        }

        public void setExercised_minutes(int exercised_minutes) {
            this.exercised_minutes = exercised_minutes;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getNremtimes() {
            return nremtimes;
        }

        public void setNremtimes(int nremtimes) {
            this.nremtimes = nremtimes;
        }

        public int getRest_hr() {
            return rest_hr;
        }

        public void setRest_hr(int rest_hr) {
            this.rest_hr = rest_hr;
        }

        public int getSleepminutes() {
            return sleepminutes;
        }

        public void setSleepminutes(int sleepminutes) {
            this.sleepminutes = sleepminutes;
        }

        public int getStepcount() {
            return stepcount;
        }

        public void setStepcount(int stepcount) {
            this.stepcount = stepcount;
        }

        public int getTarget_calorie() {
            return target_calorie;
        }

        public void setTarget_calorie(int target_calorie) {
            this.target_calorie = target_calorie;
        }

        public int getTarget_exercised_minutes() {
            return target_exercised_minutes;
        }

        public void setTarget_exercised_minutes(int target_exercised_minutes) {
            this.target_exercised_minutes = target_exercised_minutes;
        }

        public int getTarget_length() {
            return target_length;
        }

        public void setTarget_length(int target_length) {
            this.target_length = target_length;
        }

        public int getTarget_point() {
            return target_point;
        }

        public void setTarget_point(int target_point) {
            this.target_point = target_point;
        }

        public int getTarget_step() {
            return target_step;
        }

        public void setTarget_step(int target_step) {
            this.target_step = target_step;
        }

        public int getWakeuptimes() {
            return wakeuptimes;
        }

        public void setWakeuptimes(int wakeuptimes) {
            this.wakeuptimes = wakeuptimes;
        }

        public int getWeek_exercised_days() {
            return week_exercised_days;
        }

        public void setWeek_exercised_days(int week_exercised_days) {
            this.week_exercised_days = week_exercised_days;
        }

        public int getWeek_target_days() {
            return week_target_days;
        }

        public void setWeek_target_days(int week_target_days) {
            this.week_target_days = week_target_days;
        }

        public int getWeekday() {
            return weekday;
        }

        public void setWeekday(int weekday) {
            this.weekday = weekday;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public int getTarget_sleep_minutes() {
            return target_sleep_minutes;
        }

        public void setTarget_sleep_minutes(int target_sleep_minutes) {
            this.target_sleep_minutes = target_sleep_minutes;
        }
    }
}
