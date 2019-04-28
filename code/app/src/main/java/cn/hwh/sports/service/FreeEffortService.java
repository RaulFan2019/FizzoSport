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

import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.run.FreeEffortLockActivity;
import cn.hwh.sports.activity.sporting.RunningIndoorActivityV2;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.EffortPointEE;
import cn.hwh.sports.entity.event.MaxHrEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.utils.CalorieU;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.FileU;
import cn.hwh.sports.utils.HrZoneU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TTsU;

/**
 * Created by Raul.Fan on 2016/11/27.
 * 室内健身服务
 */

public class FreeEffortService extends Service {


    private static final String TAG = "FreeEffortService";

    /* 通知相关 */
    private static final Class<?>[] mSetForegroundSignature = new Class[]{
            boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{
            int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{
            boolean.class};
    private static final int NOTIFICATION_ID = 1;
    private static boolean mReflectFlg = false;

    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    private NotificationManager mNM;

    // 训练命令
    public static final int CMD_CONTINUE = 2;// 继续命令
    public static final int CMD_FINISH = 3;// 结束命令

    private static final int HR_STATE_LOW = 1;//太低了
    private static final int HR_STATE_NORMAL = 2;//正常
    private static final int HR_STATE_HIGH = 3;//偏高

    private static final int alarmWakeUpInterval = 10 * 60 * 1000;//唤醒时间间隔
    private static final int alarmCheckDaemonInterval = 15 * 1000;//检查Daemon进程存活时间间隔
    private static final int alarmCheckBleInterval = 60 * 1000;//检查Daemon进程存活时间间隔

    /* 用户信息 */
    private UserDE mUserDE;//用户信息
    private UserDE mOwnerDE;//所有者用户信息

    /* 运动信息 */
    private WorkoutDE mWorkoutDE;//运动主信息
    private LapDE mLapDE;//lap信息
    private TimeSplitDE mTimeSplitDE;//时间分段运动信息

    private int mTargetLow;
    private int mTargetHigh;

    private long mDuration;
    private long mLapDuration;
    private long mSplitDuration;

    private int mCurrHr;//当前心率
    private int mCurrCadence;//当前步频
    private int mCurrHrState = HR_STATE_NORMAL;//当前心率状态
    private int mHrStateCount = 0;
    private long mLastAlertTime = 0;

    private int mLastCount = -1;

    //计算距离
    private long mLastSpeedTimeOffSet;

    //背景播放器
    private MediaPlayer mPlaybackPlayer;

    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long startTime;
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

    //接收器
    private EffortReceiver mLockReceiver;

    private boolean isRunning = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler localHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //继续命令
                case CMD_CONTINUE:
                    Log.v(TAG, "CMD_CONTINUE");
                    startTime = System.currentTimeMillis();
                    mTimerRa.run();
                    break;
                //结束命令
                case CMD_FINISH:
                    TTsU.playFinishRun(FreeEffortService.this);
                    isRunning = false;
                    Log.v(TAG, "CMD_FINISH");
                    writeEffortLog("EFFORT SERVICE FINISH\n");
                    mTimerHandler.removeCallbacks(mTimerRa);
                    mLightHandler.removeCallbacks(mLightRa);
                    mDaemonHandler.removeCallbacks(mDaemonRa);
                    mCheckBleHandler.removeCallbacks(mCheckBleRa);
                    unregisterReceiver(mLockReceiver);
                    finishWorkout();
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
                    mWorkoutDE.startTime, mWorkoutDE.startTime, (int) (mDuration / 1000),
                    (int) (mLapDuration / 1000), event.hr, mTimeSplitDE.timeSplitId, 0, event.cadence);
            HrDBData.save(hrDE);
            if (mWorkoutDE.maxHr < event.hr) {
                mWorkoutDE.maxHr = event.hr;
                EventBus.getDefault().post(new MaxHrEE(event.hr));
            }
            if (mWorkoutDE.minHr > event.hr) {
                mWorkoutDE.minHr = event.hr;
            }

            if (mCurrHr > (mUserDE.alertHr - 5)
                    && SportSpData.getAlertTtsEnable(FreeEffortService.this)
                    && (System.currentTimeMillis() - mLastAlertTime) > 60000) {
                mLastAlertTime = System.currentTimeMillis();
                //没超过报警心率
                if (mCurrHr < mUserDE.alertHr) {
                    TTsU.playCloseToWarningHr(FreeEffortService.this);
                } else {
                    TTsU.playOutWarningHr(FreeEffortService.this);
                }
            }
            //计算距离
            mWorkoutDE.length += (mDuration - mLastSpeedTimeOffSet) * event.speed / 10000;
            mLastSpeedTimeOffSet = mDuration;

            if (mWorkoutDE.startStep == 0) {
                mWorkoutDE.startStep = event.stepCount;
            }

            //防止手表步数清0
            if (event.stepCount > mWorkoutDE.startStep) {
                mWorkoutDE.endStep += (event.stepCount - mWorkoutDE.startStep);
            }
            mWorkoutDE.startStep = event.stepCount;
//            Log.v(TAG,"mWorkoutDE.length:" + mWorkoutDE.length);
            WorkoutDBData.update(mWorkoutDE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSportParams();
        initAlarmData();
        bindNotification();
        mLightRa.run();
        mDaemonRa.run();
        mCheckBleRa.run();
        initBle();
        EventBus.getDefault().register(this);
        initRunningReceiver();
        writeEffortLog("EFFORT SERVICE START\n");
        playMute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.v(TAG,"onStartCommand");
        int cmd = CMD_CONTINUE;
        if (intent != null && intent.hasExtra("CMD")) {
            cmd = intent.getIntExtra("CMD", CMD_CONTINUE);
//            Log.v(TAG,"CMD:" + cmd);
        }
        if (cmd == CMD_FINISH && intent.hasExtra("length")) {
            mWorkoutDE.length = intent.getFloatExtra("length", mWorkoutDE.length);
//            Log.v(TAG, "mWorkoutDE.length:" + mWorkoutDE.length);
        }
        localHandler.sendEmptyMessage(cmd);
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

    /**
     * 初始化运动数据
     */
    private void initSportParams() {
        mUserDE = LocalApplication.getInstance().getLoginUser(FreeEffortService.this);
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mLapDE = LapDBData.getUnFinishLap(mWorkoutDE.startTime, mUserDE.userId);
        mOwnerDE = UserDBData.getUserById(mWorkoutDE.ownerId);

        mTimeSplitDE = TimeSplitDBData.getUnFinishTimeSplit(mWorkoutDE.startTime, mUserDE.userId);
        if (mTimeSplitDE == null) {
            mTimeSplitDE = TimeSplitDBData.createNewTimeSplit(mWorkoutDE.startTime,
                    (int) (mWorkoutDE.duration / 60), mUserDE.userId, mOwnerDE.userId);
        }
        mDuration = mWorkoutDE.duration * 1000;
        mLapDuration = mLapDE.duration * 1000;
        mSplitDuration = mDuration % 60;
        mLastSpeedTimeOffSet = mDuration;
//        if (mWorkoutDE.targetType == SportEnum.TargetType.FAT) {
//            mTargetHigh = (int) (mUserDE.maxHr * 0.75);
//            mTargetLow = (int) (mUserDE.maxHr * 0.55);
//        } else if (mWorkoutDE.targetType == SportEnum.TargetType.STRONGER) {
//            mTargetHigh = (int) (mUserDE.maxHr * 0.8);
//            mTargetLow = (int) (mUserDE.maxHr * 0.6);
//        } else {
        mTargetHigh = mUserDE.targetHrHigh;
        mTargetLow = mUserDE.targetHrLow;
//        }
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
        mTimerHandler.removeCallbacks(mTimerRa);

        //定时点亮屏幕
        mLightHandler = new Handler();
        mLightRa = new Runnable() {
            @Override
            public void run() {
                brightKeyguard();
            }
        };
        mLightHandler.removeCallbacks(mLightRa);

        //定时检查守护服务进程
        mDaemonHandler = new Handler();
        mDaemonRa = new Runnable() {
            @Override
            public void run() {
                checkDaemon();
            }
        };
        mDaemonHandler.removeCallbacks(mDaemonRa);

        //定时检查蓝牙
        mCheckBleHandler = new Handler();
        mCheckBleRa = new Runnable() {
            @Override
            public void run() {
                initBle();
                long now = SystemClock.uptimeMillis();
                if (NextTimeCheckBle == now + (alarmCheckBleInterval - now % alarmCheckBleInterval)) {
                    return;
                }
                long NextTimeCheckBle = now + (alarmCheckBleInterval - now % alarmCheckBleInterval);
                mCheckBleHandler.postAtTime(mCheckBleRa, NextTimeCheckBle);
            }
        };
        mCheckBleHandler.removeCallbacks(mCheckBleRa);
    }

    /**
     * 初始化蓝牙服务
     */
    private void initBle() {
        //初始化蓝牙
        BleManager.getBleManager().addNewConnect(mOwnerDE.bleMac);
        if (BleManager.getBleManager().mBleConnectE != null
                && BleManager.getBleManager().mBleConnectE.mIsConnected) {
            BleManager.getBleManager().mBleConnectE.controlLight(true);
        }
    }

    /**
     * 初始化跑步接收器
     */
    private void initRunningReceiver() {
        mLockReceiver = new EffortReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mLockReceiver, filter);
    }

    /**
     * 结束一个workout
     */
    private void finishWorkout() {
        TimeSplitDBData.delete(mTimeSplitDE);
//        mTimeSplitDE.status = SportEnum.SportStatus.FINISH;
//        mTimeSplitDE.avgHr = HrDBData.getAvgHrInSplit(mTimeSplitDE);
//        TimeSplitDBData.update(mTimeSplitDE);
//        UploadDBData.saveHrInfoInTimeSplit(mLapDE, mTimeSplitDE);
        //结束lap
        mLapDE.status = SportEnum.SportStatus.FINISH;
        LapDBData.update(mLapDE);
        UploadDBData.createLapEndData(mLapDE);
        //计算卡路里
        float cal = CalorieU.getMinutesCalorie(mOwnerDE, mTimeSplitDE.avgHr) * mTimeSplitDE.duration / 60;
        if (cal > 0) {
            mWorkoutDE.calorie += cal;
        }
        mWorkoutDE.status = SportEnum.SportStatus.FINISH;
        mWorkoutDE.avgHr = HrDBData.getAvgHrInWorkout(mWorkoutDE);
        if (mWorkoutDE.minHr == 999) {
            mWorkoutDE.minHr = 0;
        }
        WorkoutDBData.update(mWorkoutDE);
        UploadDBData.finishWorkout(mWorkoutDE);
        if (mWorkoutDE.duration < 60) {
            WorkoutDBData.delete(mWorkoutDE);
        } else {
            mUserDE.finishedWorkout++;
            UserDBData.update(mUserDE);
            LocalApplication.getInstance().mLoginUser = mUserDE;
        }
        stopSelf();
    }

    /**
     * 服务邦定通知服务
     */
    private void bindNotification() {
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mStartForeground = FreeEffortService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = FreeEffortService.class.getMethod("stopForeground", mStopForegroundSignature);
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
        PendingIntent contentIntent;
        if (mWorkoutDE.type == SportEnum.EffortType.RUNNING_INDOOR) {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, RunningIndoorActivityV2.class), 0);
        } else {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, SportingIndoorActivity.class), 0);
        }

        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("FIZZO");
        builder.setContentText("正在运动..");
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

    /**
     * 广播接收器
     */
    class EffortReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.v(TAG, "RunningLockReceiver onReceive");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                launchLockActivity(context);
            }
        }
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
                    FreeEffortLockActivity.class);
            alarmLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmLockIntent.putExtra("duration", mDuration / 1000);
            alarmLockIntent.putExtra("point", mWorkoutDE.effortPoint);
            context.startActivity(alarmLockIntent);
        }
    }

    /**
     * 更新时间
     */
    private void updateTimer() {

        long disparityTime = System.currentTimeMillis() - startTime;// 时间
        mDuration += disparityTime;
        mSplitDuration += disparityTime;
        mLapDuration += disparityTime;

        long now = SystemClock.uptimeMillis();
        if (NextTime == now + (1000 - now % 1000)) {
            return;
        }
        NextTime = now + (1000 - now % 1000);
        startTime = System.currentTimeMillis();
        mTimerHandler.postAtTime(mTimerRa, NextTime);

        x.task().post(new Runnable() {
            @Override
            public void run() {
                saveTimerData();
            }
        });
    }

    /**
     * 电量屏幕
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
        if (NextTimeLight == now + (alarmWakeUpInterval - now % alarmWakeUpInterval)) {
            return;
        }
        long NextTimeLight = now + (alarmWakeUpInterval - now % alarmWakeUpInterval);
        mLightHandler.postAtTime(mLightRa, NextTimeLight);
    }

    /**
     * 检查守护进程
     */
    private void checkDaemon() {
        if (!DeviceU.isWorked(FreeEffortService.this, "cn.hwh.sports.service.ListenerService")) {
            //启动监听服务
            Intent listenerIntent = new Intent(FreeEffortService.this, ListenerService.class);
            startService(listenerIntent);
        }
        long now = SystemClock.uptimeMillis();
        if (NextTimeDaemon == now + (alarmCheckDaemonInterval - now % alarmCheckDaemonInterval)) {
            return;
        }
        long NextTimeDaemon = now + (alarmCheckDaemonInterval - now % alarmCheckDaemonInterval);

        mDaemonHandler.postAtTime(mDaemonRa, NextTimeDaemon);
    }


    /**
     * 保存这一秒发生的事情
     */
    private void saveTimerData() {
        if (!isRunning) {
            return;
        }
        // 这里发送到activity
//        if (EventBus.getDefault().hasSubscriberForEvent(RunningTimeEE.class)){
        EventBus.getDefault().post(new RunningTimeEE(mDuration / 1000, mWorkoutDE.length, mWorkoutDE.endStep));
//        }
        mWorkoutDE.duration = mDuration / 1000;
        mTimeSplitDE.duration = mSplitDuration / 1000;
        mLapDE.duration = mLapDuration / 1000;

        TimeSplitDBData.update(mTimeSplitDE);

        //若分段时间超过1分钟
        if (mSplitDuration > 60 * 1000) {
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

            mSplitDuration = mSplitDuration - 60000;
            UploadDBData.saveHrInfoInTimeSplit(mLapDE, mTimeSplitDE);

            String voice = "";

            if (BleManager.getBleManager().mBleConnectE == null
                    || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
                TTsU.playOutWarningHr(FreeEffortService.this);
//                voice += "请检查蓝牙设备是否断开";
//                showVoice(voice);
            } else {
                //若是室内跑步
                if ((mTimeSplitDE.timeIndex + 1) % SportSpData.getCurrHrFreqTts(FreeEffortService.this) == 0
                        && mCurrHr != 0) {
//                    if (mWorkoutDE.type == SportEnum.EffortType.RUNNING_INDOOR) {
//                        voice += ",当前心率 " + mCurrHr;
//                        if (mCurrCadence != 0) {
//                            voice += ",当前步频 " + mCurrCadence;
//                        }
//                    } else {
//                        voice += ",当前心率 " + mCurrHr;
//                    }
//                    voice += "在" + HrZoneU.getDescribeByHrPercent(mCurrHr * 100 / mUserDE.maxHr) + "区";
//                    showVoice(voice);
                    int zone = HrZoneU.getZoneByHrPercent(mCurrHr * 100 / mUserDE.maxHr);
                    TTsU.playMinuteIndoorVoice(FreeEffortService.this, mWorkoutDE.type, mCurrHr, mCurrCadence, zone);
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
        WorkoutDBData.update(mWorkoutDE);

//        if (mCurrHr != 0) {
            RequestParams params = RequestParamsBuilder.buildUploadRecentHr(FreeEffortService.this, UrlConfig.URL_UPLOAD_RECENT_HR, mUserDE.userId, mCurrHr);
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
     * 写入跑步日志
     */
    private void writeEffortLog(final String Title) {
        x.task().post(new Runnable() {
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
                    //内存
                    String memStr = "系统内存:" + DeviceU.getTotalMemory() + "\n";
                    //电量
                    Intent batteryInfoIntent = DeviceU.getBatterPower(FreeEffortService.this);
                    String powerStatus = "电量: " + batteryInfoIntent.getIntExtra("level", 0) + "\n";

                    String logInfo = Title + memStr + powerStatus;
                    FileU.writeLog(logInfo, sdcardPath + "/");
                }
            }
        });
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
     * 获取心率状态
     *
     * @return
     */
    private int getHrState() {
        //锻炼阶段
        if ((mWorkoutDE.targetTime - 5) * 60 > mWorkoutDE.duration) {
            if (mCurrHr > mTargetHigh) {
                return HR_STATE_HIGH;
            } else if (mCurrHr < mTargetLow) {
                return HR_STATE_LOW;
            } else {
                return HR_STATE_NORMAL;
            }
        }
        //热身或冷身阶段
        if (mCurrHr > mTargetLow) {
            return HR_STATE_HIGH;
        } else {
            return HR_STATE_NORMAL;
        }
    }
}
