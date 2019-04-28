package cn.hwh.sports;

import android.app.ActivityManager;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;
import com.tencent.android.tpush.service.XGPushServiceV3;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.UUID;

import cn.hwh.sports.activity.settings.FizzoFirmwareUpdateDialogActivity;
import cn.hwh.sports.activity.settings.WatchResumeActivity;
import cn.hwh.sports.ble.BleConfig;
import cn.hwh.sports.ble.BleReplaceEE;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.adapter.BleScanAE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.service.DaemonReceiver;
import cn.hwh.sports.service.DaemonService;
import cn.hwh.sports.service.ListenerReceiver;
import cn.hwh.sports.service.ListenerService;
import cn.hwh.sports.utils.CrashHandler;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.UscTTsU;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Raul.Fan on 2016/11/6.
 */

public class LocalApplication extends MultiDexApplication {


    private static final String TAG = "LocalApplication";

    private static final int MSG_BLE_OFF = 0x01;
    private static final int MSG_BLE_ON = 0x02;
    private static final int MSG_BLE_CANCEL_DISCOVER = 0x03;

    public static Context applicationContext;//整个APP的上下文
    private static LocalApplication instance;//Application 对象

    //守护客户端
    private DaemonClient mDaemonClient;

    /* app 运行状态 */
    public boolean isActive = false;//是否在前台运行
    public UserDE mLoginUser;//当前正在登录的用户

    /* local data about db */
    DbManager.DaoConfig daoConfig;
    DbManager db;

    private static UscTTsU uscUtil;// 云之声离线语音工具

    public boolean needShowLowPowerWindow = true;
    public boolean showSyncHistoryDialogActivity = false;
    public boolean needShowUpdateFirmWareDialog = true;

    //是否正在显示手表恢复状态
    public boolean showWatchResumeActivity = false;

    BluetoothAdapter mBluetoothAdapter;


    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //蓝牙断开的消息
            if (msg.what == MSG_BLE_OFF) {
                if (BleManager.getBleManager().mBleConnectE != null) {
                    BleManager.getBleManager().mBleConnectE.disConnect();
                    BleManager.getBleManager().mBleConnectE.mBluetoothGatt.close();
                }
                BleManager.getBleManager().destroy();
                //蓝牙连接的消息
            } else if (msg.what == MSG_BLE_ON) {
                if (BleManager.getBleManager().mBleConnectE != null) {
                    BleManager.getBleManager().mBleConnectE.disConnect();
//                                        BleManager.getBleManager().mBleConnectE = null;
                }
                BleManager.getBleManager().destroy();
                if (getLoginUser(applicationContext) != null && getLoginUser(applicationContext).bleMac != null) {
                    BleManager.getBleManager().replaceConnect(getLoginUser(applicationContext).bleMac);
                    mLocalHandler.sendEmptyMessageDelayed(MSG_BLE_CANCEL_DISCOVER, 10 * 1000);
//                    if (WorkoutDBData.getUnFinishWorkout(getLoginUser(applicationContext).userId) == null){
//                        BleManager.getBleManager().replaceConnect(getLoginUser(applicationContext).bleMac);
//                    }else {
//                        String mac = UserDBData.getUserById(WorkoutDBData.getUnFinishWorkout(getLoginUser(applicationContext).userId).ownerId).bleMac;
//                        if (mac != null || !mac.equals("")){
//                            BleManager.getBleManager().replaceConnect(mac);
//                        }
//                    }
                }
            } else if (msg.what == MSG_BLE_CANCEL_DISCOVER) {
                BleManager.getBleManager().cancelDiscovery();
            }
        }
    };

    @Override
    public void onCreate() {
        createFileSystem();
        super.onCreate();

        applicationContext = getApplicationContext();
        x.Ext.init(this);
        startupExceptionHandler();
        initDB();
        initUmLib();
//        initUmPush();
//        initXGPush();
//        initJpushPush();
//        initMiPush();

        //启动保活服务
//        Intent listenerIntent = new Intent(this, ListenerService.class);
//        startService(listenerIntent);

        //语音初始化
        if (uscUtil == null) {
            uscUtil = new UscTTsU(this);
        }
        LocalApplication.getInstance().applicationContext.registerReceiver(mReceiver, makeFilter());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        mDaemonClient = new DaemonClient(createDaemonConfigurations());
        mDaemonClient.onAttachBaseContext(base);
    }

    /**
     * 获取 LocalApplication
     *
     * @return
     */
    public static LocalApplication getInstance() {
        if (instance == null) {
            instance = new LocalApplication();
        }
        return instance;
    }

    /**
     * 获取正在登录的用户对象
     *
     * @return
     */
    public UserDE getLoginUser(final Context context) {
        if (mLoginUser == null) {
            if (context != null) {
                mLoginUser = UserDBData.getUserById(UserSPData.getUserId(context));
            }
        }
        return mLoginUser;
    }

    /**
     * 获取数据库操作库
     *
     * @return
     */
    public DbManager getDb() {
        if (daoConfig == null) {
            daoConfig = new DbManager.DaoConfig()
                    .setDbName(MyBuildConfig.DB_NAME)
                    .setDbVersion(MyBuildConfig.DB_VERSION)
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    });
        }
        if (db == null) {
            db = x.getDb(daoConfig);
        }
        return db;
    }

    /**
     * 获取云之声语音工具
     *
     * @return
     */
    public UscTTsU getUscTtsUtil(Context context) {
        if (uscUtil == null) {
            uscUtil = new UscTTsU(context);
        }
        return uscUtil;
    }


    /**
     * 捕捉错误日志机制
     */
    private void startupExceptionHandler() {
        if (!MyBuildConfig.DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName(MyBuildConfig.DB_NAME)
                .setDbVersion(MyBuildConfig.DB_VERSION)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
    }

    /**
     * 创建私有文件目录
     */
    private void createFileSystem() {
        File CrashF = new File(FileConfig.CRASH_PATH);
        if (!CrashF.exists()) {
            CrashF.mkdirs();
        }

        File picF = new File(FileConfig.DEFAULT_SAVE_CUT_BITMAP);
        if (!picF.exists()) {
            picF.mkdirs();
        }

        File downloadF = new File(FileConfig.DOWNLOAD_PATH);
        if (!downloadF.exists()) {
            downloadF.mkdirs();
        }

        File syncF = new File(FileConfig.SYNC_PATH);
        if (!syncF.exists()) {
            syncF.mkdirs();
        }

        File voiceF = new File(FileConfig.RECORD_PATH);
        if (!voiceF.exists()) {
            voiceF.mkdirs();
        }

    }

    /**
     * 初始化友盟库
     */
    private void initUmLib() {
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wxd20a35f62e6da832", "429d3c28f5b6cda054bfd92bb3c10690");
    }

    /**
     * 友盟推送初始化
     */
    private void initUmPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("initUmPush", "onSuccess deviceToken:" + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("initUmPush", "onFailure:" + s);
            }
        });
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        T.showShort(context, msg.custom);
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                return super.getNotification(context, msg);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
    }


    /**
     * J push初始化
     */
    private void initJpushPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    /**
     * XG push初始化
     */
    private void initXGPush() {
        XGPushManager.registerPush(getApplicationContext());
        XGPushConfig.enableDebug(this, false);
        XGPushConfig.setAccessId(getApplicationContext(), 2100253210);
        XGPushConfig.setAccessKey(getApplicationContext(), "A6IY185TIN8G");

        Intent service = new Intent(this, XGPushServiceV3.class);
        startService(service);
    }


    /**
     * 初始化小米推送
     */
    private void initMiPush() {
        //初始化push推送服务
        if (isMainProcess()) {
            MiPushClient.registerPush(this, "2882303761517522784", "5621752248784");
        }
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d("initMiPush", content);
            }

            @Override
            public void log(String content) {
                Log.d("initMiPush", content);
            }
        };
        Logger.setLogger(this, newLogger);
    }


    /**
     * 守护进程配置
     *
     * @return
     */
    private DaemonConfigurations createDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "cn.hwh.sports:process",
                ListenerService.class.getCanonicalName(),
                ListenerReceiver.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "cn.hwh.sports:daemon",
                DaemonService.class.getCanonicalName(),
                DaemonReceiver.class.getCanonicalName());
        return new DaemonConfigurations(configuration1, configuration2, null);
    }


    private static IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private static final String TAG = "BleConnectEntity";

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e(TAG, "onReceive---------");
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e(TAG, "onReceive---------STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.e(TAG, "onReceive---------STATE_ON");
                            mLocalHandler.sendEmptyMessageDelayed(MSG_BLE_ON, 3000);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e(TAG, "onReceive---------STATE_TURNING_OFF");
                            mLocalHandler.sendEmptyMessage(MSG_BLE_OFF);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.e(TAG, "onReceive---------STATE_OFF");
                            break;
                    }
                    break;
            }
        }
    };

    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示需要升级FirmWare界面
     */
    public void showFirmWareUpdateDialog(Context context, String ftp) {
        if (!needShowUpdateFirmWareDialog) {
            return;
        }
        if (mLoginUser == null) {
            return;
        }
        if (WorkoutDBData.getUnFinishWorkout(mLoginUser.userId) != null) {
            return;
        }
        needShowUpdateFirmWareDialog = false;

        Intent i = new Intent(context, FizzoFirmwareUpdateDialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ftp", ftp);
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
     * 显示手表恢复状态界面
     *
     * @param context
     */
    public void showWatchResumeActivity(Context context) {
        Log.v(TAG, "showWatchResumeActivity");
        if (showWatchResumeActivity) {
            return;
        }
        if (mLoginUser == null) {
            return;
        }
        showWatchResumeActivity = true;
        Intent i = new Intent(context, WatchResumeActivity.class);
        Bundle bundle = new Bundle();
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}
