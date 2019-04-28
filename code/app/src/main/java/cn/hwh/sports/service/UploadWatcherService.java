package cn.hwh.sports.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutSyncDBData;
import cn.hwh.sports.entity.db.UploadDE;
import cn.hwh.sports.entity.db.WorkoutSyncDE;
import cn.hwh.sports.entity.event.UploadEE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.WorkoutRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.utils.NetworkU;

/**
 * Created by Raul on 2015/10/29.
 * 监听需要上传的数据的后台服务
 */
public class UploadWatcherService extends Service {

    /* contains */
    private static final String TAG = "UploadWatcherService";

    public static final int MSG_UPLOAD = 0x01;

    /* local data */
    private long delayTime = 5000;
    private static final long DelayTimeMax = 60 * 1000 * 5;

    private ConnectionChangeReceiver myReceiver;

    private int mRepeatTime = 0;//重试次数
    private boolean uploading = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startUpload();
        registerReceiver();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 收到新上传的数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UploadEE event) {
        startUpload();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopUpload();
        unregisterReceiver();
        super.onDestroy();
    }

    Handler uploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPLOAD) {
                //若正在上传
                if (uploading) {
                    return;
                }
                uploading = true;
                // 若网络不好, 过段时间重试
                if (!NetworkU.isNetworkConnected(UploadWatcherService.this)) {
                    return;
                }
                if (LocalApplication.getInstance().getLoginUser(UploadWatcherService.this) == null) {
                    return;
                }
                //先找有没有记录数据要上传
                List<UploadDE> workoutList = UploadDBData.getWorkoutData(LocalApplication.getInstance()
                        .getLoginUser(UploadWatcherService.this).userId);
                if (workoutList == null) {
                    // 若需要上传的数据是空, 重新获取数据
                    UploadDE uploadDE = UploadDBData.getFirst(LocalApplication.getInstance()
                            .getLoginUser(UploadWatcherService.this).userId);
                    if (uploadDE != null) {
                        postData(uploadDE);
                    } else {
                        uploading = false;
                        return;
                    }
                } else {
                    postWorkoutData(workoutList);
                }
            }
        }
    };


    /**
     * 开始上传线程
     */
    private void startUpload() {
        // 启动计时线程，开始上传
        uploadHandler.sendEmptyMessage(MSG_UPLOAD);
    }

    private void stopUpload() {
        if (uploadHandler != null) {
            uploadHandler.removeMessages(MSG_UPLOAD);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        this.unregisterReceiver(myReceiver);
    }

    /**
     * 发送数据
     */
    private void postData(final UploadDE uploadDE) {
//        Log.v(TAG,"postData uploadEntity.getUploadType():" + uploadEntity.getUploadType());
        //若上传的数据是跑步相关
        if (uploadDE.type == AppEnum.UploadType.WORKOUT) {
            postWorkout(uploadDE);
        } else if (uploadDE.type == AppEnum.UploadType.UPDATE_USER_HR) {
            postUpdateUserHr(uploadDE);
        }
    }


    /**
     * 上传记录列表信息
     *
     * @param uploadDEList
     */
    private void postWorkoutData(final List<UploadDE> uploadDEList) {
        String uploadString = "[";
        for (UploadDE uploadDE : uploadDEList) {
            uploadString += uploadDE.info + ",";
        }
        uploadString = uploadString.substring(0, uploadString.length() - 1);
        uploadString += "]";

        RequestParams requestParams = RequestParamsBuilder.buildSaveWorkoutSegmentArrayRP(UploadWatcherService.this
                , UrlConfig.URL_SAVE_DATA_SEGMENT_ARRAY, uploadString);
//        Log.v(TAG,"postWorkout:" + uploadEntity.info);
        x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

            @Override
            public void onSuccess(BaseRE s) {
                if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    mRepeatTime = 0;
                    delayTime = 100;
                    UploadDBData.delete(uploadDEList);
                    analysisWorkoutResponse(s.result, uploadDEList.get(0).workoutStartTime, uploadDEList.get(0).userId);
                } else {
                    mRepeatTime++;
                    delayTime += 1000;
                    if (delayTime > DelayTimeMax) {
                        delayTime = DelayTimeMax;
                    }
                    if (mRepeatTime > 5) {
                        UploadDBData.delete(uploadDEList);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                delayTime += 1000;
                if (delayTime > DelayTimeMax) {
                    delayTime = DelayTimeMax;
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
                uploading = false;
                uploadHandler.sendEmptyMessageDelayed(MSG_UPLOAD, delayTime);
            }
        });
    }

    /**
     * 上传跑步信息
     */
    private void postWorkout(final UploadDE uploadDE) {
        RequestParams requestParams = RequestParamsBuilder.buildSaveWorkoutSegmentRP(UploadWatcherService.this
                , UrlConfig.URL_SAVE_DATA_SEGMENT, uploadDE.info);
//        Log.v(TAG,"postWorkout:" + uploadEntity.info);
        x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

            @Override
            public void onSuccess(BaseRE s) {
                if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    mRepeatTime = 0;
                    delayTime = 100;
                    UploadDBData.delete(uploadDE);
                    analysisWorkoutResponse(s.result, uploadDE.workoutStartTime, uploadDE.userId);
                } else {
                    mRepeatTime++;
                    delayTime += 1000;
                    if (delayTime > DelayTimeMax) {
                        delayTime = DelayTimeMax;
                    }
                    if (mRepeatTime > 5) {
                        UploadDBData.delete(uploadDE);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                delayTime += 1000;
                if (delayTime > DelayTimeMax) {
                    delayTime = DelayTimeMax;
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
                uploading = false;
                uploadHandler.sendEmptyMessageDelayed(MSG_UPLOAD, delayTime);
            }
        });
    }


    /**
     * 上传用户心率数据
     *
     * @param uploadDE
     */
    private void postUpdateUserHr(final UploadDE uploadDE) {
        String[] info = uploadDE.info.split(";");
        RequestParams requestParams = RequestParamsBuilder.buildUpdateUserHrRP(UploadWatcherService.this
                , UrlConfig.URL_UPDATE_USER_INFO, info[0], info[1], info[2]);
//        Log.v(TAG,"postWorkout:" + uploadEntity.info);
        x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

            @Override
            public void onSuccess(BaseRE s) {
                if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    mRepeatTime = 0;
                    delayTime = 100;
                    UploadDBData.delete(uploadDE);
                } else {
                    mRepeatTime++;
                    delayTime += 1000;
                    if (delayTime > DelayTimeMax) {
                        delayTime = DelayTimeMax;
                    }
                    if (mRepeatTime > 5) {
                        UploadDBData.delete(uploadDE);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                delayTime += 1000;
                if (delayTime > DelayTimeMax) {
                    delayTime = DelayTimeMax;
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
                uploading = false;
                uploadHandler.sendEmptyMessageDelayed(MSG_UPLOAD, delayTime);
            }
        });
    }

    /**
     * 解析历史上传结果
     *
     * @param result
     */
    private void analysisWorkoutResponse(final String result, final String workoutStartTime, final int userId) {
        WorkoutRE workoutRE = JSON.parseObject(result, WorkoutRE.class);
        //是否是新的Workout
        if (workoutRE.newWorkout == SportEnum.NewWorkout.NEW) {
            WorkoutSyncDE workoutSyncDE = new WorkoutSyncDE(System.currentTimeMillis(), workoutStartTime,
                    workoutRE.id, userId);
            WorkoutSyncDBData.save(workoutSyncDE);
        }
        if (workoutRE.isworkoutend == SportEnum.IsWorkoutEnd.YES) {
            EventBus.getDefault().post(new WorkoutEndEE());
        }

    }

    /**
     * @author Javen
     */
    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                //改变背景或者 处理网络的全局变量
            } else {
                uploading = false;
                uploadHandler.sendEmptyMessage(MSG_UPLOAD);
                //改变背景或者 处理网络的全局变量
            }
        }
    }
}
