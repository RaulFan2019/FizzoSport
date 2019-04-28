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
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UploadDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.UploadEE;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2016/11/29.
 */

public class UploadDBData {


    /**
     * 创建新的上传数据
     *
     * @param upload
     */
    public static void createNewUploadData(final UploadDE upload) {
        try {
            LocalApplication.getInstance().getDb().save(upload);
            EventBus.getDefault().post(new UploadEE());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     *
     * @param uploadDE
     */
    public static void delete(final UploadDE uploadDE) {
        try {
            LocalApplication.getInstance().getDb().delete(uploadDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     *
     * @param uploadDE
     */
    public static void delete(final List<UploadDE> uploadDE) {
        try {
            LocalApplication.getInstance().getDb().delete(uploadDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取与上传记录相关的数据
     *
     * @param workoutStartTime
     * @return
     */
    public static UploadDE getUploadDEByWorkoutStartTime(final String workoutStartTime) {
        try {
            UploadDE uploadDE = LocalApplication.getInstance().getDb().selector(UploadDE.class)
                    .where("workoutStartTime", "=", workoutStartTime).findFirst();
            return uploadDE;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取需要上传的数据
     *
     * @return
     */
    public synchronized static UploadDE getFirst(final int userId) {
        UploadDE result = null;
        try {
            result = LocalApplication.getInstance().getDb()
                    .selector(UploadDE.class)
                    .where("userId", "=", userId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取 workout 上传记录
     *
     * @param userId
     * @return
     */
    public synchronized static List<UploadDE> getWorkoutData(final int userId) {
        //获取第一条历史记录
        try {
            UploadDE firstWorkout = LocalApplication.getInstance().getDb().selector(UploadDE.class)
                    .where("userId", "=", userId).and("type", "=", AppEnum.UploadType.WORKOUT)
                    .findFirst();
            if (firstWorkout == null) {
                return null;
            } else {
                List<UploadDE> workoutList = LocalApplication.getInstance().getDb().selector(UploadDE.class)
                        .where("userId", "=", userId).and("type", "=", AppEnum.UploadType.WORKOUT)
                        .and("workoutStartTime", "=", firstWorkout.workoutStartTime).findAll();
                List<UploadDE> uploadList = new ArrayList<>();
                int uploadSize = 0;
//                JSONArray UploadArray = new JSONArray();

                for (UploadDE uploadDE : workoutList) {
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
     * 创建workout 的头
     *
     * @param workoutDE
     */
    public static void createWorkoutHeadData(final WorkoutDE workoutDE) {
        //创建上传信息
        JSONObject uploadObj = new JSONObject();
        uploadObj.put("contenttype", "workouthead");
        uploadObj.put("users_id", workoutDE.ownerId);
        uploadObj.put("starttime", workoutDE.startTime);
        uploadObj.put("type", workoutDE.type);
        uploadObj.put("resource", SportEnum.resource.APP);
        uploadObj.put("planned_goal",workoutDE.targetType);
        uploadObj.put("planned_duration",workoutDE.targetTime);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = workoutDE.userId;
        upload.workoutStartTime = workoutDE.startTime;
        createNewUploadData(upload);
    }

    /**
     * 创建session的开始
     *
     * @param lapDE
     */
    public static void createLapHeadData(final LapDE lapDE) {
        //创建上传信息
        JSONObject uploadObj = new JSONObject();
        JSONArray sessionArray = new JSONArray();
        JSONObject sessionObj = new JSONObject();

        uploadObj.put("contenttype", "sessionhead");
        uploadObj.put("users_id", lapDE.ownerId);
        uploadObj.put("starttime", lapDE.workoutStartTime);

        sessionObj.put("lap",lapDE.lapIndex);
        sessionObj.put("starttime", lapDE.startTime);
        sessionArray.add(sessionObj);
        uploadObj.put("session", sessionArray);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = lapDE.userId;
        upload.workoutStartTime = lapDE.workoutStartTime;
        createNewUploadData(upload);
    }

    /**
     * Lap结束
     *
     * @param lapDE
     */
    public static void createLapEndData(final LapDE lapDE) {
        //创建上传信息
        JSONObject uploadObj = new JSONObject();
        JSONArray sessionArray = new JSONArray();
        JSONObject sessionObj = new JSONObject();

        uploadObj.put("contenttype", "sessionend");
        uploadObj.put("users_id", lapDE.ownerId);
        uploadObj.put("starttime", lapDE.workoutStartTime);

        sessionObj.put("lap",lapDE.lapIndex);
        sessionObj.put("starttime", lapDE.startTime);
        sessionObj.put("duration", lapDE.duration);

        sessionArray.add(sessionObj);
        uploadObj.put("session", sessionArray);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = lapDE.userId;
        upload.workoutStartTime = lapDE.workoutStartTime;
        createNewUploadData(upload);
    }

    /**
     * 保存一个time split上传信息
     *
     * @param lapDE
     * @param timeSplitDE
     */
    public static void saveHrInfoInTimeSplit(final LapDE lapDE, final TimeSplitDE timeSplitDE) {
        List<HrDE> heartbeatList = HrDBData.getHrListInTimeSplit(timeSplitDE);

        JSONObject uploadObj = new JSONObject();
        JSONArray splitArray = new JSONArray();
        JSONObject splitObj = new JSONObject();

        uploadObj.put("contenttype", "sessiondata");
        uploadObj.put("users_id", lapDE.ownerId);
        uploadObj.put("starttime", lapDE.workoutStartTime);

        //split 数据
        splitObj.put("id", timeSplitDE.timeIndex);
        splitObj.put("duration", timeSplitDE.duration);
        splitObj.put("avgheartrate", timeSplitDE.avgHr);
        splitObj.put("type", SportEnum.splitType.TIME);
        splitArray.add(splitObj);

        uploadObj.put("splits", splitArray);

        //心率数据
        if (heartbeatList != null) {
            JSONArray sessionArray = new JSONArray();
            JSONObject sessionObj = new JSONObject();
            JSONArray hrArray = new JSONArray();
            for (HrDE heartbeat : heartbeatList) {
                JSONObject hrObj = new JSONObject();
                hrObj.put("timeoffset", heartbeat.lapTimeOffSet);
                hrObj.put("bpm", heartbeat.heartbeat);
                hrObj.put("stridefrequency",heartbeat.strideFreQuency);
                hrArray.add(hrObj);
            }
            sessionObj.put("lap",lapDE.lapIndex);
            sessionObj.put("starttime", lapDE.startTime);
            sessionObj.put("heartratedata", hrArray);
            sessionArray.add(sessionObj);

            uploadObj.put("session", sessionArray);
        }

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = lapDE.userId;
        upload.workoutStartTime = lapDE.workoutStartTime;
        createNewUploadData(upload);
    }


    /**
     * 保存一分钟内的位置信息
     * @param workoutDE
     * @param lapDE
     * @param timeSplitDE
     * @return
     */
    public static int saveLocationInfoInTimeSplit(final WorkoutDE workoutDE, final LapDE lapDE, final TimeSplitDE timeSplitDE){
        List<LocationDE> locList = LocationDBData.getLocListInTimeSplit(timeSplitDE);

        if (locList != null) {
            if (locList.size() != 0) {
                JSONObject uploadObj = new JSONObject();
                uploadObj.put("contenttype", "sessiondata");
                uploadObj.put("users_id", workoutDE.ownerId);
                uploadObj.put("starttime", workoutDE.startTime);
                uploadObj.put("length", workoutDE.length);
                uploadObj.put("duration", workoutDE.duration);
                uploadObj.put("calorie", workoutDE.calorie);
                uploadObj.put("minpace", 0);
                uploadObj.put("maxpace", 0);
                uploadObj.put("maxheight", workoutDE.maxHeight);
                uploadObj.put("minheight", workoutDE.minHeight);
                JSONArray lapArray = new JSONArray();
                JSONObject lapobj = new JSONObject();
                JSONArray locArray = new JSONArray();
                // JSONArray heartArray = new JSONArray();
                for (LocationDE loc : locList) {
                    JSONObject locObj = new JSONObject();
                    locObj.put("timeoffset", loc.timeOffSet);
                    locObj.put("latitude", loc.latitude);
                    locObj.put("longitude", loc.longitude);
                    locObj.put("altitude", loc.height);
                    locObj.put("haccuracy", loc.hAccuracy);
                    locObj.put("vaccuracy", loc.vAccuracy);
                    locObj.put("speed", loc.pace);
                    locObj.put("bpm", loc.hr);
                    locArray.add(locObj);
                }

                lapobj.put("lap", lapDE.lapIndex);
                lapobj.put("starttime", lapDE.startTime);
                lapobj.put("locationdata", locArray);
                lapArray.add(lapobj);
                uploadObj.put("session", lapArray);

                UploadDE upload = new UploadDE();
                upload.type = AppEnum.UploadType.WORKOUT;
                upload.info = uploadObj.toJSONString();
                upload.userId = lapDE.userId;
                upload.workoutStartTime = lapDE.workoutStartTime;
                createNewUploadData(upload);
            }
            return locList.size();
        } else {
            return 0;
        }
    }


    /**
     * 上传结束跑步分段数据
     *
     * @param split
     */
    public static void finishLengthSplit(LengthSplitDE split, int userId) {
        JSONObject uploadObj = new JSONObject();
        uploadObj.put("contenttype", "sessiondata");
        uploadObj.put("users_id", userId);
        uploadObj.put("starttime", split.workoutStartTime);
        JSONArray spliteArray = new JSONArray();
        JSONObject spliteObj = new JSONObject();
        spliteObj.put("id", split.lengthSplitId);
        spliteObj.put("length", split.length);
        spliteObj.put("duration", split.duration);
        spliteObj.put("avgheartrate", split.avgHr);
        spliteObj.put("minaltitude", split.minHeight);
        spliteObj.put("maxaltitude", split.maxHeight);
        spliteObj.put("latitude", split.latitude);
        spliteObj.put("longitude", split.longitude);
        spliteArray.add(spliteObj);
        uploadObj.put("kmsplits", spliteArray);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = split.userId;
        upload.workoutStartTime = split.workoutStartTime;
        createNewUploadData(upload);
    }

    /**
     * 上传workout 结束信息
     *
     * @param workout
     */
    public static void finishWorkout(final WorkoutDE workout) {
        JSONObject uploadObj = new JSONObject();
//        Log.v("finishWorkout","workout.length:" + workout.length);

        uploadObj.put("contenttype", "workoutend");
        uploadObj.put("users_id", workout.ownerId);
        uploadObj.put("starttime", workout.startTime);
        uploadObj.put("duration", workout.duration);
        uploadObj.put("maxheartrate", workout.maxHr);
        uploadObj.put("minheartrate", workout.minHr);
        uploadObj.put("avgheartrate", workout.avgHr);
        uploadObj.put("stepcount", workout.endStep);
        uploadObj.put("length", workout.length);
        uploadObj.put("pe5",workout.feel);

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = workout.userId;
        upload.workoutStartTime = workout.startTime;
        UploadDBData.createNewUploadData(upload);
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

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = workout.userId;
        upload.workoutStartTime = workout.startTime;
        UploadDBData.createNewUploadData(upload);
    }


    public static void saveHrInfoWithTimeSplitFromWatch(final LapDE session, final TimeSplitDE split
            , final List<HrDE> hrList) {

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

        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.WORKOUT;
        upload.info = uploadObj.toJSONString();
        upload.userId = session.userId;
        upload.workoutStartTime = session.workoutStartTime;
        UploadDBData.createNewUploadData(upload);
    }

    /**
     * 更新用户心率信息
     *
     * @param userId
     * @param targetHigh
     * @param targetLow
     * @param alertHr
     */
    public static void updateUserHr(final int userId, final int targetLow,
                                    final int targetHigh, final int alertHr) {
        UploadDE upload = new UploadDE();
        upload.type = AppEnum.UploadType.UPDATE_USER_HR;
        upload.userId = userId;
        upload.info = targetLow + ";" + targetHigh + ";" + alertHr;
        UploadDBData.createNewUploadData(upload);
    }
}
