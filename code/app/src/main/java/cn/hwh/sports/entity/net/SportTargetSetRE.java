package cn.hwh.sports.entity.net;

/**
 * Created by Administrator on 2016/11/22.
 */

public class SportTargetSetRE {


    /**
     * exercise_days_target : {"setuptime":"1970-01-01 08:00:00","exercise_days":0}
     */

    private ExerciseDaysTargetBean exercise_days_target;

    public ExerciseDaysTargetBean getExercise_days_target() {
        return exercise_days_target;
    }

    public void setExercise_days_target(ExerciseDaysTargetBean exercise_days_target) {
        this.exercise_days_target = exercise_days_target;
    }

    public static class ExerciseDaysTargetBean {
        /**
         * setuptime : 1970-01-01 08:00:00
         * exercise_days : 0
         */

        private String setuptime;
        private int exercise_days;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public int getExercise_days() {
            return exercise_days;
        }

        public void setExercise_days(int exercise_days) {
            this.exercise_days = exercise_days;
        }
    }
}
