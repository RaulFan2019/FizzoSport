package cn.hwh.sports.data.db;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import org.xutils.ex.DbException;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.db.SyncWatchDE;
import cn.hwh.sports.entity.db.UploadDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.db.WorkoutSyncDE;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class WorkoutDBData {

    private static final String TAG = "WorkoutDBData";

    /**
     * 获取未完成的运动记录
     *
     * @param userId 用户ID
     * @return
     */
    public static WorkoutDE getUnFinishWorkout(final int userId) {
        WorkoutDE workout = null;
        try {
            workout = LocalApplication.getInstance().getDb()
                    .selector(WorkoutDE.class).where("status", "=", "1")
                    .and("userId", "=", userId).findFirst();
            if (workout != null) {
                return workout;
            } else {
                return null;
            }
        } catch (DbException e) {
            return null;
        }
    }

    /**
     * 获取未完成的运动记录
     *
     * @param userId 用户ID
     * @return
     */
    public static WorkoutDE getUnFinishWorkout(final int userId, final int type) {
        WorkoutDE workout = null;
        try {
            workout = LocalApplication.getInstance().getDb()
                    .selector(WorkoutDE.class).where("status", "=", "1")
                    .and("userId", "=", userId).and("type", "=", type)
                    .findFirst();
            if (workout != null) {
                return workout;
            } else {
                return null;
            }
        } catch (DbException e) {
            return null;
        }
    }


    /**
     * 根据记录开始时间获取记录
     *
     * @param userId
     * @param workoutStartTime
     * @return
     */
    public static WorkoutDE getWorkoutByStartTime(final int userId, final String workoutStartTime) {
        WorkoutDE workout = null;
        try {
            workout = LocalApplication.getInstance().getDb()
                    .selector(WorkoutDE.class).where("startTime", "=", workoutStartTime)
                    .and("userId", "=", userId).findFirst();
            if (workout != null) {
                return workout;
            } else {
                return null;
            }
        } catch (DbException e) {
            return null;
        }
    }

    /**
     * 创建一个新的跑步历史
     */
    public static WorkoutDE createNewWorkout(final String workoutName, final int userId,
                                             final int ownerId, final int type,final int targetType,
                                             final int targetTime) {
        WorkoutDE workoutDE = getUnFinishWorkout(userId, type);
        if (workoutDE == null) {
            // create new workout
            String startTime = TimeU.nowTime();
            workoutDE = new WorkoutDE(System.currentTimeMillis(), workoutName, startTime,
                    0, 0, 0f, 0d, 0, 0, 0, 0, 0, 0, 999, userId, ownerId, 0, 0,
                    SportEnum.SportStatus.RUNNING, type, 0,targetType,targetTime,3);
            saveWorkout(workoutDE);
        }
        return workoutDE;
    }


    /**
     * 保存workout
     *
     * @param workoutDE
     */
    public static void saveWorkout(final WorkoutDE workoutDE) {
        try {
            LocalApplication.getInstance().getDb().save(workoutDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新workout
     *
     * @param workoutDE
     */
    public static void update(final WorkoutDE workoutDE) {
        try {
            LocalApplication.getInstance().getDb().update(workoutDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除记录
     *
     * @param workoutDE
     */
    public static void delete(final WorkoutDE workoutDE) {
        try {
            LocalApplication.getInstance().getDb().delete(workoutDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
