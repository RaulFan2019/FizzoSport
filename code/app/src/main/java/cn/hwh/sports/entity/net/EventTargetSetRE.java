package cn.hwh.sports.entity.net;

/**
 * Created by Administrator on 2016/11/22.
 */

public class EventTargetSetRE {

    /**
     * stepcount_target : {"setuptime":"2016-11-20 16:55:56","stepcount":10000}
     * length_target : {"setuptime":"2016-11-20 16:55:56","length":5000}
     * calorie_target : {"setuptime":"2016-11-20 16:55:56","calorie":2500}
     * effort_point_target : {"setuptime":"2016-11-20 16:55:56","effort_point":90}
     * exercise_minutes : {"setuptime":"2016-11-20 16:55:56","exercise_minutes":70}
     */

    private StepcountTargetBean stepcount_target;
    private LengthTargetBean length_target;
    private CalorieTargetBean calorie_target;
    private EffortPointTargetBean effort_point_target;
    private ExerciseMinutesBean exercise_minutes_target;

    public StepcountTargetBean getStepcount_target() {
        return stepcount_target;
    }

    public void setStepcount_target(StepcountTargetBean stepcount_target) {
        this.stepcount_target = stepcount_target;
    }

    public LengthTargetBean getLength_target() {
        return length_target;
    }

    public void setLength_target(LengthTargetBean length_target) {
        this.length_target = length_target;
    }

    public CalorieTargetBean getCalorie_target() {
        return calorie_target;
    }

    public void setCalorie_target(CalorieTargetBean calorie_target) {
        this.calorie_target = calorie_target;
    }

    public EffortPointTargetBean getEffort_point_target() {
        return effort_point_target;
    }

    public void setEffort_point_target(EffortPointTargetBean effort_point_target) {
        this.effort_point_target = effort_point_target;
    }

    public ExerciseMinutesBean getExercise_minutes_target() {
        return exercise_minutes_target;
    }

    public void setExercise_minutes_target(ExerciseMinutesBean exercise_minutes_target) {
        this.exercise_minutes_target = exercise_minutes_target;
    }

    public static class StepcountTargetBean {
        /**
         * setuptime : 2016-11-20 16:55:56
         * stepcount : 10000
         */

        private String setuptime;
        private int stepcount;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public int getStepcount() {
            return stepcount;
        }

        public void setStepcount(int stepcount) {
            this.stepcount = stepcount;
        }
    }

    public static class LengthTargetBean {
        /**
         * setuptime : 2016-11-20 16:55:56
         * length : 5000
         */

        private String setuptime;
        private int length;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }

    public static class CalorieTargetBean {
        /**
         * setuptime : 2016-11-20 16:55:56
         * calorie : 2500
         */

        private String setuptime;
        private int calorie;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }
    }

    public static class EffortPointTargetBean {
        /**
         * setuptime : 2016-11-20 16:55:56
         * effort_point : 90
         */

        private String setuptime;
        private int effort_point;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }
    }

    public static class ExerciseMinutesBean {
        /**
         * setuptime : 2016-11-20 16:55:56
         * exercise_minutes : 70
         */

        private String setuptime;
        private int exercise_minutes;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public int getExercise_minutes() {
            return exercise_minutes;
        }

        public void setExercise_minutes(int exercise_minutes) {
            this.exercise_minutes = exercise_minutes;
        }
    }
}
