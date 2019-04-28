package cn.hwh.sports.data.db;

import org.xutils.ex.DbException;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.DayHealthDE;

/**
 * Created by Raul.Fan on 2017/4/19.
 */

public class DayHealthDBData {


    /**
     * 获取今天的健康状态
     *
     * @return
     */
    public static DayHealthDE getDayHealth(final String date, final int userId) {
        try {
            return LocalApplication.getInstance().getDb().selector(DayHealthDE.class)
                    .where("userId", "=", userId).and("date", "=", date).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 更新
     * @param dayHealthDE
     */
    public static void update(final DayHealthDE dayHealthDE){
        try {
            LocalApplication.getInstance().getDb().update(dayHealthDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存
     * @param dayHealthDE
     */
    public static void save(final DayHealthDE dayHealthDE){
        try {
            LocalApplication.getInstance().getDb().save(dayHealthDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
