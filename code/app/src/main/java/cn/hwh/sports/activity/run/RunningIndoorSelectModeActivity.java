package cn.hwh.sports.activity.run;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2017/2/20.
 */
public class RunningIndoorSelectModeActivity extends BaseActivity {


    /* local view */
    @BindView(R.id.v_select_type_1)
    View mSelectType1V;
    @BindView(R.id.v_select_type_2)
    View mSelectType2V;
    @BindView(R.id.v_select_time_30)
    View mSelectTime30V;
    @BindView(R.id.v_select_time_45)
    View mSelectTime45V;
    @BindView(R.id.v_select_time_60)
    View mSelectTime60V;

    /* local data */
    private int mTargetType;
    private int mTargetTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_running_indoor_select_mode;
    }

    @OnClick({R.id.btn_type_1, R.id.btn_type_2, R.id.btn_time_30, R.id.btn_time_45,
            R.id.btn_time_60, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击减肥按钮
            case R.id.btn_type_1:
                mTargetType = SportEnum.TargetType.FAT;
                mSelectType2V.setVisibility(View.INVISIBLE);
                mSelectType1V.setVisibility(View.VISIBLE);
                break;
            //点击增强心肺按钮
            case R.id.btn_type_2:
                mTargetType = SportEnum.TargetType.STRONGER;
                mSelectType1V.setVisibility(View.INVISIBLE);
                mSelectType2V.setVisibility(View.VISIBLE);
                break;
            //30分钟
            case R.id.btn_time_30:
                mTargetTime = 30;
                mSelectTime30V.setVisibility(View.VISIBLE);
                mSelectTime45V.setVisibility(View.INVISIBLE);
                mSelectTime60V.setVisibility(View.INVISIBLE);
                break;
            //45分钟
            case R.id.btn_time_45:
                mTargetTime = 45;
                mSelectTime30V.setVisibility(View.INVISIBLE);
                mSelectTime45V.setVisibility(View.VISIBLE);
                mSelectTime60V.setVisibility(View.INVISIBLE);
                break;
            //60分钟
            case R.id.btn_time_60:
                mTargetTime = 60;
                mSelectTime30V.setVisibility(View.INVISIBLE);
                mSelectTime45V.setVisibility(View.INVISIBLE);
                mSelectTime60V.setVisibility(View.VISIBLE);
                break;
            //下一步
            case R.id.btn_next:
                startRunning();
                break;
        }
    }

    @Override
    protected void initData() {
        mTargetType = SportSpData.getEffortCustomTargetMode(this);
        mTargetTime = SportSpData.getEffortCustomTargetTime(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        resetView();
        if (mTargetType == SportEnum.TargetType.FAT) {
            mSelectType1V.setVisibility(View.VISIBLE);
        } else {
            mSelectType2V.setVisibility(View.VISIBLE);
        }

        if (mTargetTime == 30) {
            mSelectTime30V.setVisibility(View.VISIBLE);
        } else if (mTargetTime == 45) {
            mSelectTime45V.setVisibility(View.VISIBLE);
        } else {
            mSelectTime60V.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    private void resetView() {
        mSelectType1V.setVisibility(View.INVISIBLE);
        mSelectType2V.setVisibility(View.INVISIBLE);
        mSelectTime30V.setVisibility(View.INVISIBLE);
        mSelectTime45V.setVisibility(View.INVISIBLE);
        mSelectTime60V.setVisibility(View.INVISIBLE);
    }


    /**
     * 开始跑步
     */
    private void startRunning() {
        SportSpData.setEffortCustomTargetMode(this,mTargetType);
        SportSpData.setEffortCustomTargetTime(this,mTargetTime);
        if (BleManager.getBleManager().mBleConnectE == null
                || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
            T.showShort(this, "请连接蓝牙设备");
        } else {
            if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
                BleManager.getBleManager().mBleConnectE.stopSync();
                T.showShort(this, "同步暂停，运动结束后恢复");
            }
            int userId = LocalApplication.getInstance().getLoginUser(this).userId;
            WorkoutDE workoutDE = WorkoutDBData.createNewWorkout("室内健身",userId, userId,
                    SportEnum.EffortType.RUNNING_INDOOR, mTargetType, mTargetTime);
            LapDE lapDE = LapDBData.createNewLap(workoutDE.startTime, userId, userId);

            UploadDBData.createWorkoutHeadData(workoutDE);
            UploadDBData.createLapHeadData(lapDE);
            Intent i = new Intent(this, RunningIndoorWarmUpActivity.class);
            startActivity(i);
            finish();
        }
    }
}
