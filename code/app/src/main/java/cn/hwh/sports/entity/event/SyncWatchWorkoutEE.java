package cn.hwh.sports.entity.event;

import cn.hwh.sports.entity.db.WorkoutDE;

/**
 * Created by Raul.Fan on 2016/10/18.
 */
public class SyncWatchWorkoutEE {

    public static final int MSG_COUNT = 0x01;
    public static final int MSG_PERCENT = 0x02;
    public static final int MSG_FINISH = 0x03;
    public static final int MSG_SYNC_SUCCESS = 0x04;
    public static final int MSG_SYNC_ERROR = 0x05;
    public static final int MSG_FINISH_WITHOUT_TOAST = 0x06;

    public int msg;
    public int workoutCount;
    public String workoutInfo;

    public WorkoutDE workoutDE;


    public SyncWatchWorkoutEE(int msg){
        this.msg = msg;
    }

    public SyncWatchWorkoutEE(int msg, int workoutCount) {
        this.msg = msg;
        this.workoutCount = workoutCount;
    }

    public SyncWatchWorkoutEE(int msg, String workoutInfo) {
        this.msg = msg;
        this.workoutInfo = workoutInfo;
    }

    public SyncWatchWorkoutEE(int msg, WorkoutDE workoutDE) {
        this.msg = msg;
        this.workoutDE = workoutDE;
    }
}
