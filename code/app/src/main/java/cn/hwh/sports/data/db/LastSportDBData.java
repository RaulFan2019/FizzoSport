package cn.hwh.sports.data.db;

import org.xutils.ex.DbException;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.entity.db.LastSportDE;

/**
 * Created by Raul.Fan on 2017/4/19.
 */

public class LastSportDBData {


    /**
     * 获取上次运动信息
     * @param userId
     * @return
     */
    public static LastSportDE getLastSportInfo(final int userId) {
        try {
            return LocalApplication.getInstance().getDb().selector(LastSportDE.class)
                    .where("userId", "=", userId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 保存
     * @param lastSportDE
     */
    public static void save(LastSportDE lastSportDE){
        try {
            LocalApplication.getInstance().getDb().save(lastSportDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新
     * @param lastSportDE
     */
    public static void update(LastSportDE lastSportDE){
        try {
            LocalApplication.getInstance().getDb().update(lastSportDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
