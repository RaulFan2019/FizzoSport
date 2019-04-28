package cn.hwh.sports.fragment.main;

import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.monitor.RecordSleepActivity;
import cn.hwh.sports.activity.monitor.RecordWeightActivity;
import cn.hwh.sports.activity.run.ReadyRunActivity;
import cn.hwh.sports.activity.run.RunningIndoorSelectModeActivity;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.db.WorkoutSyncDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/11/15.
 */

public class MainAddDataFragment extends BaseFragment {

    /* contains */
    private static final String TAG = "MainAddDataFragment";

    @BindView(R.id.view_bg)
    View mBgView;
    @BindView(R.id.ll_add_menu)
    LinearLayout mMenuLl;

    private AlphaAnimation alphaAnimation;
    private Animation translateOpenAnimation;

    /* 构造函数 */
    public static MainAddDataFragment newInstance() {
        MainAddDataFragment fragment = new MainAddDataFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_add_data;
    }

    @OnClick({R.id.ll_record_sport, R.id.ll_record_weight, R.id.ll_record_sleep,
            R.id.view_bg, R.id.ll_record_run, R.id.ll_record_run_indoor})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击记录跑步
            case R.id.ll_record_run:
                onRunningRecordClick();
                break;
            //点击室内健身
            case R.id.ll_record_sport:
                onIndoorRecordClick();
                break;
            //点击增加体重
            case R.id.ll_record_weight:
                startActivity(RecordWeightActivity.class);
                MainActivity var1 = (MainActivity) getActivity();
                var1.onAddDataClick();
                break;
            //点击增加睡眠
            case R.id.ll_record_sleep:
                startActivity(RecordSleepActivity.class);
                MainActivity var2 = (MainActivity) getActivity();
                var2.onAddDataClick();
                break;
            //点击背景
            case R.id.view_bg:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.onAddDataClick();
                break;
            //室内跑步
            case R.id.ll_record_run_indoor:
                onRecordRunIndoorClick();
                break;
        }
    }

    @Override
    protected void initParams() {
        alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(200);
        translateOpenAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_menu_open);
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        //背景动画
        mBgView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBgView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //菜单出现动画
        mMenuLl.startAnimation(translateOpenAnimation);
        translateOpenAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMenuLl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onInVisible() {
//        mBgView.setVisibility(View.INVISIBLE);
//        mMenuLl.setVisibility(View.INVISIBLE);
    }

    /**
     * 点击室外跑步
     */
    private void onRunningRecordClick() {
        if (BleManager.getBleManager().mBleConnectE == null
                || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
            T.showShort(getActivity(), "请连接设备");
        } else {
            if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
                T.showShort(getActivity(), "请等待同步完成");
            } else {
                startActivity(ReadyRunActivity.class);
            }
        }
        MainActivity activity = (MainActivity) getActivity();
        activity.onAddDataClick();
    }


    /**
     * 点击室内跑步
     */
    private void onRecordRunIndoorClick() {
        startActivity(RunningIndoorSelectModeActivity.class);
        MainActivity activity = (MainActivity) getActivity();
        activity.onAddDataClick();
    }

    /**
     * 点击室内健身
     */
    private void onIndoorRecordClick() {
        if (BleManager.getBleManager().mBleConnectE == null
                || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
            T.showShort(getActivity(), "请连接蓝牙设备");
        } else {
            if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
                BleManager.getBleManager().mBleConnectE.stopSync();
                T.showShort(getActivity(), "同步暂停，运动结束后恢复");
            }
            int userId = LocalApplication.getInstance().getLoginUser(getActivity()).userId;
            WorkoutDE workoutDE = WorkoutDBData.createNewWorkout("室内健身",userId, userId, SportEnum.EffortType.FREE_INDOOR,
                    SportEnum.TargetType.DEFAULT, 0);
            LapDE lapDE = LapDBData.createNewLap(workoutDE.startTime, userId, userId);

            UploadDBData.createWorkoutHeadData(workoutDE);
            UploadDBData.createLapHeadData(lapDE);
            Intent i = new Intent(getActivity(), SportingIndoorActivity.class);
            startActivity(i);
        }
        MainActivity activity = (MainActivity) getActivity();
        activity.onAddDataClick();
    }

}
