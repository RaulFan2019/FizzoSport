package cn.hwh.sports.data.db;

import com.nostra13.universalimageloader.utils.L;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.WorkoutDE;

/**
 * Created by Raul.Fan on 2016/11/27.
 */

public class HrDBData {


    /**
     * 保存
     *
     * @param hrDE
     */
    public static void save(final HrDE hrDE) {
        try {
            LocalApplication.getInstance().getDb().save(hrDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存
     *
     * @param hrDEList
     */
    public static void save(final List<HrDE> hrDEList) {
        try {
            LocalApplication.getInstance().getDb().save(hrDEList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取一个时间split的平均心率
     *
     * @param timeSplitDE
     * @return
     */
    public static int getAvgHrInSplit(final TimeSplitDE timeSplitDE) {
        try {
            List<HrDE> hrList = LocalApplication.getInstance().getDb()
                    .selector(HrDE.class)
                    .where("workoutStartTime", "=", timeSplitDE.workoutStartTime)
                    .and("timeSplitId", "=", timeSplitDE.timeSplitId).findAll();
            if (hrList != null && hrList.size() > 0) {
                int total = 0;
                for (HrDE hrDE : hrList) {
                    total += hrDE.heartbeat;
                }
                return total / hrList.size();
            } else {
                return 0;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除这一分钟的心率
     * @return
     */
    public static void deleteHrInSplit(final TimeSplitDE timeSplitDE){
        try {
            List<HrDE> hrList = LocalApplication.getInstance().getDb().selector(HrDE.class).
                    where("workoutStartTime", "=", timeSplitDE.workoutStartTime)
                    .and("timeSplitId", "=", timeSplitDE.timeSplitId).findAll();
            if (hrList != null){
                LocalApplication.getInstance().getDb().delete(hrList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个距离split的平均心率
     *
     * @param lengthSplitDE
     * @return
     */
    public static int getAvgHrInLengthSplit(final LengthSplitDE lengthSplitDE) {
        try {
            List<HrDE> hrList = LocalApplication.getInstance().getDb()
                    .selector(HrDE.class)
                    .where("workoutStartTime", "=", lengthSplitDE.workoutStartTime)
                    .and("lengthSplitId", "=", lengthSplitDE.lengthSplitId).findAll();
            if (hrList != null && hrList.size() > 0) {
                int total = 0;
                for (HrDE hrDE : hrList) {
                    total += hrDE.heartbeat;
                }
                return total / hrList.size();
            } else {
                return 0;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取workout的平均心率
     *
     * @param workoutDE
     * @return
     */
    public static int getAvgHrInWorkout(final WorkoutDE workoutDE) {
        try {
            List<HrDE> hrList = LocalApplication.getInstance().getDb()
                    .selector(HrDE.class)
                    .where("workoutStartTime", "=", workoutDE.startTime).findAll();
            if (hrList != null && hrList.size() > 0) {
                int total = 0;
                for (HrDE hrDE : hrList) {
                    total += hrDE.heartbeat;
                }
                return total / hrList.size();
            } else {
                return 0;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取一个time split中的心率列表
     * @param timeSplitDE
     * @return
     */
    public static List<HrDE> getHrListInTimeSplit(final TimeSplitDE timeSplitDE) {
        List<HrDE> result = new ArrayList<>();
        try {
            List<HrDE> tmp = LocalApplication.getInstance().getDb().selector(HrDE.class)
                    .where("workoutStartTime", "=", timeSplitDE.workoutStartTime)
                    .and("timeSplitId", "=", timeSplitDE.timeSplitId).findAll();
            if (tmp != null) {
                result.addAll(tmp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取锻炼历史中所有心率数据
     * @param workoutStartTime
     * @return
     */
    public static List<HrDE> getHrListInWorkout(final String workoutStartTime){
        List<HrDE> result = new ArrayList<>();
        try {
            List<HrDE> tmp = LocalApplication.getInstance().getDb().selector(HrDE.class)
                    .where("workoutStartTime", "=", workoutStartTime).findAll();
            if (tmp != null) {
                result.addAll(tmp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 找指定timeOffSet最接近的心率
     * @param workoutStartTime
     * @param timeOffSet
     * @return
     */
    public static HrDE getHrInfoAlainTimeOffSet(final String workoutStartTime,final int timeOffSet){
        try {
            return LocalApplication.getInstance().getDb().selector(HrDE.class).
                    where("workoutStartTime", "=", workoutStartTime).and("timeOffSet","<=",timeOffSet)
                    .orderBy("timeOffSet",true).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }
}
