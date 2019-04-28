package cn.hwh.sports.data.db;

import android.content.Context;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.db.LengthSplitDE;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class LengthSplitDBData {

    /**
     * 获取未完成的Split
     *
     * @return
     */
    public static LengthSplitDE getUnFinishSplit(final String workoutName) {
        LengthSplitDE split;
        try {
            split = LocalApplication.getInstance().getDb().selector(LengthSplitDE.class)
                    .where("status", "=", SportEnum.SportStatus.RUNNING)
                    .and("workoutName", "=", workoutName).findFirst();
            if (split != null) {
                return split;
            } else {
                return null;
            }
        } catch (DbException e) {
            return null;
        }
    }

    /**
     * 获取记录中的距离分段
     * @param workoutName
     * @return
     */
    public static List<LengthSplitDE> getAllSplitInWorkout(final String workoutName){
        List<LengthSplitDE> result = new ArrayList<>();
        try {
            List<LengthSplitDE> tmp = LocalApplication.getInstance().getDb().selector(LengthSplitDE.class)
                    .where("workoutStartTime","=",workoutName).findAll();
            if (tmp != null){
                result.addAll(tmp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建新的split信息
     */
    public static LengthSplitDE createNewSplit(final float mRunningLength, final String workoutStartTime
            , final int userId, final int ownerId) {
        LengthSplitDE mDBSplit = new LengthSplitDE(System.currentTimeMillis(), (int) (mRunningLength / 1000),
                workoutStartTime, 0, 0, 0, 0, 0, 0, 0, SportEnum.SportStatus.RUNNING, userId, ownerId);
        save(mDBSplit);
        return mDBSplit;
    }

    /**
     * 更新
     * @param splitDE
     */
    public static void update(LengthSplitDE splitDE){
        try {
            LocalApplication.getInstance().getDb().update(splitDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存split
     *
     * @param splitDE
     */
    public static void save(LengthSplitDE splitDE) {
        try {
            LocalApplication.getInstance().getDb().save(splitDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存split
     *
     * @param splitDEs
     */
    public static void save(List<LengthSplitDE> splitDEs) {
        try {
            LocalApplication.getInstance().getDb().save(splitDEs);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
