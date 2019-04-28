package cn.hwh.sports.data.db;

import com.alibaba.fastjson.JSON;

import org.xutils.ex.DbException;

import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.WorkoutCalendarDE;
import cn.hwh.sports.entity.net.GetCalendarRE;

/**
 * Created by Raul.Fan on 2017/5/12.
 */

public class WorkoutCalendarDBData {



    /**
     * 根据日期获取日历信息
     *
     * @param userId
     * @return
     */
    public static List<WorkoutCalendarDE> getWorkoutCalendarList(final int userId) {
        try {
            return LocalApplication.getInstance().getDb().selector(WorkoutCalendarDE.class)
                    .where("userId", "=", userId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据日期获取日历信息
     *
     * @param userId
     * @param startDay
     * @param endDay
     * @return
     */
    public static List<WorkoutCalendarDE> getWorkoutCalendarList(final int userId,
                                                                 final String startDay, String endDay) {
        try {
            return LocalApplication.getInstance().getDb().selector(WorkoutCalendarDE.class)
                    .where("userId", "=", userId).and("day", ">=", startDay).and("day", "<=", endDay).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 保存或更新信息
     */
    public static void saveOrUpdate(final int userId ,GetCalendarRE.CalendarEntity entity){
        try {
            WorkoutCalendarDE de = LocalApplication.getInstance().getDb().selector(WorkoutCalendarDE.class)
                    .where("userId","=",userId).and("day","=",entity.date_short).findFirst();
            if (de == null){
                de = new WorkoutCalendarDE(userId,entity.date_short, JSON.toJSONString(entity));
                LocalApplication.getInstance().getDb().save(de);
            }else {
                de.data = JSON.toJSONString(entity);
                LocalApplication.getInstance().getDb().update(de);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回某一天的信息
     * @param userId
     * @param day
     * @return
     */
    public static WorkoutCalendarDE getWorkoutCalendarData(final int userId,final String day){
        try {
            return LocalApplication.getInstance().getDb().selector(WorkoutCalendarDE.class)
                    .where("userId","=",userId).and("day","=",day).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
