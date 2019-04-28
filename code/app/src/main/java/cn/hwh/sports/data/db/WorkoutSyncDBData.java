package cn.hwh.sports.data.db;

import org.xutils.ex.DbException;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.WorkoutSyncDE;

/**
 * Created by Raul.Fan on 2016/11/29.
 */

public class WorkoutSyncDBData {


    /**
     * 保存
     *
     * @param workoutSyncDE
     */
    public static void save(final WorkoutSyncDE workoutSyncDE) {
        try {
            LocalApplication.getInstance().getDb().save(workoutSyncDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取记录在服务器的ID
     * @param startTime
     * @param userId
     * @return
     */
    public static long getWorkoutServiceId(final String startTime, final int userId) {
        try {
            WorkoutSyncDE workoutSyncDE = LocalApplication.getInstance().getDb().selector(WorkoutSyncDE.class)
                    .where("workoutStartTime","=",startTime).and("ownerId","=",userId).findFirst();
            if (workoutSyncDE != null){
                return workoutSyncDE.serviceId;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
