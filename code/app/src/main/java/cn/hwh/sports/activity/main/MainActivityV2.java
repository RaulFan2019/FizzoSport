package cn.hwh.sports.activity.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.sporting.RunningIndoorActivityV2;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.activity.sporting.SportingRunningOutdoorActivity;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.AppSPData;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.net.UpdateRE;
import cn.hwh.sports.fragment.main.MainHealthFragmentV2;
import cn.hwh.sports.fragment.main.MainSleepFragment;
import cn.hwh.sports.fragment.main.MainSportFragment;
import cn.hwh.sports.fragment.main.MainUserFragment;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.service.UploadWatcherService;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.StringU;

/**
 * Created by Raul.Fan on 2017/4/17.
 */

public class MainActivityV2 extends BaseActivity {

    /* contains */
    private static final String TAG = "MainActivity";

    private static final int REQUEST_BLE_PERMISSION = 1;//蓝牙打开请求

    public static final int TAB_HEALTH = 0x01;
    public static final int TAB_SLEEP = 0x02;
    public static final int TAB_SPORT = 0x03;
    public static final int TAB_USER = 0x04;


    private static final int MSG_UPDATE_PROGRESS = 0x06;
    private static final int MSG_DOWNLOAD_APK_OK = 0x07;
    private static final int MSG_DOWNLOAD_ERROR = 0x08;

    /* local view */
    @BindView(R.id.iv_health)
    ImageView mHealthIv;
    @BindView(R.id.tv_health)
    TextView mHealthTv;
//    @BindView(R.id.iv_sleep)
//    ImageView mSleepIv;
//    @BindView(R.id.tv_sleep)
//    TextView mSleepTv;
    @BindView(R.id.iv_sport)
    ImageView mSportIv;
    @BindView(R.id.tv_sport)
    TextView mSportTv;
    @BindView(R.id.iv_user)
    ImageView mUserIv;
    @BindView(R.id.tv_user)
    TextView mUserTv;

    /* fragment */
    private MainHealthFragmentV2 mHealthFragment;
    private MainUserFragment mUserFragment;
    private MainSleepFragment mSleepFragment;
    private MainSportFragment mSportFragment;


    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器

    private DialogBuilder mDialogBuilder;


    /* local data */
    private long exitTime = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_v2;
    }

    @OnClick({R.id.ll_health, R.id.ll_sport, R.id.ll_user})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_health:
                setTabSelection(TAB_HEALTH);
                break;
//            case R.id.ll_sleep:
//                setTabSelection(TAB_SLEEP);
//                break;
            case R.id.ll_sport:
                setTabSelection(TAB_SPORT);
                break;
            case R.id.ll_user:
                setTabSelection(TAB_USER);
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取版本信息失败
                case MSG_DOWNLOAD_ERROR:
                    Toasty.error(MainActivityV2.this, (String) msg.obj).show();
                    System.exit(0);
                    break;
                case MSG_UPDATE_PROGRESS:
                    mDialogBuilder.setDownLoadProgress(msg.arg1);
                    break;
                case MSG_DOWNLOAD_APK_OK:
                    DeviceU.installAPK(MainActivityV2.this, (File) msg.obj);
                    System.exit(0);
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {
        setTabSelection(TAB_HEALTH);
        //启动上传服务
        Intent uploadServiceIntent = new Intent(MainActivityV2.this, UploadWatcherService.class);
        startService(uploadServiceIntent);

        //检查APP是否要升级版本
        if (AppSPData.getAppUpdateNeedTip(MainActivityV2.this)
                && !AppSPData.getAppUpdateInfo(MainActivityV2.this).equals("")) {
            UpdateRE updateEntity = JSON.parseObject(AppSPData.getAppUpdateInfo(MainActivityV2.this), UpdateRE.class);
            if (updateEntity.versionCode > MyBuildConfig.VersionCode) {
                //是否必须升级
                if (StringU.mustUpdateForSupportWatch(MyBuildConfig.SUPPORT_WATCH_VERSION, updateEntity.firmware_protocol_version)) {
                    showMustUpdateAppDialog(updateEntity.url);
                } else {
                    showUpdateAppDialog(updateEntity.url);
                }
                return;
            }
        }
        doNormalCreate();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    public void setTabSelection(int index) {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            //切换到分析
            case TAB_HEALTH:
                mHealthTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
                mHealthIv.setImageResource(R.drawable.ic_main_tab_health_focus);
                if (mHealthFragment == null) {
                    mHealthFragment = MainHealthFragmentV2.newInstance();
                    transaction.add(R.id.ll_fragment_root, mHealthFragment);
                } else {
                    transaction.show(mHealthFragment);
                }
                break;
            //切换到运动
            case TAB_USER:
                mUserTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
                mUserIv.setImageResource(R.drawable.ic_main_tab_me_focus);
                if (mUserFragment == null) {
                    mUserFragment = MainUserFragment.newInstance();
                    transaction.add(R.id.ll_fragment_root, mUserFragment);
                } else {
                    transaction.show(mUserFragment);
                }
                break;
//            case TAB_SLEEP:
//                mSleepTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
//                mSleepIv.setImageResource(R.drawable.ic_main_tab_sleep_focus);
//                if (mSleepFragment == null) {
//                    mSleepFragment = MainSleepFragment.newInstance();
//                    transaction.add(R.id.ll_fragment_root, mSleepFragment);
//                } else {
//                    transaction.show(mSleepFragment);
//                }
//                break;
            case TAB_SPORT:
                mSportTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
                mSportIv.setImageResource(R.drawable.ic_main_tab_sport_focus);
                if (mSportFragment == null) {
                    mSportFragment = MainSportFragment.newInstance();
                    transaction.add(R.id.ll_fragment_root, mSportFragment);
                } else {
                    transaction.show(mSportFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 清除掉所有的选中状态。
     */
    private void resetBtn() {
        mHealthTv.setTextColor(getResources().getColor(R.color.tv_tab_normal));
        mHealthIv.setImageResource(R.drawable.ic_main_tab_health_normal);
        mUserTv.setTextColor(getResources().getColor(R.color.tv_tab_normal));
        mUserIv.setImageResource(R.drawable.ic_main_tab_me_normal);
        mSportTv.setTextColor(getResources().getColor(R.color.tv_tab_normal));
        mSportIv.setImageResource(R.drawable.ic_main_tab_sport_normal);
//        mSleepTv.setTextColor(getResources().getColor(R.color.tv_tab_normal));
//        mSleepIv.setImageResource(R.drawable.ic_main_tab_sleep_normal);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mHealthFragment != null) {
            transaction.hide(mHealthFragment);
        }
        if (mUserFragment != null) {
            transaction.hide(mUserFragment);
        }
        if (mSleepFragment != null) {
            transaction.hide(mSleepFragment);
        }
        if (mSportFragment != null) {
            transaction.hide(mSportFragment);
        }
    }

    /**
     * 初始化蓝牙
     */
    private void initBLE() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

//        Log.v(TAG,"mBluetoothAdapter == null:" + (mBluetoothAdapter == null));
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            return;
        }
        // 若蓝牙未打开
        if (!mBluetoothAdapter.isEnabled()) {
            requestBluetooth();
            return;
        }
        connectToDevice();
    }

    /**
     * 请求打开蓝牙
     */
    private void requestBluetooth() {
        // 蓝牙未打开 请求蓝牙打开权限
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_BLE_PERMISSION);
    }

    /**
     * 尝试连接设备
     */
    private void connectToDevice() {
        //若已连接过设备
        String mDeviceMac = LocalApplication.getInstance().getLoginUser(getApplicationContext()).bleMac;
        if (!mDeviceMac.equals("")) {
            if (BleManager.getBleManager().mBleConnectE != null
                    && BleManager.getBleManager().mBleConnectE.mIsConnected) {
                return;
            }
            BleManager.getBleManager().addNewConnect(mDeviceMac);
            BleManager.getBleManager().replaceConnect(mDeviceMac);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BleManager.getBleManager().cancelDiscovery();
                }
            }, 10 * 1000);
        }
    }


    /**
     * 执行不需要升级行为
     */
    private void doNormalCreate() {
        initBLE();
        WorkoutDE workout = WorkoutDBData.getUnFinishWorkout(LocalApplication.getInstance().getLoginUser(getApplicationContext()).userId);
//        Log.v(TAG,"workout == null：" + (workout == null));
        if (workout != null) {
//            Log.v(TAG,"workout.type:" + workout.type);
            if (workout.type == SportEnum.EffortType.RUNNING_OUTDOOR) {
                Intent runI = new Intent(MainActivityV2.this, SportingRunningOutdoorActivity.class);
                startActivity(runI);
            } else if (workout.type == SportEnum.EffortType.RUNNING_INDOOR) {
                Intent runIndoorI = new Intent(MainActivityV2.this, RunningIndoorActivityV2.class);
                startActivity(runIndoorI);
            }else {
                Intent indoorI = new Intent(MainActivityV2.this, SportingIndoorActivity.class);
                startActivity(indoorI);
            }
        }
    }


    /***
     * 显示必须升级的对话框
     */
    private void showMustUpdateAppDialog(final String url) {
        mDialogBuilder.showAppMustUpdateDialog(MainActivityV2.this);
        mDialogBuilder.setListener(new DialogBuilder.AppMustUpdateListener() {
            @Override
            public void onConfirmBtnClick() {
                downLoadApk(url);
            }
        });
    }


    /**
     * 显示可以选择升级的对话框
     */
    private void showUpdateAppDialog(final String url) {
        mDialogBuilder.showAppNormalUpdateDialog(MainActivityV2.this);
        mDialogBuilder.setListener(new DialogBuilder.AppNormalUpdateListener() {
            @Override
            public void onConfirmBtnClick() {
                downLoadApk(url);
            }

            @Override
            public void onCancelBtnClick() {
                AppSPData.setAppUpdateNeedTip(MainActivityV2.this, false);
                doNormalCreate();
            }
        });
    }


    /**
     * 下载APK
     */
    private void downLoadApk(final String url) {
        mDialogBuilder.showAppDownloadDialog(MainActivityV2.this);
        x.task().post(new Runnable() {
            @Override
            public void run() {
//                Log.v(TAG,"mUpdateRE.url:" + mUpdateRE.url);
                RequestParams params = new RequestParams("http://" + url);
//                params.setSaveFilePath(FileConfig.DOWNLOAD_PATH + File.separator + mUpdateRE.apkName);
                params.setCancelFast(true);
                x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
//                        Log.v(TAG, "onSuccess:" + result.getAbsolutePath());
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_APK_OK;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
//                        Log.v(TAG, "onCancelled");
                    }

                    @Override
                    public void onFinished() {
//                        Log.v(TAG, "onFinished");
                    }

                    @Override
                    public void onWaiting() {
//                        Log.v(TAG, "onWaiting");
                    }

                    @Override
                    public void onStarted() {
//                        Log.v(TAG, "onStarted");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        Message msg = new Message();
                        msg.what = MSG_UPDATE_PROGRESS;
                        msg.arg1 = (int) (current * 100 / total);
                        mHandler.sendMessage(msg);
//                        Log.v(TAG,"onLoading:" + ",total:" + total + ",current:" + current);
                    }
                });
            }
        });
    }

    /**
     * 按2次退出本页面
     */
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 3000) {
            Toast.makeText(getApplicationContext(), "再按一次退出Fizzo", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            moveTaskToBack(true);
        }
    }
}
