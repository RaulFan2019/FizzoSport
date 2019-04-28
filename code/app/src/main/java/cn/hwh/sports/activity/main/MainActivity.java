package cn.hwh.sports.activity.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.sporting.RunningIndoorActivityV2;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.activity.sporting.SportingRunningOutdoorActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.main.MainAddDataFragment;
import cn.hwh.sports.fragment.main.MainCoachFragment;
import cn.hwh.sports.fragment.main.MainHealthFragment;
import cn.hwh.sports.fragment.main.MainUserFragment;
import cn.hwh.sports.service.UploadWatcherService;
import cn.hwh.sports.ui.bannertoast.TipViewController;


/**
 * Created by Raul.Fan on 2016/11/11.
 */

public class MainActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "MainActivity";

    private static final int REQUEST_BLE_PERMISSION = 1;//蓝牙打开请求

    public static final int TAB_HEALTH = 0x01;
    public static final int TAB_USER = 0x02;
    public static final int TAB_COACH = 0x03;

    /* view */
    @BindView(R.id.iv_health)
    ImageView mTabHealthIv;
    @BindView(R.id.tv_health)
    TextView mTabHealthTv;
    @BindView(R.id.iv_user)
    ImageView mTabUserIv;
    @BindView(R.id.tv_user)
    TextView mTabUserTv;
    @BindView(R.id.iv_add_data)
    ImageView mAddDataIv;

    /* fragment */
    private MainHealthFragment mHealthFragment;
    private MainUserFragment mUserFragment;
    private MainAddDataFragment mAddDataFragment;
    private MainCoachFragment mCoachFragment;

    /* local status data */
    private boolean mShowAddData = false;

    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.ll_health, R.id.iv_add_data, R.id.ll_user})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_health:
                if (UserSPData.getUserPage(MainActivity.this) == AppEnum.UserRoles.MANAGE) {
                    setTabSelection(TAB_COACH);
                } else if (UserSPData.getUserPage(MainActivity.this) == AppEnum.UserRoles.COACH) {
                    setTabSelection(TAB_COACH);
                } else {
                    setTabSelection(TAB_HEALTH);
                }
                break;
            case R.id.iv_add_data:
                onAddDataClick();
                break;
            case R.id.ll_user:
                setTabSelection(TAB_USER);
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {
        //判断初始是否已经记录上次页面
        if (UserSPData.getUserPage(this) == AppEnum.UserRoles.NO_ROLE) {
            if (UserSPData.getUserRole(MainActivity.this) == AppEnum.UserRoles.MANAGE) {
                setTabSelection(TAB_COACH);
            } else if (UserSPData.getUserRole(MainActivity.this) == AppEnum.UserRoles.COACH) {
                setTabSelection(TAB_COACH);
            } else {
                setTabSelection(TAB_HEALTH);
            }

        } else {
            if (UserSPData.getUserPage(MainActivity.this) == AppEnum.UserRoles.MANAGE) {
                setTabSelection(TAB_COACH);
            } else if (UserSPData.getUserPage(MainActivity.this) == AppEnum.UserRoles.COACH) {
                setTabSelection(TAB_COACH);
            } else {
                setTabSelection(TAB_HEALTH);
            }
        }

        initBLE();
        //启动上传服务
        Intent uploadServiceIntent = new Intent(MainActivity.this, UploadWatcherService.class);
        startService(uploadServiceIntent);
        WorkoutDE workout = WorkoutDBData.getUnFinishWorkout(LocalApplication.getInstance().getLoginUser(getApplicationContext()).userId);

        if (workout != null) {
            if (workout.type == SportEnum.EffortType.RUNNING_OUTDOOR) {
                Intent runI = new Intent(MainActivity.this, SportingRunningOutdoorActivity.class);
                startActivity(runI);
            }else if (workout.type == SportEnum.EffortType.RUNNING_INDOOR){
                Intent runIndoorI = new Intent(MainActivity.this, RunningIndoorActivityV2.class);
                startActivity(runIndoorI);
            }else {
                Intent indoorI = new Intent(MainActivity.this, SportingIndoorActivity.class);
                startActivity(indoorI);
            }
        }
//        if (LocalApplication.getInstance().needShowLowPowerWindow) {
//            WindowUtils.showPopupWindow(MainActivity.this);
//        }
//        showLowPowerNotification();
//        showBannerToast();

//        LocalApplication.getInstance().showFirmWareUpdateDialog(MainActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.v(TAG,"onActivityResult");
        if (requestCode == REQUEST_BLE_PERMISSION ){
            //DO NOTHING
//            if (resultCode != RESULT_OK){
////                requestBluetooth();
//            }else {
////                connectToDevice();
//            }
        }
    }

    @Override
    protected void causeGC() {

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
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
                mTabHealthTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
                mTabHealthTv.setText(R.string.tab_health);
                mTabHealthIv.setImageResource(R.drawable.ic_tab_main_health_select);
                if (mHealthFragment == null) {
                    mHealthFragment = MainHealthFragment.newInstance();
                    transaction.add(R.id.ll_fragment_root, mHealthFragment);
                } else {
                    transaction.show(mHealthFragment);
                }
                if (mShowAddData) {
                    Animation lCloseAnim = AnimationUtils.loadAnimation(this, R.anim.anim_add_data_close);
                    lCloseAnim.setFillAfter(true);
                    mAddDataIv.startAnimation(lCloseAnim);
                    transaction.hide(mAddDataFragment);
                    mShowAddData = !mShowAddData;
                }
                break;
            //切换到运动
            case TAB_USER:
                mTabUserTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
                mTabUserIv.setImageResource(R.drawable.ic_tab_main_user_select);
                if (mUserFragment == null) {
                    mUserFragment = MainUserFragment.newInstance();
                    transaction.add(R.id.ll_fragment_root, mUserFragment);
                } else {
                    transaction.show(mUserFragment);
                }
                if (mShowAddData) {
                    Animation lCloseAnim = AnimationUtils.loadAnimation(this, R.anim.anim_add_data_close);
                    lCloseAnim.setFillAfter(true);
                    mAddDataIv.startAnimation(lCloseAnim);
                    transaction.hide(mAddDataFragment);
                    mShowAddData = !mShowAddData;
                }
                break;
            case TAB_COACH:
                mTabHealthTv.setTextColor(getResources().getColor(R.color.tv_tab_select));
                mTabHealthTv.setText(R.string.tab_coach_main);
                mTabHealthIv.setImageResource(R.drawable.ic_tab_main_health_select);
                if (mCoachFragment == null) {
                    mCoachFragment = MainCoachFragment.newInstance();
                    transaction.add(R.id.ll_fragment_root, mCoachFragment);
                } else {
                    transaction.show(mCoachFragment);
                }
                if (mShowAddData) {
                    Animation lCloseAnim = AnimationUtils.loadAnimation(this, R.anim.anim_add_data_close);
                    lCloseAnim.setFillAfter(true);
                    mAddDataIv.startAnimation(lCloseAnim);
                    transaction.hide(mAddDataFragment);
                    mShowAddData = !mShowAddData;
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 清除掉所有的选中状态。
     */
    private void resetBtn() {
        mTabHealthTv.setTextColor(getResources().getColor(R.color.tv_tab_normal));
        mTabHealthIv.setImageResource(R.drawable.ic_tab_main_health_nomal);
        mTabUserTv.setTextColor(getResources().getColor(R.color.tv_tab_normal));
        mTabUserIv.setImageResource(R.drawable.ic_tab_main_user_normal);
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
        if (mCoachFragment != null) {
            transaction.hide(mCoachFragment);
        }
    }


    /**
     * 点击添加数据按钮
     */
    public void onAddDataClick() {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mShowAddData) {
            Animation lCloseAnim = AnimationUtils.loadAnimation(this, R.anim.anim_add_data_close);
            lCloseAnim.setFillAfter(true);
//            LinearInterpolator lin = new LinearInterpolator();
//            lCloseAnim.setInterpolator(lin);
            mAddDataIv.startAnimation(lCloseAnim);
            transaction.hide(mAddDataFragment);
        } else {
            if (mAddDataFragment == null) {
                mAddDataFragment = MainAddDataFragment.newInstance();
                transaction.add(R.id.ll_fragment_data, mAddDataFragment);
            } else {
                transaction.show(mAddDataFragment);
            }
            Animation lOpenAnim = AnimationUtils.loadAnimation(this, R.anim.anim_add_data_open);
            lOpenAnim.setFillAfter(true);
//            LinearInterpolator lin = new LinearInterpolator();
//            lOpenAnim.setInterpolator(lin);
            mAddDataIv.startAnimation(lOpenAnim);
        }
        transaction.commitAllowingStateLoss();
        mShowAddData = !mShowAddData;
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
    private void requestBluetooth(){
        // 蓝牙未打开 请求蓝牙打开权限
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_BLE_PERMISSION);
    }


    /**
     * 尝试连接设备
     */
    private void connectToDevice(){
        //若已连接过设备
        String mDeviceMac = LocalApplication.getInstance().getLoginUser(getApplicationContext()).bleMac;
        if (!mDeviceMac.equals("")) {
            if (BleManager.getBleManager().mBleConnectE != null
                    && BleManager.getBleManager().mBleConnectE.mIsConnected){
                return;
            }
            BleManager.getBleManager().replaceConnect(mDeviceMac);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BleManager.getBleManager().cancelDiscovery();
                }
            }, 10 *1000);
        }
    }

    /**
     * 显示低电量的通知
     */
    private void showLowPowerNotification() {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("FIZZO COR ")//设置通知栏标题
                .setContentText("手环电量只剩 20%, 请及时充电") //
                .setTicker("") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果
                .setSmallIcon(R.drawable.ic_battery_low)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        mNotificationManager.notify(1000,mBuilder.build());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLowPowerNotification();
                showBannerToast();
            }
        },10000);
    }


    private void showBannerToast(){
        TipViewController mTipViewController;
        mTipViewController = new TipViewController(getApplication(), "");
        mTipViewController.show();
    }
}
