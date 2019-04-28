package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/23.
 */

public class GetMoverDayEffortRE {

    /**
     * date : 2016-11-24
     * weekday : 4
     * effort_point : 0
     */

    public List<DaysEntity> days;

    public List<DaysEntity> getDays() {
        return days;
    }

    public void setDays(List<DaysEntity> days) {
        this.days = days;
    }

    public static class DaysEntity {
        public String date;
        public int weekday;
        public int effort_point;

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

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }
    }
}
