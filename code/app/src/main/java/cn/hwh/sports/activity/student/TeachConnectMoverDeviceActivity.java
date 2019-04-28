package cn.hwh.sports.activity.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.ui.common.CircularImage;
import cn.hwh.sports.utils.ImageU;

/**
 * Created by Raul.Fan on 2016/11/30.
 * 连接学员设备
 */
public class TeachConnectMoverDeviceActivity extends BaseActivity {

    @BindView(R.id.v_connecting)
    View mConnectingV;
    @BindView(R.id.iv_avatar)
    CircularImage ivAvatar;

    /* data */
    private UserDE mMover;
    private boolean mConnect = false;

    private RotateAnimation mRotateAnimation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teach_connect_stu_device;
    }

    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
        //心率数据
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            if (!mConnect) {
                mConnect = true;
                int userId = LocalApplication.getInstance().getLoginUser(TeachConnectMoverDeviceActivity.this).userId;
                WorkoutDE workoutDE = WorkoutDBData.createNewWorkout("室内健身",userId, mMover.userId,
                        SportEnum.EffortType.FREE_INDOOR, SportEnum.TargetType.DEFAULT, 0);
                LapDE lapDE = LapDBData.createNewLap(workoutDE.startTime, userId, mMover.userId);

                UploadDBData.createWorkoutHeadData(workoutDE);
                UploadDBData.createLapHeadData(lapDE);
                startActivity(SportingIndoorActivity.class);
                finish();
            }
        }
    }

    @OnClick(R.id.tv_stop)
    public void onClick(View view) {
        BleManager.getBleManager().mBleConnectE.disConnect();
        String mac = LocalApplication.getInstance().getLoginUser(TeachConnectMoverDeviceActivity.this).bleMac;
        if (!mac.equals("")) {
            BleManager.getBleManager().addNewConnect(mac);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BleManager.getBleManager().mBleConnectE.disConnect();
        String mac = LocalApplication.getInstance().getLoginUser(TeachConnectMoverDeviceActivity.this).bleMac;
        if (!mac.equals("")) {
            BleManager.getBleManager().addNewConnect(mac);
        }
    }

    @Override
    protected void initData() {
        int moverId = getIntent().getExtras().getInt("userId");
        mMover = UserDBData.getUserById(moverId);

        mRotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());//不停顿
        mRotateAnimation.setDuration(1500);//设置动画持续时间
        mRotateAnimation.setRepeatCount(-1);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ImageU.loadUserImage(mMover.avatar, ivAvatar);
        mConnectingV.setAnimation(mRotateAnimation);
        mRotateAnimation.start();
    }

    @Override
    protected void doMyCreate() {
        EventBus.getDefault().register(this);
        BleManager.getBleManager().addNewConnect(mMover.bleMac);

        ;
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        mRotateAnimation.cancel();
    }

}
