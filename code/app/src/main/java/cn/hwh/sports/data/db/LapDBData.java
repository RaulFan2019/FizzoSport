package cn.hwh.sports.data.db;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.xutils.ex.DbException;

import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.UploadDE;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class LapDBData {


    /**
     * 获取未完成的lap
     *
     * @return
     */
    public static LapDE getUnFinishLap(final String workoutStartTime, final int userId) {
        LapDE lap;
        try {
            lap = LocalApplication.getInstance().getDb().selector(LapDE.class)
                    .where("status", "=", SportEnum.SportStatus.RUNNING)
                    .and("workoutStartTime", "=", workoutStartTime)
                    .and("userId", "=", userId)
                    .findFirst();

            if (lap != null) {
                return lap;
            } else {
                return null;
            }
        } catch (DbException e) {
            return null;
        }
    }

    /**
     * 获取lap序号
     *
     * @return
     */
    public static long getLapIndexFromWorkout(final int userId, final String workoutName) {
        try {
            List<LapDE> lapList = LocalApplication.getInstance().getDb().selector(LapDE.class)
                    .where("workoutStartTime", "=", workoutName).and("userId", "=", userId).findAll();
            if (lapList == null || lapList.size() == 0) {
                return 0;
            } else {
                return lapList.size();
            }
        } catch (DbException e) {
            return 0;
        }
    }

    /**
     * 建立新的lap信息
     */
    public static LapDE createNewLap(final String workoutStartTime, final int userId, final int ownerId) {
        // Log.v(TAG, "建立新的lap信息");
        String startTime = TimeU.nowTime();
        long index = LapDBData.getLapIndexFromWorkout(userId, workoutStartTime);
        LapDE mLapDE = new LapDE(System.currentTimeMillis(), startTime, workoutStartTime, 0, index, 0,
                SportEnum.SportStatus.RUNNING, userId, ownerId);
        saveLap(mLapDE);
        return mLapDE;
    }

//    public static List<LapDE> getLaps(final String workoutStartTime, final int userId){
//        try {
//            return LocalApplication.getInstance().getDb().selector(LapDE.class)
//                    .where("workoutStartTime","=",workoutStartTime).and("userId","=",userId)
//                    .findAll();
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 找分段信息
     * @param workoutStartTime
     * @param lapStartTime
     * @param userId
     * @return
     */
    public static LapDE getLapFromWorkout(final String workoutStartTime, final String lapStartTime, final int userId){
        try {
            return LocalApplication.getInstance().getDb().selector(LapDE.class)
                    .where("workoutStartTime","=",workoutStartTime)
                    .and("startTime","=",lapStartTime).and("userId","=",userId)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建新的session
     *
     * @param workoutStartTime
     * @param userId
     * @return
     */
    public static LapDE createWatchLap(final String workoutStartTime, final int userId) {
        LapDE session = new LapDE(System.currentTimeMillis(),
                workoutStartTime, workoutStartTime, 0, 0, 0, SportEnum.SportStatus.FINISH, userId, userId);

        //创建上传信息
        JSONObject uploadObj = new JSONObject();
        JSONArray sessionArray = new JSONArray();
        JSONObject sessionObj = new JSONObject();

        uploadObj.put("contenttype", "sessionhead");
        uploadObj.put("users_id", userId);
        uploadObj.put("starttime", workoutStartTime);

        sessionObj.put("starttime", session.startTime);
        sessionArray.add(sessionObj);
        uploadObj.put("session", sessionArray);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = userId;
        upload.workoutStartTime = workoutStartTime;
        UploadDBData.createNewUploadData(upload);

        return session;
    }

    /**
     * 结束手表Lap
     *
     * @param session
     */
    public static void finishWatchLap(final LapDE session) {

        //创建上传信息
        JSONObject uploadObj = new JSONObject();
        JSONArray sessionArray = new JSONArray();
        JSONObject sessionObj = new JSONObject();


        uploadObj.put("contenttype", "sessionend");
        uploadObj.put("users_id", session.userId);
        uploadObj.put("starttime", session.workoutStartTime);

        sessionObj.put("starttime", session.startTime);
        sessionArray.add(sessionObj);
        uploadObj.put("session", sessionArray);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = session.userId;
        upload.workoutStartTime = session.workoutStartTime;
        UploadDBData.createNewUploadData(upload);
    }

    /**
     * 更新lap
     *
     * @param lapDE
     */
    public static void update(final LapDE lapDE) {
        try {
            LocalApplication.getInstance().getDb().update(lapDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存LAP
     *
     * @param mLapDE
     */
    public static void saveLap(final LapDE mLapDE) {
        try {
            LocalApplication.getInstance().getDb().save(mLapDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存LAP
     *
     * @param laps
     */
    public static void save(final List<LapDE> laps) {
        try {
            LocalApplication.getInstance().getDb().save(laps);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
