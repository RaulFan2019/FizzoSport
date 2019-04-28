package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2017/4/19.
 */

public class GetHealthSummaryRE {


    /**
     * date : 2017-04-19
     * weekday : 3
     * stepcount : 527
     * exercised_calorie : 75
     * exercised_minutes : 7
     * effective_minutes : 7
     * updatetime : 2017-04-19 10:18:31
     * last_exercise : {"id":4177276,"type":3,"name":"室内健身","starttime":"2017-04-19 10:24:38","duration":964}
     */

    public String date;
    public int weekday;
    public int stepcount;
    public int exercised_calorie;
    public int exercised_minutes;
    public int effective_minutes;
    public String updatetime;
    /**
     * id : 4177276
     * type : 3
     * name : 室内健身
     * starttime : 2017-04-19 10:24:38
     * duration : 964
     */

    public LastExerciseEntity last_exercise;

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

    public int getExercised_calorie() {
        return exercised_calorie;
    }

    public void setExercised_calorie(int exercised_calorie) {
        this.exercised_calorie = exercised_calorie;
    }

    public int getExercised_minutes() {
        return exercised_minutes;
    }

    public void setExercised_minutes(int exercised_minutes) {
        this.exercised_minutes = exercised_minutes;
    }

    public int getEffective_minutes() {
        return effective_minutes;
    }

    public void setEffective_minutes(int effective_minutes) {
        this.effective_minutes = effective_minutes;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public LastExerciseEntity getLast_exercise() {
        return last_exercise;
    }

    public void setLast_exercise(LastExerciseEntity last_exercise) {
        this.last_exercise = last_exercise;
    }

    public static class LastExerciseEntity {
        public int id;
        public int type;
        public String name;
        public String starttime;
        public int duration;

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

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
