package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/23.
 */

public class GetMoverDayCountRE {

    /**
     * date : 2016-11-24
     * weekday : 4
     * movercount : 1
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
        public int movercount;

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

        public int getMovercount() {
            return movercount;
        }

        public void setMovercount(int movercount) {
            this.movercount = movercount;
        }
    }
}
