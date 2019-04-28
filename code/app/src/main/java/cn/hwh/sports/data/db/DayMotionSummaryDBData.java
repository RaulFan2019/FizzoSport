package cn.hwh.sports.data.db;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.DayMotionSummaryDE;

/**
 * Created by Administrator on 2016/11/24.
 */

public class DayMotionSummaryDBData {

    /**
     * 通过UserId获取所有该用户的主页数据
     *
     * @param userId
     * @return
     */
    public static List<DayMotionSummaryDE> getDayMotionSummary(final int userId){
        try {
            return LocalApplication.getInstance().getDb().selector(DayMotionSummaryDE.class).where("userid","=",userId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveOrUpdate(final List<DayMotionSummaryDE> entityList){
        try {
            LocalApplication.getInstance().getDb().saveOrUpdate(entityList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取时间段内的数据
     *
     * @param userId
     * @param startTime 包含该日期
     * @param endTime  不包含该日期
     * @return
     */
    public static List<DayMotionSummaryDE> getDayMotionSummary(final  int userId,final String startTime ,final String endTime,boolean orderBy){
        WhereBuilder whereBuilder = WhereBuilder.b();
        whereBuilder.and("date","<=",startTime).and("date",">",endTime).and("userid","=",userId);
        try {
            return  LocalApplication.getInstance().getDb().selector(DayMotionSummaryDE.class).where(whereBuilder).orderBy("date",orderBy).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }

    }
}
