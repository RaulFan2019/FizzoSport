package cn.hwh.sports.entity.adapter;

import cn.hwh.sports.entity.net.GetUserWorkoutListRE;

/**
 * Created by Raul.Fan on 2016/12/19.
 */

public class WorkoutListAE {

    public static final int ITEM_RUN = 0;
    public static final int ITEM_EFFORT = 1;
    public static final int ITEM_RUNNING_INDOOR = 2;
    public static final int SECTION = 3;

    public int type;//类型

    public int sectionPosition;//section位置
    public int listPosition;//list 位置

    public String weekStartTime;
    public int weekDuration;

    public GetUserWorkoutListRE.WorkoutEntity workoutEntity;

    public WorkoutListAE(int type, int sectionPosition, int listPosition,
                         String weekStartTime, int weekDuration,
                         GetUserWorkoutListRE.WorkoutEntity workoutEntity) {
        this.type = type;
        this.sectionPosition = sectionPosition;
        this.listPosition = listPosition;
        this.workoutEntity = workoutEntity;
        this.weekStartTime = weekStartTime;
        this.weekDuration = weekDuration;
    }
}
