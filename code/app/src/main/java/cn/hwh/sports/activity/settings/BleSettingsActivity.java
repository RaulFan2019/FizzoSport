package cn.hwh.sports.activity.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.BleScanDeviceAdapter;
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
import cn.hwh.sports.ui.common.WaterWaveView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/12/21.
 */
public class BleSettingsActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "BleSettingsActivity";
    private static final long TIMEOUT_SCAN = 15 * 1000;//15秒后重新扫描
    private static final long FRESH_INTERVAL = 1000;//1秒后刷新列表

    private static final int MSG_STOP_SCAN = 0x01;// 停止扫描
    private static final int MSG_UPDATE_LIST = 0x02;//刷新列表
    private static final int MSG_UPDATE_LIST_REPEAT = 0x03;//定时刷新列表
    private static final int MSG_BIND_ERROR = 0x04;
    private static final int MSG_BIND_OK = 0x05;

    /* view */
    @BindView(R.id.v_state_bar)
    View mStateBarV;
    @BindView(R.id.lv_ble_device)
    ListView mBleDeviceLv;
    @BindView(R.id.btn_scan)
    Button mScanBtn;
    @BindView(R.id.v_scan)
    WaterWaveView mWaveV;

    /* local data */
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    private static final int REQUEST_BLE_PERMISSION = 1;//蓝牙打开请求

    private List<BleScanAE> mScanList = new ArrayList<>();

    private BleScanDeviceAdapter mAdapter;

    private String mCurrDeviceMac = "";//已连接设备的MAC地址
    private String mCurrDeviceName = "";//设备名称

    private UserDE mUserDE;
    private boolean mFirstIn = true;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_ble_settings;
    }


    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止扫描
            if (msg.what == MSG_STOP_SCAN) {
                stopScan();
            } else if (msg.what == MSG_UPDATE_LIST) {
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == MSG_UPDATE_LIST_REPEAT) {
                mAdapter.notifyDataSetChanged();
                mLocalHandler.sendEmptyMessageDelayed(MSG_UPDATE_LIST_REPEAT, FRESH_INTERVAL);
                //绑定失败
            } else if (msg.what == MSG_BIND_ERROR) {
                T.showShort(BleSettingsActivity.this, (String) msg.obj);
                //断开之前的连接
                if (BleManager.getBleManager().mBleConnectE != null) {
                    BleManager.getBleManager().mBleConnectE.disConnect();
                }
                listChangeState(mCurrDeviceMac, BleScanAE.STATE_DISCONNECT);
                mCurrDeviceMac = "";
                mCurrDeviceName = "";
                //绑定成功
            } else if (msg.what == MSG_BIND_OK) {
                mUserDE.bleMac = mCurrDeviceMac;
                mUserDE.bleName = mCurrDeviceName;
                UserDBData.update(mUserDE);
                LocalApplication.getInstance().mLoginUser = mUserDE;
                finish();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
        switch (event.msg) {
            //已连接
            case BleManager.MSG_CONNECTED:
                break;
            //失去连接
            case BleManager.MSG_DISCONNECT:
                listChangeState(mCurrDeviceMac, BleScanAE.STATE_DISCONNECT);
                break;
            //新的心率值
            case BleManager.MSG_CAN_BIND:
                listChangeState(mCurrDeviceMac, BleScanAE.STATE_CONNECTED);
                //若首次匹配新设备,检查设备是否被绑定
                if (!mUserDE.bleMac.equals(mCurrDeviceMac)) {
                    checkBindDevice();
                }
                break;
            case BleManager.MSG_CONNECT_FAIL:
                break;
            //非Fizzo 手环
            case BleManager.MSG_NOT_FIZZO:
                T.showShort(BleSettingsActivity.this,"抱歉，该设备不是FIZZO手环，绑定失败");
                if (BleManager.getBleManager().mBleConnectE != null) {
                    BleManager.getBleManager().mBleConnectE.disConnect();
                }
                listChangeState(mCurrDeviceMac, BleScanAE.STATE_DISCONNECT);
                break;
        }
    }

    @OnClick({R.id.btn_scan, R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                onClickScanBtn();
                break;
            case R.id.btn_back:
                if (BleManager.getBleManager().mBleConnectE != null) {
                    BleManager.getBleManager().mBleConnectE.disConnect();
                }
                finish();
                break;
        }
    }

    /**
     * listView 的监听事件
     */
    BleScanDeviceAdapter.localListener mListListener = new BleScanDeviceAdapter.localListener() {
        @Override
        public void onSelectDevice(int position) {
            //若是新设备,去连接
            if (!mScanList.get(position).device.getAddress().equals(mCurrDeviceMac)) {
                mCurrDeviceMac = mScanList.get(position).device.getAddress();
                mCurrDeviceName = mScanList.get(position).device.getName();
                mLocalHandler.removeMessages(MSG_STOP_SCAN);
                stopScan();
                //开始检查设备是否可以绑定
                startConnectGatt(mCurrDeviceMac);
                listChangeState(mCurrDeviceMac, BleScanAE.STATE_CONNECTING);
            }
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
        mUserDE = LocalApplication.getInstance().getLoginUser(BleSettingsActivity.this);
        mCurrDeviceMac = mUserDE.bleMac;
        mCurrDeviceName = mUserDE.bleName;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAdapter = new BleScanDeviceAdapter(BleSettingsActivity.this, mScanList, mListListener);
        mBleDeviceLv.setAdapter(mAdapter);
        //设置 wave
        mWaveV.setWaveInfo(160f, 6f, 1f, 2f, Color.parseColor("#00bfa5"));
    }

    @Override
    protected void doMyCreate() {
        initBLE();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置色块区域高度
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mStateBarV.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = DeviceU.getStatusBarHeight(this);
            mStateBarV.setLayoutParams(layoutParams);
        } else {
            mStateBarV.setVisibility(View.GONE);
        }
        // 若蓝牙未打开
        if (!mBluetoothAdapter.isEnabled()) {
            // 蓝牙未打开 请求蓝牙打开权限
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLE_PERMISSION);
            return;
        }

        if (mFirstIn) {
            onClickScanBtn();
            mFirstIn = false;
        }
    }

    @Override
    protected void causeGC() {
        mScanList.clear();
        if (mLocalHandler != null) {
            mLocalHandler.removeMessages(MSG_BIND_ERROR);
            mLocalHandler.removeMessages(MSG_BIND_OK);
            mLocalHandler.removeMessages(MSG_STOP_SCAN);
            mLocalHandler.removeMessages(MSG_UPDATE_LIST);
            mLocalHandler.removeMessages(MSG_UPDATE_LIST_REPEAT);
        }
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
     * 点击扫描按钮
     */
    private void onClickScanBtn() {
        //改变连接状态的View
        mScanBtn.setText("扫描中");
        mScanBtn.setEnabled(false);
        mScanBtn.setClickable(false);
        mWaveV.setVisibility(View.VISIBLE);
        mWaveV.resetWave();
        mBluetoothAdapter.startLeScan(new UUID[]{BleConfig.UUID_HEART_RATE_SERVICE}, mLeScanCallback);
        mLocalHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, TIMEOUT_SCAN);
        mLocalHandler.sendEmptyMessageDelayed(MSG_UPDATE_LIST_REPEAT, FRESH_INTERVAL);
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        mScanBtn.setText("再次扫描");
        mScanBtn.setEnabled(true);
        mScanBtn.setClickable(true);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mWaveV.setVisibility(View.GONE);
    }


    /**
     * 扫描新设备
     */
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            Log.v(TAG,"onLeScan");
            listChange(device, rssi);
        }
    };

    /**
     * 当接收到新的数据时
     *
     * @param device
     */
    private void listChange(final BluetoothDevice device, final int rssi) {
        //若已在显示列表中
        for (BleScanAE le : mScanList) {
            if ((le.device.getAddress().equals(device.getAddress()))) {
                le.rssi = rssi;
                return;
            }
        }
        BleScanAE leBleScan = new BleScanAE(device, BleScanAE.STATE_DISCONNECT, rssi);
        mScanList.add(leBleScan);
        mLocalHandler.sendEmptyMessage(MSG_UPDATE_LIST);
    }

    /**
     * 改变列表中当前设备状态
     *
     * @param state
     */
    private void listChangeState(final String mac, final int state) {
        for (int i = 0, size = mScanList.size(); i < size; i++) {
            if (mScanList.get(i).device.getAddress().equals(mac)) {
                mScanList.get(i).mConnectState = state;
            } else {
                mScanList.get(i).mConnectState = BleScanAE.STATE_DISCONNECT;
            }
        }
        mLocalHandler.sendEmptyMessage(MSG_UPDATE_LIST);
    }

    /**
     * 连接Ble设备
     */
    private void startConnectGatt(final String mac) {
        //断开之前的连接
        if (BleManager.getBleManager().mBleConnectE != null) {
            BleManager.getBleManager().mBleConnectE.disConnect();
        }
        BleManager.getBleManager().addNewConnect(mac);
    }

    /**
     * 检查设备
     */
    private void checkBindDevice() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildUpdateBleDevice(BleSettingsActivity.this
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
}
