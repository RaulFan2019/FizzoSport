package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Administrator on 2016/11/25.
 */

public class StepDetailListRE {


    private List<DaysBean> days;

    public List<DaysBean> getDays() {
        return days;
    }

    public void setDays(List<DaysBean> days) {
        this.days = days;
    }

    public static class DaysBean {
        /**
         * date : 2016-11-14
         * weekday : 1
         * stepcount : 0
         * target_step : 10000
         */

        private String date;
        private int weekday;
        private int stepcount;
        private int target_step;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getWeekday() {
            return weekday;
        }

        public void setWeekday(int weekday) {
            this.weekday = weekday;
        }

        public int getStepcount() {
            return stepcount;
        }

        public void setStepcount(int stepcount) {
            this.stepcount = stepcount;
        }

        public int getTarget_step() {
            return target_step;
        }

        public void setTarget_step(int target_step) {
            this.target_step = target_step;
        }
    }
}
