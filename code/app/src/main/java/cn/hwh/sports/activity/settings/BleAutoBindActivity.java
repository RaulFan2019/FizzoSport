package cn.hwh.sports.activity.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.ble.BleConfig;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.entity.adapter.BleScanAE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2017/1/12.
 */

public class BleAutoBindActivity extends BaseActivity {


    private static final String TAG = "BleAutoBindActivity";

    private static final int STEP_ADORN = 0;//佩戴步骤
    private static final int STEP_OPEN_DEVICE = 1;//打开手表
    private static final int STEP_SCANNING = 2;//扫描阶段
    private static final int STEP_SCAN_NONE = 3;//没有扫描到设备阶段
    private static final int STEP_WAIT_SHOCK = 4;//等待震动


    private static final int INTERVAL_SCAN = 5 * 1000;
    private static final int INTERVAL_SHOCK = 10 * 1000;
    private static final int LIMIT_RSSI = -65;

    private static final int MSG_STOP_SCAN = 0x01;// 停止扫描
    private static final int MSG_SHOCK_TIMEOUT = 0x02;//等待敲击超时

    private static final int MSG_BIND_ERROR = 0x04;//绑定异常
    private static final int MSG_BIND_OK = 0x05;//绑定通过

    /* local view */
    @BindView(R.id.tv_step_title)
    TextView mStepTitleTv;//步骤标题
    @BindView(R.id.tv_step_tip)
    TextView mStepTipTv;//步骤提示
    @BindView(R.id.v_adorn)
    View mAdornV;//佩戴图示
    @BindView(R.id.v_open_device)
    View mOpenDeviceV;//打开设备图示

    @BindView(R.id.fl_scan)
    FrameLayout mScanFl;//扫描布局
    @BindView(R.id.v_scanning)
    View mScanningV;//扫描图示
    @BindView(R.id.v_scan_none)
    View mScanNoneV;//没有扫描到设备图示

    @BindView(R.id.fl_shock)
    FrameLayout mShockFl;
    @BindView(R.id.v_shock)
    View mShockV;


    @BindView(R.id.btn_active)
    Button mActiveBtn;//顺利的操作
    @BindView(R.id.btn_ask)
    Button mAskBtn;//有疑问的操作


    /* local data */
    private int mStep = STEP_ADORN;

    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    private static final int REQUEST_BLE_PERMISSION = 1;//蓝牙打开请求

    private String mCurrDeviceMac = "";//已连接设备的MAC地址
    private String mCurrDeviceName = "";//设备名称

    private UserDE mUserDE;

    private List<BleScanAE> mScanList = new ArrayList<>();
    private RotateAnimation mRotateAnimation;
    private TranslateAnimation mTranAnim;

    private boolean mIsStartFromScan = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_ble_auto_bind;
    }

    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //停止扫描
                case MSG_STOP_SCAN:
                    stopScan();
                    break;
                //绑定失败
                case MSG_BIND_ERROR:
                    T.showShort(BleAutoBindActivity.this, (String) msg.obj);
                    //断开之前的连接
                    BleManager.getBleManager().mBleConnectE.disConnect();
                    BleManager.getBleManager().mBleConnectE = null;
                    finish();
                    break;
                //绑定成功
                case MSG_BIND_OK:
                    mUserDE.bleMac = mCurrDeviceMac;
                    mUserDE.bleName = mCurrDeviceName;
                    UserDBData.update(mUserDE);
                    BleManager.getBleManager().addNewConnect(mUserDE.bleMac);
                    LocalApplication.getInstance().mLoginUser = mUserDE;
                    T.showShort(BleAutoBindActivity.this, "绑定成功");
                    finish();
                    BleManager.getBleManager().mBleConnectE.readFirmVersion();
                    break;
                case MSG_SHOCK_TIMEOUT:
                    stopWaitShock();
                    break;
            }
        }
    };


    @OnClick({R.id.btn_active, R.id.btn_ask})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击顺利按钮
            case R.id.btn_active:
                //佩戴阶段
                if (mStep == STEP_ADORN) {
                    stepToOpenDevice();
                    //开机阶段
                } else if (mStep == STEP_OPEN_DEVICE) {
                    startScan();
                } else if (mStep == STEP_SCAN_NONE) {
                    startScan();
                }
                break;
            //点击疑问按钮
            case R.id.btn_ask:
                //佩戴阶段
                if (mStep == STEP_ADORN) {
                    finish();
                    //开机阶段
                } else if (mStep == STEP_OPEN_DEVICE) {
                    finish();
                    //扫描阶段
                } else if (mStep == STEP_SCANNING) {
                    cancelBind();
                    //没有扫描到设备
                } else if (mStep == STEP_SCAN_NONE) {
                    cancelBind();
                    //等待敲击
                } else if (mStep == STEP_WAIT_SHOCK) {
                    stopShockToScan();
//                    startWaitShock();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
//        Log.v("AutoBind","onBleEventBus:" + event.msg);
        switch (event.msg) {
            //已连接
            case BleManager.MSG_CONNECTED:
                //DO NOTHING
                break;
            //失去连接
            case BleManager.MSG_DISCONNECT:
                //DO NOTHING
                break;
            //若可以绑定
            case BleManager.MSG_CAN_BIND:
                mRotateAnimation.cancel();
                mScanningV.setVisibility(View.GONE);
                startWaitShock();
                break;
            case BleManager.MSG_CONNECT_FAIL:
                //DO NOTHING
                break;
            //非Fizzo 手环
            case BleManager.MSG_NOT_FIZZO:
                //DO NOTHING
                break;
            case BleManager.MSG_SHOCK_OK:
                mLocalHandler.removeMessages(MSG_SHOCK_TIMEOUT);
                checkBindDevice();
                break;
            //震动错误
            case BleManager.MSG_SHOCK_ERROR:
                mLocalHandler.removeMessages(MSG_SHOCK_TIMEOUT);
                stopWaitShock();
                break;
        }
    }

    /**
     * 扫描新设备
     */
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            for (BleScanAE le : mScanList) {
                if ((le.device.getAddress().equals(device.getAddress()))) {
                    le.rssi = rssi;
                    return;
                }
            }
            Log.v(TAG, "onLeScan:<" + device.getAddress() + ">");
            BleScanAE leBleScan = new BleScanAE(device, BleScanAE.STATE_DISCONNECT, rssi);
            mScanList.add(leBleScan);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //请求打开蓝牙
            case REQUEST_BLE_PERMISSION:
                if (resultCode != RESULT_OK) {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void initData() {
        mUserDE = LocalApplication.getInstance().getLoginUser(BleAutoBindActivity.this);
        mCurrDeviceMac = mUserDE.bleMac;
        mCurrDeviceName = mUserDE.bleName;
        mIsStartFromScan = getIntent().hasExtra("scan");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void doMyCreate() {
        initBLE();
        EventBus.getDefault().register(this);
        Log.v(TAG, "mIsStartFromScan:" + mIsStartFromScan);
        if (mIsStartFromScan) {
            startScan();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 若蓝牙未打开
        if (!mBluetoothAdapter.isEnabled()) {
            // 蓝牙未打开 请求蓝牙打开权限
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLE_PERMISSION);
            return;
        }
    }

    @Override
    protected void causeGC() {
        mScanList.clear();
        if (mLocalHandler != null) {
            mLocalHandler.removeCallbacksAndMessages(null);
        }
        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
        }
        if (mTranAnim != null) {
            mTranAnim.cancel();
        }
        mScanningV.clearAnimation();
        mShockV.clearAnimation();

        EventBus.getDefault().unregister(this);
    }


    /**
     * 初始化蓝牙
     */
    private void initBLE() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            T.showShort(this, "当前手机不支持BLE蓝牙");
            finish();
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            T.showShort(this, "该设备不支持蓝牙");
            finish();
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        Log.v(TAG,"startScan");
        mStep = STEP_SCANNING;
        //UI 变化
        mOpenDeviceV.setVisibility(View.GONE);
        mScanFl.setVisibility(View.VISIBLE);
        mScanNoneV.setVisibility(View.GONE);
        mActiveBtn.setVisibility(View.INVISIBLE);
        mStepTipTv.setText("请确保手环打开并远离其他FIZZO手环");
        mStepTitleTv.setText("手环靠近手机");
        mRotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());//不停顿
        mRotateAnimation.setDuration(3000);//设置动画持续时间
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.start();
        mScanningV.setAnimation(mRotateAnimation);
        //开始扫描
        mBluetoothAdapter.startLeScan(new UUID[]{BleConfig.UUID_FIZZO_PRIVATE_SERVICE}, mLeScanCallback);
        //10秒后停止扫描
        mLocalHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, INTERVAL_SCAN);
    }


    /**
     * 停止扫描
     */
    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        //没有扫描到设备
        if (mScanList.size() == 0) {
            mStep = STEP_SCAN_NONE;
            mRotateAnimation.cancel();
            mScanFl.setVisibility(View.GONE);
            mStepTitleTv.setText("周围没有可绑定的设备");
            mActiveBtn.setVisibility(View.VISIBLE);
            mAskBtn.setVisibility(View.VISIBLE);
            mActiveBtn.setText("重新扫描");
            mAskBtn.setText("暂不绑定");
        } else {
            //找到信号最好的设备, 尝试连接
            BleScanAE bleScanAE = mScanList.get(0);
            for (BleScanAE ae : mScanList) {
//                Log.v("BleScanAE","BleScanAE rssi:" + ae.rssi);
                if (ae.rssi > bleScanAE.rssi) {
                    bleScanAE = ae;
                }
            }
            if (bleScanAE.rssi < LIMIT_RSSI) {
                mStep = STEP_SCAN_NONE;
                mRotateAnimation.cancel();
                mScanFl.setVisibility(View.GONE);
                mStepTitleTv.setText("周围没有可绑定的设备");
                mActiveBtn.setVisibility(View.VISIBLE);
                mAskBtn.setVisibility(View.VISIBLE);
                mActiveBtn.setText("重新扫描");
                mAskBtn.setText("暂不绑定");
            } else {
                mStepTitleTv.setText("正在连接设备");
                mCurrDeviceMac = bleScanAE.device.getAddress();
                mCurrDeviceName = bleScanAE.device.getName();
                BleManager.getBleManager().addNewConnect(mCurrDeviceMac);
            }
        }
    }

    /**
     * 开始等待敲击
     */
    private void startWaitShock() {
        mStep = STEP_WAIT_SHOCK;//没有扫描到设备
        if (mScanList.size() == 0) {
            mStep = STEP_SCAN_NONE;
            mRotateAnimation.cancel();
            mScanFl.setVisibility(View.GONE);
            mStepTitleTv.setText("周围没有可绑定的设备");
            mActiveBtn.setVisibility(View.VISIBLE);
            mAskBtn.setVisibility(View.VISIBLE);
            mActiveBtn.setText("重新扫描");
            mAskBtn.setText("暂不绑定");
        } else {
            //找到信号最好的设备, 尝试连接
            BleScanAE bleScanAE = mScanList.get(0);
            for (BleScanAE ae : mScanList) {
//                Log.v("BleScanAE","BleScanAE rssi:" + ae.rssi);
                if (ae.rssi > bleScanAE.rssi) {
                    bleScanAE = ae;
                }
            }
            if (bleScanAE.rssi < LIMIT_RSSI) {
                mStep = STEP_SCAN_NONE;
                mRotateAnimation.cancel();
                mScanFl.setVisibility(View.GONE);
                mStepTitleTv.setText("周围没有可绑定的设备");
                mActiveBtn.setVisibility(View.VISIBLE);
                mAskBtn.setVisibility(View.VISIBLE);
                mActiveBtn.setText("重新扫描");
                mAskBtn.setText("暂不绑定");
            } else {
                mStepTitleTv.setText("正在连接设备");
                mCurrDeviceMac = bleScanAE.device.getAddress();
                mCurrDeviceName = bleScanAE.device.getName();
                BleManager.getBleManager().addNewConnect(mCurrDeviceMac);
            }
        }
        BleManager.getBleManager().mBleConnectE.shock();
        mShockFl.setVisibility(View.VISIBLE);
        mScanFl.setVisibility(View.GONE);
        mTranAnim = new TranslateAnimation(0, -15f, 0f, 0f);
        mTranAnim.setDuration(500);
        mTranAnim.setRepeatCount(Animation.INFINITE);
        mTranAnim.setRepeatMode(Animation.REVERSE);
        mTranAnim.start();
        mShockV.setAnimation(mTranAnim);
        mActiveBtn.setVisibility(View.INVISIBLE);
        mAskBtn.setVisibility(View.INVISIBLE);
        mAskBtn.setText("手环没有振动");
        mStepTitleTv.setText("振动后短按电源键");
        mStepTipTv.setText("当手环振动后，请短按电源键确认绑定设备");
        mLocalHandler.sendEmptyMessageDelayed(MSG_SHOCK_TIMEOUT, INTERVAL_SHOCK);
    }


    /**
     * 停止等待敲击，重新扫描
     */
    private void stopShockToScan() {
        BleManager.getBleManager().mBleConnectE.disConnect();
        BleManager.getBleManager().mBleConnectE = null;
        finish();
        Intent i = new Intent(BleAutoBindActivity.this, BleAutoBindActivity.class);
        i.putExtra("scan", true);
        startActivity(i);
    }


    /**
     * 停止等待敲击
     */
    private void stopWaitShock() {
        mTranAnim.cancel();
        mActiveBtn.setVisibility(View.INVISIBLE);
        mAskBtn.setVisibility(View.VISIBLE);
        mAskBtn.setText("手环没有振动");
    }

    /**
     * 检查设备
     */
    private void checkBindDevice() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildUpdateBleDevice(BleAutoBindActivity.this
                        , UrlConfig.URL_UPDATE_HR_DEVICE, mCurrDeviceMac, mCurrDeviceName, mUserDE.userId);
                x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

                    @Override
                    public void onSuccess(BaseRE s) {
                        if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mLocalHandler.sendEmptyMessage(MSG_BIND_OK);
                        } else {
                            Message message = new Message();
                            message.what = MSG_BIND_ERROR;
                            message.obj = s.errormsg;
                            mLocalHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Message message = new Message();
                        message.what = MSG_BIND_ERROR;
                        message.obj = HttpExceptionHelper.getErrorMsg(throwable);
                        mLocalHandler.sendMessage(message);
                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 进入打开手环步骤
     */
    private void stepToOpenDevice() {
        mStep = STEP_OPEN_DEVICE;
        mAdornV.setVisibility(View.GONE);
        mOpenDeviceV.setVisibility(View.VISIBLE);
        mActiveBtn.setText("手环已开启");
        mStepTitleTv.setText("长按3秒开机");
        mStepTipTv.setText("FIZZO COR的电源键在侧面");
    }

    /**
     * 取消绑定
     */
    private void cancelBind() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (BleManager.getBleManager().mBleConnectE != null) {
            BleManager.getBleManager().mBleConnectE.disConnect();
        }
        finish();
    }
}
