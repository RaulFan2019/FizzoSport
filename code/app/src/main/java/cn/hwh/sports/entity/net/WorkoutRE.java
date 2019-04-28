package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2016/11/29.
 */

public class WorkoutRE {


    /**
     * id : 3884689
     * newWorkout : 1
     * needNotifyBeginRunning : 0
     * isworkoutend : 0
     */

    public int id;
    public int newWorkout;
    public int needNotifyBeginRunning;
    public int isworkoutend;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNewWorkout() {
        return newWorkout;
    }

    public void setNewWorkout(int newWorkout) {
        this.newWorkout = newWorkout;
    }

    public int getNeedNotifyBeginRunning() {
        return needNotifyBeginRunning;
    }

    public void setNeedNotifyBeginRunning(int needNotifyBeginRunning) {
        this.needNotifyBeginRunning = needNotifyBeginRunning;
    }

    public int getIsworkoutend() {
        return isworkoutend;
    }

    public void setIsworkoutend(int isworkoutend) {
        this.isworkoutend = isworkoutend;
    }
}
