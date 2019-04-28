package cn.hwh.sports.data.db;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.WorkoutDE;

/**
 * Created by Raul.Fan on 2016/11/27.
 */

public class TimeSplitDBData {

    /**
     * 获取未完成的时间split
     *
     * @param workoutStartTime
     * @param userId
     * @return
     */
    public static TimeSplitDE getUnFinishTimeSplit(final String workoutStartTime, final int userId) {
        try {
            return LocalApplication.getInstance().getDb().selector(TimeSplitDE.class)
                    .where("workoutStartTime", "=", workoutStartTime)
                    .and("userId", "=", userId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个新的时间split
     *
     * @param workoutStartTime
     * @param index
     * @param userId
     * @param ownerId
     * @return
     */
    public static TimeSplitDE createNewTimeSplit(final String workoutStartTime, final int index,
                                                 final int userId, final int ownerId) {
        TimeSplitDE timeSplitDE = new TimeSplitDE(System.currentTimeMillis(), workoutStartTime, index,
                0, SportEnum.SportStatus.RUNNING, 0, userId, ownerId);
        try {
            LocalApplication.getInstance().getDb().save(timeSplitDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return timeSplitDE;
    }

    /**
     * 创造一个手表中的split
     *
     * @param workoutStartTime
     * @param index
     * @param userId
     * @param avgHr
     * @return
     */
    public static TimeSplitDE createWatchSplit(final String workoutStartTime, final int index
            , final int userId, final int avgHr, final List<HrDE> hrList, final LapDE session) {
        TimeSplitDE split = new TimeSplitDE(System.currentTimeMillis(), workoutStartTime, index,
                avgHr, SportEnum.SportStatus.FINISH, 60, userId,userId);
//        DBDataHeartbeat.save(hrList);
        UploadDBData.saveHrInfoWithTimeSplitFromWatch(session, split, hrList);
        return split;
    }

    /**
     * 保存
     * @param splitDEs
     */
    public static void save(final List<TimeSplitDE> splitDEs){
        try {
            LocalApplication.getInstance().getDb().save(splitDEs);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新split
     *
     * @param timeSplitDE
     */
    public static void update(final TimeSplitDE timeSplitDE) {
        try {
            LocalApplication.getInstance().getDb().update(timeSplitDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除
     * @param timeSplitDE
     */
    public static void delete(final TimeSplitDE timeSplitDE){
        try {
            LocalApplication.getInstance().getDb().delete(timeSplitDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取这个workout下所有split
     * @param workoutDE
     * @return
     */
    public static List<TimeSplitDE> getSplitsByWorkout(WorkoutDE workoutDE) {
        List<TimeSplitDE> result = new ArrayList<>();

        try {
            List<TimeSplitDE> tmp = LocalApplication.getInstance().getDb().selector(TimeSplitDE.class)
                    .where("workoutStartTime", "=", workoutDE.startTime)
                    .and("userId", "=", workoutDE.userId).findAll();
            if (tmp != null && tmp.size() > 0) {
                result.addAll(tmp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }
}
