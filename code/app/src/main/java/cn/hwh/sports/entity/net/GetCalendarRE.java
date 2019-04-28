package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2017/5/12.
 */

public class GetCalendarRE {


    /**
     * calendar : [{"date":"2017-04-18","weekday":2,"monthday":18,"month":4,"duration":86400,"stepcount":0,"minutes":7,"calorie":58,"workoutcount":1,"workouts":[{"id":4177255,"type":3,"name":"日常锻炼","starttime":"2017-04-18 14:14:45","finishtime":"2017-04-18 14:21:45","duration":420}]},{"date":"2017-04-19","weekday":3,"monthday":19,"month":4,"duration":86400,"stepcount":0,"minutes":7,"calorie":75,"workoutcount":1,"workouts":[{"id":4177239,"type":3,"name":"无器械","starttime":"2017-04-19 10:11:31","finishtime":"2017-04-19 10:18:32","duration":421}]}]
     * from_day : 2017-04-01
     * to_day : 2017-04-30
     */

    public String from_day;
    public String to_day;
    /**
     * date : 2017-04-18
     * weekday : 2
     * monthday : 18
     * month : 4
     * duration : 86400
     * stepcount : 0
     * minutes : 7
     * calorie : 58
     * workoutcount : 1
     * workouts : [{"id":4177255,"type":3,"name":"日常锻炼","starttime":"2017-04-18 14:14:45","finishtime":"2017-04-18 14:21:45","duration":420}]
     */

    public List<CalendarEntity> calendar;

    public String getFrom_day() {
        return from_day;
    }

    public void setFrom_day(String from_day) {
        this.from_day = from_day;
    }

    public String getTo_day() {
        return to_day;
    }

    public void setTo_day(String to_day) {
        this.to_day = to_day;
    }

    public List<CalendarEntity> getCalendar() {
        return calendar;
    }

    public void setCalendar(List<CalendarEntity> calendar) {
        this.calendar = calendar;
    }

    public static class CalendarEntity {
        public String date_short;
        public int duration;
        public int stepcount;
        public int minutes;
        public int calorie;
        public int workoutcount;
        /**
         * id : 4177255
         * type : 3
         * name : 日常锻炼
         * starttime : 2017-04-18 14:14:45
         * finishtime : 2017-04-18 14:21:45
         * duration : 420
         */

        public List<WorkoutsEntity> workouts;

        public String getDate() {
            return date_short;
        }

        public void setDate(String date_short) {
            this.date_short = date_short;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getStepcount() {
            return stepcount;
        }

        public void setStepcount(int stepcount) {
            this.stepcount = stepcount;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public int getWorkoutcount() {
            return workoutcount;
        }

        public void setWorkoutcount(int workoutcount) {
            this.workoutcount = workoutcount;
        }

        public List<WorkoutsEntity> getWorkouts() {
            return workouts;
        }

        public void setWorkouts(List<WorkoutsEntity> workouts) {
            this.workouts = workouts;
        }

        public static class WorkoutsEntity {
            public int id;
            public int type;
            public String name;
            public String starttime;
            public String finishtime;
            public int duration;
            public int calorie;
            public int resource;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getStarttime() {
                return starttime;
            }

            public void setStarttime(String starttime) {
                this.starttime = starttime;
            }

            public String getFinishtime() {
                return finishtime;
            }

            public void setFinishtime(String finishtime) {
                this.finishtime = finishtime;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getCalorie() {
                return calorie;
            }

            public void setCalorie(int calorie) {
                this.calorie = calorie;
            }

            public int getResource() {
                return resource;
            }

            public void setResource(int resource) {
                this.resource = resource;
            }
        }
    }
}
