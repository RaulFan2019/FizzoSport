package cn.hwh.sports.data.db;

import android.content.Context;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.TimeSplitDE;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class LocationDBData {


    /**
     * 获取最后一个或第一个
     *
     * @param lap
     * @param first
     * @return
     */
    public static LocationDE getLocationByLap(LapDE lap, boolean first) {
        LocationDE location = null;
        try {
            location = LocalApplication.getInstance().getDb().selector(LocationDE.class)
                            .where("workoutStartTime", "=", lap.workoutStartTime)
                            .and("lapStartTime", "=", lap.startTime)
                            .orderBy("locationId", !first).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return location;
    }

    /**
     * 获取从指定位置开始的所有位置信息
     * @param workoutStartTime
     * @param lastId
     * @return
     */
    public static List<LocationDE> getAfterLocFromWorkout(final String workoutStartTime, final long lastId) {
        List<LocationDE> result = new ArrayList<>();
        try {
            result = LocalApplication.getInstance().getDb().selector(LocationDE.class)
                    .where("workoutStartTime","=",workoutStartTime)
                    .and("locationId",">=",lastId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取一个time split中的位置列表
     * @param timeSplitDE
     * @return
     */
    public static List<LocationDE> getLocListInTimeSplit(final TimeSplitDE timeSplitDE) {
        List<LocationDE> result = new ArrayList<>();
        try {
            List<LocationDE> tmp = LocalApplication.getInstance().getDb().selector(LocationDE.class)
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
     * 保存地理位置
     * @param locationDE
     */
    public static void save(LocationDE locationDE){
        try {
            LocalApplication.getInstance().getDb().save(locationDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存地理位置
     * @param locations
     */
    public static void save(final List<LocationDE> locations){
        try {
            LocalApplication.getInstance().getDb().save(locations);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
