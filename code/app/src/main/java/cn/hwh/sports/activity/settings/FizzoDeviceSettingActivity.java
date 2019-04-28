package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.pickviewlibrary.OptionsPickerView;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetLatestFirmwareRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.MyRequestParams;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2017/1/3.
 */

public class FizzoDeviceSettingActivity extends BaseActivity {

    private static final String TAG = "FizzoDeviceSettingActivity";

    private static final int MSG_UNBIND_ERROR = 1;
    private static final int MSG_UNBIND_OK = 2;
    private static final int MSG_NEED_UPDATE = 3;

    private static final int MODE_RUNNING = 1;//跑步模式
    private static final int MODE_EFFORT = 2;//健身模式


    @BindView(R.id.tv_state)
    TextView mStateTv;//状态文本

    @BindView(R.id.tv_mac)
    TextView mMacTv;

    @BindView(R.id.v_need_update)
    View mUpdateDeviceTitleV;
    @BindView(R.id.tv_update_device)
    TextView mUpdateDeviceTv;

    private DialogBuilder mDialogBuilder;
    private OptionsPickerView mModePv;    //工作模式

    /* local data */
    private UserDE mUserDe;
    private boolean mNeedUpdate = false;
    private GetLatestFirmwareRE mLatestFirmwareRE;
    private String mCurrDeviceFirmware;
    private int mMode;


    private Callback.Cancelable mCancelable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fizzo_device_setting;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //解绑失败
                case MSG_UNBIND_ERROR:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    T.showShort(FizzoDeviceSettingActivity.this, (String) msg.obj);
                    break;
                //解绑成功
                case MSG_UNBIND_OK:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    T.showShort(FizzoDeviceSettingActivity.this, "解除绑定成功");
                    mUserDe.bleMac = "";
                    mUserDe.bleName = "";
                    UserDBData.update(mUserDe);
                    LocalApplication.getInstance().mLoginUser = mUserDe;
                    //断开之前的连接
                    if (BleManager.getBleManager().mBleConnectE != null) {
                        BleManager.getBleManager().mBleConnectE.disConnect();
                        BleManager.getBleManager().mBleConnectE = null;
                    }
                    BleManager.getBleManager().replaceConnect("");
                    finish();
                    break;
                //需要升级
                case MSG_NEED_UPDATE:
                    mUpdateDeviceTitleV.setVisibility(View.VISIBLE);
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
        //改变手表状态
        if (event.msg == BleManager.MSG_CONNECTED
                || event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_NEW_BATTERY) {
            updateWatchView();
        }
    }

    @OnClick({R.id.btn_back, R.id.rl_update_device, R.id.btn_unbind})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.btn_back:
                finish();
                break;
            //点击升级设备
            case R.id.rl_update_device:
                onUpdateDeviceClick();
                break;
            //点击解绑
            case R.id.btn_unbind:
                onUnBindClick();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mUserDe = LocalApplication.getInstance().getLoginUser(FizzoDeviceSettingActivity.this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mModePv = new OptionsPickerView(FizzoDeviceSettingActivity.this);
        mMacTv.setText(mUserDe.bleMac);
    }

    @Override
    protected void doMyCreate() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateWatchView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void causeGC() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_UNBIND_ERROR);
            mHandler.removeMessages(MSG_UNBIND_OK);
        }
        if (mCancelable != null) {
            mCancelable.cancel();
        }

    }

    /**
     * 点击升级设备
     */
    private void onUpdateDeviceClick() {
        if (BleManager.getBleManager().mBleConnectE == null) {
            T.showShort(FizzoDeviceSettingActivity.this, "请连接设备");
            return;
        }
        if (mNeedUpdate) {
            if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 20) {
                T.showShort(FizzoDeviceSettingActivity.this, "电量过低，不宜升级");
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("ftp", mLatestFirmwareRE.ftpurl);
                startActivity(FizzoDeviceUpdateActivity.class, bundle);
            }
        } else {
            T.showShort(FizzoDeviceSettingActivity.this, "不需要升级");
        }
    }

    /**
     * 点击解绑
     */
    private void onUnBindClick() {
        mDialogBuilder.showChoiceDialog(FizzoDeviceSettingActivity.this, "解除绑定",
                "是否解除设备<" + mUserDe.bleName + ">的绑定？", "解绑");
        mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                postUnBind();
            }
        });
    }

    /**
     * 发送解除绑定寻求
     */
    private void postUnBind() {
        mDialogBuilder.showProgressDialog(FizzoDeviceSettingActivity.this, "正在解除绑定", false);
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildRemoveDevice(FizzoDeviceSettingActivity.this
                        , UrlConfig.URL_REMOVE_DEVICE, mUserDe.userId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

                    @Override
                    public void onSuccess(BaseRE s) {
                        if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_UNBIND_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_UNBIND_ERROR;
                            msg.obj = s.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Message msg = new Message();
                        msg.what = MSG_UNBIND_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(throwable);
                        mHandler.sendMessage(msg);
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
     * 更新手表页面
     */
    private void updateWatchView() {
        mUpdateDeviceTitleV.setVisibility(View.GONE);
        //若手表正在连接
        if (BleManager.getBleManager().mBleConnectE == null
                || !BleManager.getBleManager().mBleConnectE.mIsConnected) {
            mStateTv.setText("未连接..");
            mUpdateDeviceTv.setText("---");
        } else {
            if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 20) {
                mStateTv.setText(BleManager.getBleManager().mBleConnectE.mCurrBattery + "% 电量很低");
            } else if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 60) {
                mStateTv.setText(BleManager.getBleManager().mBleConnectE.mCurrBattery + "% 电量中等");
            } else {
                mStateTv.setText(BleManager.getBleManager().mBleConnectE.mCurrBattery + "% 电量很足");
            }
            mCurrDeviceFirmware = BleManager.getBleManager().mBleConnectE.firmwareVer;
            mUpdateDeviceTv.setText(mCurrDeviceFirmware);
            postGetLeastVersion();
        }
    }


    /**
     * 获取最新的版本信息
     */
    private void postGetLeastVersion() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new MyRequestParams(FizzoDeviceSettingActivity.this, UrlConfig.URL_GET_LEAST_FIRMWARE_VERSION);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (BaseResponseParser.ERROR_CODE_NONE == result.errorcode) {
                            mLatestFirmwareRE = JSON.parseObject(result.result, GetLatestFirmwareRE.class);
                            if (StringU.firmwareNeedUpdate(mCurrDeviceFirmware, mLatestFirmwareRE.name)) {
                                mNeedUpdate = true;
                                mHandler.sendEmptyMessage(MSG_NEED_UPDATE);
                            }
                        }
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
            }
        });
    }

}
