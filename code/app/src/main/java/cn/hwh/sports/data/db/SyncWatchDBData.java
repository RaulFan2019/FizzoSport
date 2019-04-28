package cn.hwh.sports.data.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.SyncWatchDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UploadDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.UploadEE;

/**
 * Created by Raul.Fan on 2016/11/29.
 */

public class SyncWatchDBData {


    /**
     * 创建新的上传数据
     *
     * @param upload
     */
    public static void createNewUploadData(final SyncWatchDE upload) {
        try {
            LocalApplication.getInstance().getDb().save(upload);
            EventBus.getDefault().post(new UploadEE());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个手表workout
     *
     * @param userId
     * @param startTime
     * @return
     */
    public static WorkoutDE createWatchWorkout(final int userId, final String startTime) {
        WorkoutDE sport = new WorkoutDE(System.currentTimeMillis(), startTime, startTime, 0, 0, 0, 0,
                999, 0, 0, 0, 0, 0, 999, userId, userId, 0, 0, SportEnum.SportStatus.FINISH,
                SportEnum.EffortType.FREE_INDOOR, 0, SportEnum.TargetType.DEFAULT, 0 ,3);

        //创建上传信息
        JSONObject uploadObj = new JSONObject();
        uploadObj.put("contenttype", "workouthead");
        uploadObj.put("users_id", userId);
        uploadObj.put("starttime", sport.startTime);
        uploadObj.put("type", sport.type);
        uploadObj.put("resource", SportEnum.resource.WATCH);

        SyncWatchDE syncWatchDE = new SyncWatchDE();
        syncWatchDE.info = uploadObj.toJSONString();
        syncWatchDE.userId = userId;
        syncWatchDE.workoutStartTime = sport.startTime;
        createNewUploadData(syncWatchDE);
        return sport;
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

        SyncWatchDE syncWatchDE = new SyncWatchDE();
        syncWatchDE.info = uploadObj.toJSONString();
        syncWatchDE.userId = userId;
        syncWatchDE.workoutStartTime = workoutStartTime;

        createNewUploadData(syncWatchDE);
        return session;
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
                avgHr, SportEnum.SportStatus.FINISH, 60, userId, userId);
//        DBDataHeartbeat.save(hrList);
        JSONObject uploadObj = new JSONObject();
        JSONArray splitArray = new JSONArray();
        JSONObject splitObj = new JSONObject();

        uploadObj.put("contenttype", "sessiondata");
        uploadObj.put("users_id", session.userId);
        uploadObj.put("starttime", session.workoutStartTime);

        //split 数据
        splitObj.put("id", split.timeIndex);
        splitObj.put("duration", split.duration);
        splitObj.put("avgheartrate", split.avgHr);
        splitArray.add(splitObj);

        uploadObj.put("splits", splitArray);

        //心率数据
        if (hrList != null) {
            JSONArray sessionArray = new JSONArray();
            JSONObject sessionObj = new JSONObject();
            JSONArray hrArray = new JSONArray();
            for (HrDE heartbeat : hrList) {
                JSONObject hrObj = new JSONObject();
                hrObj.put("timeoffset", heartbeat.timeOffSet);
                hrObj.put("bpm", heartbeat.heartbeat);
                hrObj.put("motiontype", heartbeat.actionType);
                hrObj.put("motionintensity", heartbeat.actionCount);
                hrArray.add(hrObj);
            }
            sessionObj.put("starttime", session.startTime);
            sessionObj.put("heartratedata", hrArray);
            sessionArray.add(sessionObj);
            uploadObj.put("session", sessionArray);
        }

        SyncWatchDE syncWatchDE = new SyncWatchDE();
        syncWatchDE.info = uploadObj.toJSONString();
        syncWatchDE.userId = userId;
        syncWatchDE.workoutStartTime = session.workoutStartTime;

        createNewUploadData(syncWatchDE);
        return split;
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

        SyncWatchDE syncWatchDE = new SyncWatchDE();
        syncWatchDE.info = uploadObj.toJSONString();
        syncWatchDE.userId = session.userId;
        syncWatchDE.workoutStartTime = session.workoutStartTime;

        createNewUploadData(syncWatchDE);
    }

    /**
     * 上传workout 结束信息
     *
     * @param workout
     */
    public static void finishWatchWorkout(final WorkoutDE workout, final long stepCount) {
        JSONObject uploadObj = new JSONObject();

        uploadObj.put("contenttype", "workoutend");
        uploadObj.put("users_id", workout.userId);
        uploadObj.put("starttime", workout.startTime);
        uploadObj.put("duration", workout.duration);
        uploadObj.put("maxheartrate", workout.maxHr);
        uploadObj.put("minheartrate", workout.minHr);
        uploadObj.put("avgheartrate", workout.avgHr);
        uploadObj.put("stepcount", stepCount);

        SyncWatchDE syncWatchDE = new SyncWatchDE();
        syncWatchDE.info = uploadObj.toJSONString();
        syncWatchDE.userId = workout.userId;
        syncWatchDE.workoutStartTime = workout.startTime;

        createNewUploadData(syncWatchDE);
    }


    /**
     * 获取 workout 上传记录
     *
     * @param userId
     * @return
     */
    public synchronized static List<SyncWatchDE> getWorkoutData(final int userId) {
        //获取第一条历史记录
        try {
            SyncWatchDE firstWorkout = LocalApplication.getInstance().getDb().selector(SyncWatchDE.class)
                    .where("userId", "=", userId).findFirst();
            if (firstWorkout == null) {
                return null;
            } else {
                List<SyncWatchDE> workoutList = LocalApplication.getInstance().getDb().selector(SyncWatchDE.class)
                        .where("userId", "=", userId).and("workoutStartTime", "=", firstWorkout.workoutStartTime)
                        .findAll();
                List<SyncWatchDE> uploadList = new ArrayList<>();
                int uploadSize = 0;

                for (SyncWatchDE uploadDE : workoutList) {
                    if (uploadSize > 1024 * 100) {
                        return uploadList;
                    }
                    uploadList.add(uploadDE);
                    uploadSize += uploadDE.info.length();
                }
                return uploadList;
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除列表
     *
     * @param syncWatchDEList
     */
    public static void delete(List<SyncWatchDE> syncWatchDEList) {
        try {
            LocalApplication.getInstance().getDb().delete(syncWatchDEList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
