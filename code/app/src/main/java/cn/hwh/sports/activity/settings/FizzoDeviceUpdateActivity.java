package cn.hwh.sports.activity.settings;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.ble.DfuService;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.event.SyncWatchWorkoutEE;
import cn.hwh.sports.ssh.ConnectionStatusListener;
import cn.hwh.sports.ssh.SessionController;
import cn.hwh.sports.ssh.SessionUserInfo;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by Raul.Fan on 2017/1/4.
 */
public class FizzoDeviceUpdateActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "FizzoDeviceUpdateActivity";

    private static final int MSG_CONNECT_SSH_OK = 0x01;//连接SSH服务器
    private static final int MSG_DOWNLOAD_OK = 0x02;
    private static final int MSG_UPDATE_PERCENT = 0x03;
    private static final int MSG_UPDATE_COMPLETE = 0x04;
    private static final int MSG_UPDATE_ERROR = 0x05;
    private static final int MSG_PROGRESS_NOT_UPDATE = 0x06;

    private static final long INTERVAL_PROGRESS_NOT_UPDATE = 5000;

    @BindView(R.id.tv_step_title)
    TextView mStepTitleTv;
    @BindView(R.id.tv_percent)
    TextView mPercentTv;
    @BindView(R.id.v_update_outside)
    View mUpdateOutsideV;
    @BindView(R.id.v_update_inside)
    View mUpdateInsideV;
    @BindView(R.id.btn_pause_update)
    LinearLayout mPauseUpdateBtn;
    @BindView(R.id.btn_finish)
    Button mFinishBtn;

    private UserDE mUserDe;
    private String mFtpUrl;

    private RotateAnimation mRotateOutsideAnim;
    private RotateAnimation mRotateInsideAnim;

    /* ssh */
    private SessionUserInfo mSUI;

    /* dfu */
    DfuServiceController controller;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fizzo_device_update;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //已连接上SSH服务器
                case MSG_CONNECT_SSH_OK:
                    downloadUpdateFile();
                    break;
                //下载成功
                case MSG_DOWNLOAD_OK:
                    updateNow();
                    break;
                //下载更新百分比
                case MSG_UPDATE_PERCENT:
                    mPercentTv.setText((10 + (int) (msg.arg1 * 0.2)) + "%");
                    break;
                //更新完成
                case MSG_UPDATE_COMPLETE:
                    mUpdateOutsideV.setBackgroundResource(R.drawable.bg_update_device_finish);
                    mUpdateInsideV.setVisibility(View.GONE);
                    mRotateInsideAnim.cancel();
                    mRotateOutsideAnim.cancel();
                    mUpdateOutsideV.clearAnimation();
                    mUpdateInsideV.clearAnimation();
                    mPercentTv.setVisibility(View.GONE);
                    mPauseUpdateBtn.setVisibility(View.GONE);
                    mFinishBtn.setVisibility(View.VISIBLE);
                    mStepTitleTv.setText("升级完成");
                    T.showShort(FizzoDeviceUpdateActivity.this, "升级完成");
                    break;
                case MSG_UPDATE_ERROR:
                    T.showShort(FizzoDeviceUpdateActivity.this, (String) msg.obj);
                    finish();
                    break;
                case MSG_PROGRESS_NOT_UPDATE:
                    T.showShort(FizzoDeviceUpdateActivity.this, "更新出错,请重新尝试");
                    finish();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_pause_update, R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pause_update:
                if (controller != null) {
                    controller.pause();
                    controller.abort();
                }
                finish();
                break;
            case R.id.btn_finish:
                finish();
                break;
        }
    }


    /**
     * 手表同步手机完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(SyncWatchWorkoutEE event) {
        Log.v(TAG,"SyncWatchWorkoutEE event.msg:" + event.msg);
        //同步完成
        if (event.msg == SyncWatchWorkoutEE.MSG_FINISH) {
            mPercentTv.setText("10%");
            mStepTitleTv.setText("准备升级");
            if (BleManager.getBleManager().mBleConnectE != null) {
                BleManager.getBleManager().mBleConnectE.readyUpdate();
            }
        }
    }

    /**
     * 手表同步手机完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
//        Log.v(TAG,"BleConnectEE event.msg:" + event.msg);
        //同步完成
        if (event.msg == BleManager.MSG_READY_OTA_OK) {
            mStepTitleTv.setText("正在下载更新包");
            BleManager.getBleManager().mConnectMac = "";
            BleManager.getBleManager().stopScan();
            if (BleManager.getBleManager().mBleConnectE != null) {
                BleManager.getBleManager().mBleConnectE.disConnect();
                BleManager.getBleManager().mBleConnectE = null;
            }
            startConnectSSH();
        }
    }


    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            Log.v(TAG, "onDeviceConnecting:" + "Connecting…");
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            Log.v(TAG, "onDfuProcessStarting:" + "Starting DFU…");
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            Log.v(TAG, "onEnablingDfuMode:" + "Starting bootloader…");
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            Log.v(TAG, "onFirmwareValidating:" + "Validating…");
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            Log.v(TAG, "onDeviceDisconnecting:" + "Disconnecting…");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    manager.cancel(DfuService.NOTIFICATION_ID);
//                    Message msg = new Message();
//                    msg.what = MSG_UPDATE_ERROR;
//                    msg.obj = "错误断开,请重新尝试升级";
//                    mHandler.sendMessage(msg);
//                }
//            }, 200);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            Log.v(TAG, "onDfuCompleted:" + "Done");
            mHandler.removeMessages(MSG_PROGRESS_NOT_UPDATE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                    mHandler.sendEmptyMessage(MSG_UPDATE_COMPLETE);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            Log.v(TAG, "onDfuAborted:" + "Aborted");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            Log.v(TAG, "onProgressChanged " + "percent:" + percent + ",speed:" + speed);
            mHandler.removeMessages(MSG_PROGRESS_NOT_UPDATE);
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_NOT_UPDATE,INTERVAL_PROGRESS_NOT_UPDATE);
            mPercentTv.setText((30 + (int) (percent * 0.7)) + "%");
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            Log.v(TAG, "onError " + "deviceAddress:" + deviceAddress + ",msg:" + message);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                    Message msg = new Message();
                    msg.what = MSG_UPDATE_ERROR;
                    msg.obj = message;
                    mHandler.sendMessage(msg);
                }
            }, 200);
        }
    };

    @Override
    protected void initData() {
        mUserDe = LocalApplication.getInstance().getLoginUser(FizzoDeviceUpdateActivity.this);
//        Log.v("TAG","getIntent().getExtras() == null" + (getIntent().getExtras() == null));
        mFtpUrl = getIntent().getExtras().getString("ftp");
        mRotateOutsideAnim = (RotateAnimation) AnimationUtils.loadAnimation(FizzoDeviceUpdateActivity.this, R.anim.rotating);
        mRotateInsideAnim = (RotateAnimation) AnimationUtils.loadAnimation(FizzoDeviceUpdateActivity.this, R.anim.rotating_contrary);
        mRotateOutsideAnim.setInterpolator(new LinearInterpolator());//不停顿
        mRotateInsideAnim.setInterpolator(new LinearInterpolator());//不停顿
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mPercentTv.setTypeface(typeFace);
    }

    @Override
    protected void doMyCreate() {
        EventBus.getDefault().register(this);
        ActivityStackManager.getAppManager().addWatchUpdateActivity(this);
        //同步历史
        if (BleManager.getBleManager().mBleConnectE != null) {
            if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 20){
                T.showShort(FizzoDeviceUpdateActivity.this,"电量太低不适合升级");
                finish();
                return;
            }else {
//                BleManager.getBleManager().mBleConnectE.reStartSync();
                mPercentTv.setText("10%");
                mStepTitleTv.setText("准备升级");
                BleManager.getBleManager().mBleConnectE.readyUpdate();
            }
        }
        mUpdateOutsideV.setAnimation(mRotateOutsideAnim);
        mUpdateInsideV.setAnimation(mRotateInsideAnim);
        mRotateOutsideAnim.start();
        mRotateInsideAnim.start();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        ActivityStackManager.getAppManager().finishWatchUpdateActivity(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        BleManager.getBleManager().addNewConnect(mUserDe.bleMac);
        if (mRotateInsideAnim!= null){
            mRotateInsideAnim.cancel();
        }
        if (mRotateOutsideAnim != null){
            mRotateOutsideAnim.cancel();
        }
        mUpdateInsideV.clearAnimation();
        mUpdateOutsideV.clearAnimation();
    }

    /**
     * 开始连接SSH服务器
     */
    private void startConnectSSH() {
        String url;
        if (MyBuildConfig.BUILD_TYPE == AppEnum.BuildType.BUILD_ALPHA) {
            url = "121.43.113.78";
        } else {
            url = "121.43.110.233";
        }
        mSUI = new SessionUserInfo("iosssh", url, "Gogogo123!Abery#", 22);

        SessionController.getSessionController().setUserInfo(mSUI);
        SessionController.getSessionController().connect();
        SessionController.getSessionController().setConnectionStatusListener(new ConnectionStatusListener() {
            @Override
            public void onDisconnected() {
                Log.v(TAG, "ftps disconnected");
            }

            @Override
            public void onConnected() {
                Log.v(TAG, "ftps connected");
                SessionController.getSessionController().setConnectionStatusListener(null);
                mHandler.sendEmptyMessage(MSG_CONNECT_SSH_OK);
            }
        });
    }

    /**
     * 下载更新文件包
     */
    private void downloadUpdateFile() {
        // sftp the file
        try {
            SessionController.getSessionController().downloadFile(mFtpUrl, FileConfig.DOWNLOAD_UPDATE_DEVICE_ZIP,
                    new SftpProgressMonitor() {
                        long mSize = 0;
                        long mCount = 0;

                        @Override
                        public void init(int op, String src, String dest, long max) {
                            mSize = max;
                        }

                        @Override
                        public boolean count(long count) {
                            mCount += count;
//                            Log.v(TAG, "mSize: " + mSize + ",mCount:" + mCount);
                            int progress = (int) ((float) (mCount) / (float) (mSize) * (float) 100);
                            Message message = new Message();
                            message.what = MSG_UPDATE_PERCENT;
                            message.arg1 = progress;
                            mHandler.sendMessage(message);
                            return true;
                        }

                        @Override
                        public void end() {
                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_OK);
                        }
                    });
        } catch (JSchException je) {
            Log.d(TAG, "JschException " + je.getMessage());
        } catch (SftpException se) {
            Log.d(TAG, "SftpException " + se.getMessage());
        }
    }


    private void updateNow() {
        mStepTitleTv.setText("设备升级中");

        final boolean keepBond = false;
        final DfuServiceInitiator starter = new DfuServiceInitiator(mUserDe.bleMac)
                .setDeviceName(mUserDe.name)
                .setKeepBond(keepBond);
        // If you want to have experimental buttonless DFU feature supported call additionally:
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        // but be aware of this: https://devzone.nordicsemi.com/question/100609/sdk-12-bootloader-erased-after-programming/
        // and other issues related to this experimental service.

        // Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
        // In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.
        final String mFilePath = FileConfig.DOWNLOAD_UPDATE_DEVICE_ZIP;
//        final String mFilePath = FileConfig.DOWNLOAD_PATH + "test.zip";//Test

        final Uri mFileStreamUri = getImageContentUri(FizzoDeviceUpdateActivity.this, new File(mFilePath));
        starter.setZip(mFileStreamUri, mFilePath);
        controller = starter.start(this, DfuService.class);
        // You may use the controller to pause, resume or abort the DFU process.
    }

    /**
     * Gets the content:// URI  from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
