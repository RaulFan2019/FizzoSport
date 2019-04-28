package cn.hwh.sports.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.sporting.SportingLockRunningOutdoorActivity;
import cn.hwh.sports.activity.sporting.SportingRunningOutdoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.LengthSplitDBData;
import cn.hwh.sports.data.db.LocationDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.EffortPointEE;
import cn.hwh.sports.entity.event.MaxHrEE;
import cn.hwh.sports.entity.event.RunningLocationEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.utils.CalorieU;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.EventU;
import cn.hwh.sports.utils.FileU;
import cn.hwh.sports.utils.GpsUtils;
import cn.hwh.sports.utils.HrZoneU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.TTsU;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class RunningService extends Service implements AMapLocationListener {

    /* contains */
    private static final String TAG = "RunningService";

    // 跑步命令
    public static final int CMD_PAUSE = 1;// 暂停命令
    public static final int CMD_CONTINUE = 2;// 继续命令
    public static final int CMD_FINISH = 3;// 结束命令

    //发送消息
    private static final int MSG_WAKE_UP = 0x11;
    private static final int MSG_CHECK_DAEMON = 0x12;

    //间隔时间参数
    private static final int INTERVAL_WAKE_UP = 15 * 60 * 1000;//定时唤醒
    private static final int INTERVAL_CHECK_DAEMON = 15 * 1000;//定时检查daemon是否存活
    private static final int INTERVAL_CHECK_BLE = 60 * 1000;//检查Daemon进程存活时间间隔
    private static final int INTERVAL_UPLOAD_LOCATION = 60 * 1000;//检查Daemon进程存活时间间隔

    //GPS获取参数
    private static final int GPS_INTERVAL = 2000;//获取GPS信息时间频率 单位:毫秒
    private static final int GPS_ACCURACY = 250;//精度控制
    private static final int GPS_LIMIT_MIN_PACE = 50;//速度过滤参数(最快速度)

    //通知相关
    private static final Class<?>[] mSetForegroundSignature = new Class[]{
            boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{
            int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{
            boolean.class};
    private static final int NOTIFICATION_ID = 1;
    private static boolean mReflectFlg = false;


    /* local data */

    /* 通知相关 */
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    private NotificationManager mNM;

    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long mStartTime;
    private static long NextTime = 0;
    //定时点亮屏幕
    private static Handler mLightHandler = null;
    private static Runnable mLightRa = null;
    private static long NextTimeLight = 0;
    //定时检查守护服务进程
    private static Handler mDaemonHandler = null;
    private static Runnable mDaemonRa = null;
    private static long NextTimeDaemon = 0;
    //定时检查蓝牙
    private static Handler mCheckBleHandler = null;
    private static Runnable mCheckBleRa = null;
    private static long NextTimeCheckBle = 0;
    //定时上传位置数据
    private static Handler mUploadLocationHandler = null;
    private static Runnable mUploadLocationRa = null;
    private static long NextTimeUploadLocation = 0;

    /* 定位相关 */
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption = null;
    private LatLng mLastLLatLng;// 用于计算距离

    //当前状态参数
    private boolean isRunning = false; //是否在跑步
    private long mDuration = 0;// 跑步用时
    public float mLength = 0;// 跑步距离

    private LapDE mLapDE;// 数据库lap信息
    private long mLapDuration;//分段耗时
    private float mLapLength = 0;// lap 距离

    private LengthSplitDE mLengthSplitDE;
    private long mLengthSplitDuration;//距离分段耗时
    private float mLengthSplitLength = 0;

    private TimeSplitDE mTimeSplitDE;
    private long mTimeSplitDuration;//时间分段耗时

    private int mPace;// 可靠速度
    private long mLastTimeForPace;// 用于计算速度的时间
    private int mLocSizeInMin = 0;//每分钟的获取点的数量
    private int mNullLocationTime = 0;
    private int mNetworkGpsCount = 0;

    //蓝牙状态
    private int mCurrHr;//当前心率
    private int mCurrCadence;//当前步频
    private long mLastAlertTime = 0;

    // 跑步历史参数
    private UserDE mUserDE;//用户信息
    private UserDE mOwnerDE;//代理用户信息
    private WorkoutDE mWorkoutDE; // 数据库中保存的跑步历史

    //接收器
    private RunningReceiver mLockReceiver;

    //背景播放器
    private MediaPlayer mPlaybackPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 广播接收器
     */
    class RunningReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.v(TAG, "RunningLockReceiver onReceive");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                launchLockActivity(context);
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (!isRunning) {
            return;
        }
        //定位不成功，扔掉
        if (location.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
            return;
        }
        //缓存定位结果扔掉
        if (location.getLocationType() == AMapLocation.LOCATION_TYPE_FIX_CACHE) {
            return;
        }
        //两次定位位置相差很小
        if (location.getLocationType() == AMapLocation.LOCATION_TYPE_SAME_REQ){
            return;
        }
//        if (location.getLocationType() == AMapLocation.LOCATION_TYPE_AMAP){
//            return;
//        }
        //若定位来源是基站，并且定位数量小于2，那么扔掉
        if (location.getLocationType() == AMapLocation.LOCATION_TYPE_CELL) {
            mNetworkGpsCount++;
            if (mNetworkGpsCount < 2) {
                return;
            }
        } else {
            mNetworkGpsCount = 0;
        }

        if (location != null && isRunning) {
            if (location.getLatitude() > 10
                    && location.getLongitude() > 10
                    && location.getAccuracy() < AppEnum.Gps.ACCURACY_POWER_LOW) {
                updateLocation(location);
            }
        }
    }

    /**
     * 收到跑步的消息或命令
     */
    Handler runningHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 收到暂停
                case CMD_PAUSE:
                    isRunning = false;
                    mTimerHandler.removeCallbacksAndMessages(null);
//                    mHandler.removeMessages(MSG_SAVE_UPLOAD);
                    stopLocation();
                    finishLap();
                    break;
                // 收到继续
                case CMD_CONTINUE:
                    isRunning = true;
                    mStartTime = System.currentTimeMillis();
                    mTimerRa.run();
//                    mHandler.sendEmptyMessage(MSG_SAVE_UPLOAD);
                    initGps();
                    if (mLapDE.status == SportEnum.SportStatus.FINISH) {
                        mLapDE = LapDBData.createNewLap(mWorkoutDE.startTime, mUserDE.userId, mOwnerDE.userId);
                        mLapDuration = 0;
                    }
                    if (mTimeSplitDE.status == SportEnum.SportStatus.FINISH) {
                        mTimeSplitDE = TimeSplitDBData.createNewTimeSplit(mWorkoutDE.startTime,
                                (int) (mWorkoutDE.duration / 60), mUserDE.userId, mOwnerDE.userId);
                        mLapDuration = 0;
                    }
                    break;
                // 收到完成
                case CMD_FINISH:
                    unregisterReceiver(mLockReceiver);
                    TTsU.playFinishRun(RunningService.this);
                    mLightHandler.removeCallbacksAndMessages(null);
                    mCheckBleHandler.removeCallbacksAndMessages(null);
                    mCheckBleHandler.removeCallbacksAndMessages(null);
                    finishWorkout();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
//        Log.v(TAG,"BleConnectEE :" + event.speed);
        if (!isRunning) {
            return;
        }

        if (event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_CONNECT_FAIL) {
            mCurrHr = 0;
        }

        //心率数据
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            mCurrHr = event.hr;
            mCurrCadence = event.cadence;
            HrDE hrDE = new HrDE(System.currentTimeMillis(),
                    mWorkoutDE.startTime, mWorkoutDE.startTime,(int)mDuration/1000,(int) (mLapDuration / 1000),
                    event.hr, mTimeSplitDE.timeSplitId, 0, event.cadence);
            HrDBData.save(hrDE);
            if (mWorkoutDE.maxHr < event.hr) {
                mWorkoutDE.maxHr = event.hr;
                EventBus.getDefault().post(new MaxHrEE(event.hr));
            }
            if (mWorkoutDE.minHr > event.hr) {
                mWorkoutDE.minHr = event.hr;
            }

            if (mCurrHr > (mUserDE.alertHr - 5)
                    && SportSpData.getAlertTtsEnable(RunningService.this)
                    && (System.currentTimeMillis() - mLastAlertTime) > 60000) {
                mLastAlertTime = System.currentTimeMillis();
                //没超过报警心率
                if (mCurrHr < mUserDE.alertHr) {
                    TTsU.playCloseToWarningHr(RunningService.this);
                } else {
                    TTsU.playOutWarningHr(RunningService.this);
                }
            }

            if (mWorkoutDE.startStep == 0) {
                mWorkoutDE.startStep = event.stepCount;
            }

            //防止手表步数清0
            if (event.stepCount > mWorkoutDE.startStep) {
                mWorkoutDE.endStep += (event.stepCount - mWorkoutDE.startStep);
            }
            mWorkoutDE.startStep = event.stepCount;
            WorkoutDBData.update(mWorkoutDE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindNotification();
        initRunParams();
        initAlarmData();
        initRunningReceiver();
        EventBus.getDefault().register(this);
        //记录跑步开始的状态
        writeRunningLog("RUNNING SERVICE START\n");
        playMute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = CMD_CONTINUE;
        if (intent != null && intent.hasExtra("CMD")) {
            cmd = intent.getIntExtra("CMD", CMD_CONTINUE);
        }
        runningHandler.sendEmptyMessage(cmd);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMute();
        //取消前台通知
        stopForegroundCompat(NOTIFICATION_ID);
        EventBus.getDefault().unregister(this);
    }


    /**********************************************************************
     **                  ##处理跑步数据的相关方法 ##
     *********************************************************************/

    /**
     * 结束lap信息
     */
    private void finishLap() {
        if (mLapDE.status == SportEnum.SportStatus.FINISH) {
            return;
        }
        mLapDE.duration = (mLapDuration / 1000);
        mLapDE.status = SportEnum.SportStatus.FINISH;
        LapDBData.update(mLapDE);
        UploadDBData.saveLocationInfoInTimeSplit(mWorkoutDE, mLapDE, mTimeSplitDE);

        //结束timeSplit
        mTimeSplitDE.status = SportEnum.SportStatus.FINISH;
        TimeSplitDBData.delete(mTimeSplitDE);
        HrDBData.deleteHrInSplit(mTimeSplitDE);

        mLastLLatLng = null;
        UploadDBData.createLapEndData(mLapDE);
    }

    /**
     * 结束跑步历史
     */
    private void finishWorkout() {
        isRunning = false;
        // 保存位置信息
        mLocSizeInMin = UploadDBData.saveLocationInfoInTimeSplit(mWorkoutDE, mLapDE, mTimeSplitDE);
        // 结束lap
        finishLap();
        // 保存split 信息
        finishLengthSplit(mLengthSplitDE, mLengthSplitLength, mLengthSplitDuration, 0, 0, true);
        // 保存跑步历史信息
        mWorkoutDE.status = SportEnum.SportStatus.FINISH;
        mWorkoutDE.duration = (mDuration / 1000);
        if (mWorkoutDE.length > 0) {
            mWorkoutDE.avgPace = ((int) (mWorkoutDE.duration * 1000 / mWorkoutDE.length));// 计算平均配速
        } else {
            mWorkoutDE.avgPace = 0;// 计算平均配速
        }

        //计算平均心率
        mWorkoutDE.avgHr = HrDBData.getAvgHrInWorkout(mWorkoutDE);
        WorkoutDBData.update(mWorkoutDE);
        UploadDBData.finishWorkout(mWorkoutDE);

        // 若距离小于100米, 距离太短不记录
        if (mWorkoutDE.length < 100) {
            WorkoutDBData.delete(mWorkoutDE);
        }
        stopSelf();
    }


    /**
     * 检查守护进程
     */
    private void checkDaemon() {
        if (!DeviceU.isWorked(RunningService.this, "cn.hwh.sports.service.ListenerService")) {
            //启动监听服务
            Intent listenerIntent = new Intent(RunningService.this, ListenerService.class);
            startService(listenerIntent);
        }
        long now = SystemClock.uptimeMillis();
        if (NextTimeDaemon == now + (INTERVAL_CHECK_DAEMON - now % INTERVAL_CHECK_DAEMON)) {
            return;
        }
        long NextTimeDaemon = now + (INTERVAL_CHECK_DAEMON - now % INTERVAL_CHECK_DAEMON);

        mDaemonHandler.postAtTime(mDaemonRa, NextTimeDaemon);
    }

    /**
     * 点量屏幕
     */
    private void brightKeyguard() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取电源管理器对象
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager
                .SCREEN_DIM_WAKE_LOCK, "bright");
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl.acquire();
        //点亮屏幕
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        //得到键盘锁管理器对象
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        kl.reenableKeyguard();
        //重新启用自动加锁
        wl.release();
        long now = SystemClock.uptimeMillis();
        if (NextTimeLight == now + (INTERVAL_WAKE_UP - now % INTERVAL_WAKE_UP)) {
            return;
        }
        long NextTimeLight = now + (INTERVAL_WAKE_UP - now % INTERVAL_WAKE_UP);
        mLightHandler.postAtTime(mLightRa, NextTimeLight);
    }

    /**
     * 检查蓝牙
     */
    private void checkBle() {
        initBle();
        long now = SystemClock.uptimeMillis();
        if (NextTimeCheckBle == now + (INTERVAL_CHECK_BLE - now % INTERVAL_CHECK_BLE)) {
            return;
        }
        long NextTimeCheckBle = now + (INTERVAL_CHECK_BLE - now % INTERVAL_CHECK_BLE);
        mCheckBleHandler.postAtTime(mCheckBleRa, NextTimeCheckBle);
    }


    /**
     * 启动锁屏界面
     *
     * @param context
     */
    private void launchLockActivity(Context context) {
        KeyguardManager km = (KeyguardManager)
                context.getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            Intent alarmLockIntent = new Intent(context,
                    SportingLockRunningOutdoorActivity.class);
            alarmLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            alarmLockIntent.putExtra("time", mDuration / 1000);
//            alarmLockIntent.putExtra("length", mLength);
//            alarmLockIntent.putExtra("speed", mPace);
            context.startActivity(alarmLockIntent);
        }
    }

    /**
     * 初始化跑步参数
     */
    private void initRunParams() {
        //获取用户信息
        mUserDE = LocalApplication.getInstance().getLoginUser(RunningService.this);
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mOwnerDE = UserDBData.getUserById(mWorkoutDE.ownerId);

        mLapDE = LapDBData.getUnFinishLap(mWorkoutDE.startTime, mUserDE.userId);
        if (mLapDE == null) {
            mLapDE = LapDBData.createNewLap(mWorkoutDE.startTime, mUserDE.userId, mOwnerDE.userId);
        } else {
            LocationDE location = LocationDBData.getLocationByLap(mLapDE, false);
            if (location != null) {
                mLastLLatLng = new LatLng(location.latitude, location.longitude);
            }
        }

        LocationDE locationDE = LocationDBData.getLocationByLap(mLapDE, false);
        if (locationDE != null) {
            mLastLLatLng = new LatLng(locationDE.latitude, locationDE.longitude);
        }

        mTimeSplitDE = TimeSplitDBData.getUnFinishTimeSplit(mWorkoutDE.startTime, mUserDE.userId);
        if (mTimeSplitDE == null) {
            mTimeSplitDE = TimeSplitDBData.createNewTimeSplit(mWorkoutDE.startTime,
                    (int) (mWorkoutDE.duration / 60), mUserDE.userId, mOwnerDE.userId);
        }

        mLengthSplitDE = LengthSplitDBData.getUnFinishSplit(mWorkoutDE.startTime);
        if (mLengthSplitDE == null) {
            mLengthSplitDE = LengthSplitDBData.createNewSplit(mWorkoutDE.length,
                    mWorkoutDE.startTime, mUserDE.userId, mOwnerDE.userId);
        }
        // 初始化时间参数
        mDuration = mWorkoutDE.duration * 1000;
        mLengthSplitDuration = mLengthSplitDE.duration * 1000;
        mTimeSplitDuration = mTimeSplitDE.duration * 1000;
        mLapDuration = mLapDE.duration * 1000;

        // 初始化距离参数
        mLength = mWorkoutDE.length;
        mLapLength = mLapDE.length;
        mLengthSplitLength = mLengthSplitDE.length;
        // 速度参数
        mPace = 0;
        mLastTimeForPace = mDuration;

        isRunning = true;
    }

    /**
     * 初始化定时事件
     */
    private void initAlarmData() {
        //计时器
        mTimerHandler = new Handler();
        mTimerRa = new Runnable() {
            @Override
            public void run() {
                updateTimer();
            }
        };
        mTimerHandler.removeCallbacksAndMessages(null);
        //定时点亮屏幕
        mLightHandler = new Handler();
        mLightRa = new Runnable() {
            @Override
            public void run() {
                brightKeyguard();
            }
        };
        mLightHandler.removeCallbacksAndMessages(null);

        //定时检查守护服务进程
        mDaemonHandler = new Handler();
        mDaemonRa = new Runnable() {
            @Override
            public void run() {
                checkDaemon();
            }
        };
        mDaemonHandler.removeCallbacksAndMessages(null);

        //定时检查蓝牙
        mCheckBleHandler = new Handler();
        mCheckBleRa = new Runnable() {
            @Override
            public void run() {
                checkBle();
            }
        };
        mCheckBleHandler.removeCallbacksAndMessages(null);

    }


    /**
     * 初始化跑步接收器
     */
    private void initRunningReceiver() {
        mLockReceiver = new RunningReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mLockReceiver, filter);
    }

    /**
     * 初始化BLE
     */
    private void initBle() {
        //初始化蓝牙
        BleManager.getBleManager().addNewConnect(mOwnerDE.bleMac);
    }

    /**
     * 更新时间
     */
    private void updateTimer() {
//        Log.v(TAG, "updateTimer isRunning:" + isRunning);
        if (!isRunning) {
            return;
        }
        long disparityTime = System.currentTimeMillis() - mStartTime;// 时间
        mDuration += disparityTime;
        mLapDuration += disparityTime;
        mTimeSplitDuration += disparityTime;
        mLengthSplitDuration += disparityTime;

        long now = SystemClock.uptimeMillis();
        if (NextTime == now + (1000 - now % 1000)) {
            return;
        }
        NextTime = now + (1000 - now % 1000);
        mStartTime = System.currentTimeMillis();
        mTimerHandler.postAtTime(mTimerRa, NextTime);

        x.task().post(new Runnable() {
            @Override
            public void run() {
                saveTimerData();
            }
        });
    }

    /**
     * 保存这一秒发生的事情
     */
    private void saveTimerData() {
        if (!isRunning) {
            return;
        }
        // 这里发送到activity
        EventBus.getDefault().post(new RunningTimeEE(mDuration / 1000, mWorkoutDE.length, mWorkoutDE.endStep));

        mWorkoutDE.duration = mDuration / 1000;
        mTimeSplitDE.duration = mTimeSplitDuration / 1000;
        mLapDE.duration = mLapDuration / 1000;
        mLengthSplitDE.duration = mLengthSplitDuration / 1000;

        TimeSplitDBData.update(mTimeSplitDE);
        LengthSplitDBData.update(mLengthSplitDE);

        //若分段时间超过1分钟
        if (mTimeSplitDuration > 60 * 1000) {
            mTimeSplitDE.duration = 60;
            mTimeSplitDE.status = SportEnum.SportStatus.FINISH;
            mTimeSplitDE.avgHr = HrDBData.getAvgHrInSplit(mTimeSplitDE);
            TimeSplitDBData.update(mTimeSplitDE);

            //计算卡路里
            float cal = CalorieU.getMinutesCalorie(mOwnerDE, mTimeSplitDE.avgHr);
            Log.v(TAG, "cal:" + cal);
            if (cal > 0) {
                mWorkoutDE.calorie += cal;
            }
            //计算锻炼点数
            mWorkoutDE.effortPoint += EffortPointU.getMinutesEffortPoint(mTimeSplitDE.avgHr, mOwnerDE.maxHr);

            mTimeSplitDuration = mTimeSplitDuration - 60000;
            UploadDBData.saveHrInfoInTimeSplit(mLapDE, mTimeSplitDE);

            //上传轨迹
            mLocSizeInMin = UploadDBData.saveLocationInfoInTimeSplit(mWorkoutDE, mLapDE, mTimeSplitDE);
            if (mLocSizeInMin == 0) {
                mNullLocationTime++;
                //3分钟都没定位 重启
                if (mNullLocationTime > 2) {
                    mNullLocationTime = 0;
                    stopLocation();
                    initGps();
                    writeRunningLog("RESET GPS\n");
                }
            } else {
                mNullLocationTime = 0;
            }
            String voice = "";
            //播报分钟语音
            if (BleManager.getBleManager().mBleConnectE == null
                    || BleManager.getBleManager().mBleConnectE.mIsConnected == false){
                TTsU.playOutWarningHr(RunningService.this);
//                voice += "请检查蓝牙设备是否断开";
//                showVoice(voice);
            }else {
                if ((mTimeSplitDE.timeIndex + 1) % SportSpData.getCurrHrFreqTts(RunningService.this) == 0
                        && mCurrHr != 0) {
//                    voice = "当前心率 " + mCurrHr + ",当前步频 " + mCurrCadence;
//                    if (mCurrCadence == 0){
//                        voice = "当前心率 " + mCurrHr;
//                    }
//                    voice += "在" + HrZoneU.getDescribeByHrPercent(mCurrHr * 100 / mUserDE.maxHr) + "区";
//                    showVoice(voice);
                    int zone = HrZoneU.getZoneByHrPercent(mCurrHr * 100 / mUserDE.maxHr);
                    TTsU.playMinuteOutdoorVoice(RunningService.this,mCurrHr,mCurrCadence,zone);
                }
            }

            if (isRunning) {
                mTimeSplitDE = TimeSplitDBData.createNewTimeSplit(mWorkoutDE.startTime, (int) (mDuration / 60000),
                        mUserDE.userId, mOwnerDE.userId);
            }
            WorkoutDBData.update(mWorkoutDE);
            EventBus.getDefault().post(new EffortPointEE(mWorkoutDE.effortPoint));
        }
        LapDBData.update(mLapDE);
        LengthSplitDBData.update(mLengthSplitDE);
        WorkoutDBData.update(mWorkoutDE);

//        if (mCurrHr != 0) {
            RequestParams params = RequestParamsBuilder.buildUploadRecentHr(RunningService.this, UrlConfig.URL_UPLOAD_RECENT_HR, mUserDE.userId, mCurrHr);
            x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                @Override
                public void onSuccess(BaseRE result) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
//        }
    }

    /**
     * 保存地址信息
     *
     * @param location
     */
    private void updateLocation(AMapLocation location) {
        // 若上次位置信息不为空
        if (mLastLLatLng != null) {
            float tempLength = AMapUtils.calculateLineDistance(mLastLLatLng,
                    new LatLng(location.getLatitude(), location.getLongitude()));

            // 保存split海拔信息
            if (location.hasAltitude()) {
                double Altitude = location.getAltitude();
                if (Altitude > mLengthSplitDE.maxHeight) {
                    mLengthSplitDE.maxHeight = Altitude;
                }
                if (Altitude < mLengthSplitDE.minHeight) {
                    mLengthSplitDE.minHeight = Altitude;
                }
            }
            int lPace = 0;
            if (tempLength == 0) {
                lPace = 0;
            } else {
                lPace = (int) ((mDuration - mLastTimeForPace) / tempLength);
            }
//            }
            //速度不符合正常速度的
            if (lPace < GPS_LIMIT_MIN_PACE) {
                lPace = 0;
            }
//            若速度是0 不做任何记录
            if (lPace == 0) {
                return;
            }
            mPace = lPace;

            // 保存速度信息
            mLength += tempLength;
            mLapLength += tempLength;
            mLengthSplitLength += tempLength;
            mLastTimeForPace = mDuration;

            // 保存距离信息
            mWorkoutDE.length = mLength;
            mLapDE.length = mLapLength;
        }

        // 保存split信息
        if (mLengthSplitDE.status == SportEnum.SportStatus.RUNNING) {
            if (mLengthSplitLength < 1000) {
                mLengthSplitDE.length = mLengthSplitLength;
                mLengthSplitDE.duration = mLengthSplitDuration / 1000;
                LengthSplitDBData.update(mLengthSplitDE);
                // 分段结束
            } else {
                saveSplit(mLastLLatLng, location);
            }
        }
        mLastLLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mWorkoutDE.duration = mDuration / 1000;
        mLapDE.duration = mDuration / 1000;
        WorkoutDBData.update(mWorkoutDE);
        LapDBData.update(mLapDE);

        // 保存location信息到DB
        LocationDE tempLocation = new LocationDE(System.currentTimeMillis(), mWorkoutDE.startTime,
                mLapDE.startTime, location.getLatitude(), location.getLongitude(), location.getAccuracy(),
                location.getAltitude(), location.getAccuracy(), (mLapDuration / 1000), mPace,
                mUserDE.userId,mOwnerDE.userId,mCurrHr,mTimeSplitDE.timeSplitId);
        LocationDBData.save(tempLocation);

        EventBus.getDefault().post(new RunningLocationEE(mLength, mPace));
    }


    /**
     * 保存分段
     *
     * @param LastLLatLng
     * @param location
     */
    private void saveSplit(LatLng LastLLatLng, AMapLocation location) {
        int splitSize = (int) (mLengthSplitLength / 1000);
        long time = (long) (mLengthSplitDuration * 1000 / mLengthSplitLength);
        double LatitudeChange = (location.getLatitude() - LastLLatLng.latitude) / splitSize;
        double LongitudeChange = (location.getLongitude() - LastLLatLng.longitude) / splitSize;
        for (int i = 0; i < splitSize; i++) {
            finishLengthSplit(mLengthSplitDE, 1000, time, (LatitudeChange * (i + 1) + LastLLatLng.latitude),
                    (LongitudeChange * (i + 1) + LastLLatLng.longitude), false);
            int lastSplitId = mLengthSplitDE.splitIndex;
            mLengthSplitDE = new LengthSplitDE(System.currentTimeMillis(), (lastSplitId + 1),
                    mWorkoutDE.startTime, 0, 0, 0, 0, 0, 0, 0, SportEnum.SportStatus.RUNNING, mUserDE.userId, mOwnerDE.userId);
            mLengthSplitDuration = mLengthSplitDuration - time;
            mLengthSplitLength = mLengthSplitLength - 1000;
        }
        // Log.v(TAG, "创建新的splite信息");
        LengthSplitDBData.save(mLengthSplitDE);


    }

    /**
     * 结束split
     */
    private void finishLengthSplit(LengthSplitDE DBSplit, float length, long time,
                                   double Latitude, double Longitude, boolean lastSplit) {
        // Log.v(TAG, "结束splite");
        int avgHeartRate = HrDBData.getAvgHrInLengthSplit(DBSplit);
        DBSplit.length = length;
        DBSplit.duration = (time / 1000);
        DBSplit.status = SportEnum.SportStatus.FINISH;
        DBSplit.avgHr = avgHeartRate;
        DBSplit.latitude = Latitude;
        DBSplit.longitude = Longitude;

        int pace = 0;
        if (DBSplit.length != 0) {
            pace = (int) (DBSplit.duration * 1000 / DBSplit.length);
            if (pace != 0) {
                if (mWorkoutDE.maxPace < pace) {
                    mWorkoutDE.maxPace = pace;
                }
                if (mWorkoutDE.minPace == 0) {
                    mWorkoutDE.minPace = pace;
                }
                if (mWorkoutDE.minPace > pace) {
                    mWorkoutDE.minPace = pace;
                }
            }
        }
        LengthSplitDBData.update(DBSplit);
        UploadDBData.finishLengthSplit(DBSplit, mUserDE.userId);
        if (!lastSplit) {
            TTsU.playPerKm(RunningService.this, (int) (mLength / 1000), (int) (mDuration / 1000), pace);
        }
    }


    /**
     * 初始化GPS取点服务
     */
    private void initGps() {
        locationClient = new AMapLocationClient(RunningService.this);
        locationOption = new AMapLocationClientOption();

        // 设置定位模式为仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        if (!PreferencesData.getOnlyIsGpsLocMode(RunningService.this)) {
//            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
//        } else {
//            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        }
        // 设置定位监听
        locationClient.setLocationListener(this);

        locationOption.setInterval(GPS_INTERVAL);//定位间隔
        locationOption.setNeedAddress(false);//不需要返回位置信息
        locationOption.setOnceLocation(false);//持续定位
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }


    /**
     * 停止定位
     */
    private void stopLocation() {
        if (null != locationClient) {
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 播放语音提示
     *
     * @param voice
     */
    private void showVoice(final String voice) {
        if (SportSpData.getTtsEnable(this)) {
            LocalApplication.getInstance().getUscTtsUtil(this).showVoice(voice);
        }
    }


    /**
     * 写入跑步日志
     */
    private void writeRunningLog(final String Title) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Save the log on SD card if available
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    String sdcardPath = FileConfig.CRASH_PATH;
                    File crashPath = new File(sdcardPath);
                    if (!crashPath.exists()) {
                        crashPath.mkdir();
                    }
                    //Gps状态
                    String GpsStatus = "Gps状态: " + GpsUtils.getStatus(RunningService.this) + "\n";
                    //内存
                    String memStr = "系统内存:" + DeviceU.getTotalMemory() + "\n";
                    //电量
                    Intent batteryInfoIntent = DeviceU.getBatterPower(RunningService.this);
                    String powerStatus = "电量: " + batteryInfoIntent.getIntExtra("level", 0) + "\n";

                    String logInfo = Title + GpsStatus + memStr + powerStatus;
                    FileU.writeLog(logInfo, sdcardPath + "/");
                }
            }
        });
    }

    /**
     * 停止播放背景音乐
     */
    private void stopMute() {
        try {
            mPlaybackPlayer.stop();
            mPlaybackPlayer.release();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mPlaybackPlayer = null;
    }


    /**
     * 播放背景音乐
     */
    private void playMute() {
        mPlaybackPlayer = new MediaPlayer();
        mPlaybackPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                Log.e(TAG,"onError");
                stopMute();
                playMute();
                return false;
            }
        });
        try {
            mPlaybackPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd("mute.mp3");
            mPlaybackPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mPlaybackPlayer.setLooping(true);
            mPlaybackPlayer.prepare();
            mPlaybackPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    /*****************************************************************
     *                 ## 和通知相关的方法
     ****************************************************************/

    /**
     * 服务邦定通知服务
     */
    private void bindNotification() {
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mStartForeground = RunningService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = RunningService.class.getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }
        try {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "OS doesn't have Service.startForeground OR Service.setForeground!");
        }
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SportingRunningOutdoorActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Fizzo");
        builder.setContentText("正在室外跑步.");
        Notification notification = builder.build();

        startForegroundCompat(NOTIFICATION_ID, notification);
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        if (mReflectFlg) {
            // If we have the new startForeground API, then use it.
            if (mStartForeground != null) {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }

            // Fall back on the old API.
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNM.notify(id, notification);
        } else {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法startForeground设置前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground设置前台运行 */
            if (Build.VERSION.SDK_INT >= 5) {
                startForeground(id, notification);
            } else {
                // Fall back on the old API.
                mSetForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
                mNM.notify(id, notification);
            }
        }
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        if (mReflectFlg) {
            // If we have the new stopForeground API, then use it.
            if (mStopForeground != null) {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }

            // Fall back on the old API.  Note to cancel BEFORE changing the
            // foreground state, since we could be killed at that point.
            mNM.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        } else {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法stopForeground停止前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground停止前台运行 */

            if (Build.VERSION.SDK_INT >= 5) {
                stopForeground(true);
            } else {
                // Fall back on the old API.  Note to cancel BEFORE changing the
                // foreground state, since we could be killed at that point.
                mNM.cancel(id);
                mSetForegroundArgs[0] = Boolean.FALSE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
            }
        }
    }

    /**
     * 和通知相关的 API
     **/
    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
//            Log.w("ApiDemos", "Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            // Should not happen.
//            Log.w("ApiDemos", "Unable to invoke method", e);
        }
    }


}
