package cn.hwh.sports.data.db;

import android.content.Context;

import org.xutils.ex.DbException;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/11/10.
 */

public class UserDBData {

    private static final String TAG = "UserDBData";

    /**
     * 通过用户ID ，获取用户信息
     *
     * @param userId
     * @return
     */
    public static UserDE getUserById(final int userId) {
        try {
            return LocalApplication.getInstance().getDb()
                    .selector(UserDE.class).where("userId", "=", userId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 插入用户数据
     *
     * @param user
     */
    public static void save(final UserDE user) {
        try {
            LocalApplication.getInstance().getDb().save(user);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新用户本地数据信息
     *
     * @param user
     */
    public static void update(final UserDE user) {
        try {
            LocalApplication.getInstance().getDb().update(user);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存或更新用户
     *
     * @param user
     */
    public static void saveOrUpdate(final UserDE user) {
        try {
            if (getUserById(user.userId) == null) {
                LocalApplication.getInstance().getDb().save(user);
            } else {
                LocalApplication.getInstance().getDb().update(user);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param context
     * @param entity
     */
    public static void CommonLogin(final Context context, final UserInfoRE entity) {
        UserDE user = new UserDE(entity.id, entity.name, entity.sessionid, entity.weight,
                entity.height, entity.nickname, entity.gender, entity.birthdate, entity.avatar,
                entity.locationprovince, entity.locationcity, entity.maxhr, entity.resthr, entity.hrdevice.macaddr,
                entity.hrdevice.name, entity.vo2max, entity.registerdate, entity.roles.get(entity.roles.size() - 1).role
                , entity.daily_target.stepcount, entity.daily_target.length, entity.daily_target.effort_point,
                entity.daily_target.calorie, entity.daily_target.exercise_minutes, entity.daily_target.sleep_minutes
                , entity.targethrlow, entity.targethrhigh, entity.alerthr, entity.characteristic_target.weight,
                entity.characteristic_target.fatrate,entity.finishedworkout,entity.monthexerciseddays);
        UserSPData.setUserId(context, entity.id);
        UserSPData.setUserName(context, entity.name);
        UserSPData.setHasLogin(context, true);
        UserSPData.setUserAvatar(context, entity.avatar);
        UserSPData.setUserNickName(context, entity.nickname);
        UserSPData.setUserRole(context, user.role);

        try {
            if (getUserById(user.userId) == null) {
                LocalApplication.getInstance().getDb().save(user);
            } else {
                LocalApplication.getInstance().getDb().update(user);
            }
            LocalApplication.getInstance().mLoginUser = user;
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 微信登录
     *
     * @param context
     * @param entity
     */
    public static UserDE WxLogin(final Context context, final UserInfoRE entity) {
        UserDE user = new UserDE(entity.id, entity.name, entity.sessionid, entity.weight,
                entity.height, entity.nickname, entity.gender, entity.birthdate, entity.avatar,
                entity.locationprovince, entity.locationcity, entity.maxhr, entity.resthr, entity.hrdevice.macaddr,
                entity.hrdevice.name, entity.vo2max, entity.registerdate, entity.roles.get(entity.roles.size() - 1).role
                , entity.daily_target.stepcount, entity.daily_target.length, entity.daily_target.effort_point,
                entity.daily_target.calorie, entity.daily_target.exercise_minutes, entity.daily_target.sleep_minutes
                , entity.targethrlow, entity.targethrhigh, entity.alerthr, entity.characteristic_target.weight,
                entity.characteristic_target.fatrate,entity.finishedworkout,entity.monthexerciseddays);
        try {
            if (getUserById(user.userId) == null) {
                LocalApplication.getInstance().getDb().save(user);
            } else {
                LocalApplication.getInstance().getDb().update(user);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return user;
    }
}
