package cn.hwh.sports.activity.run;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import cn.hwh.sports.activity.workout.WorkoutIndoorActivity;
import cn.hwh.sports.activity.workout.WorkoutListActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.EffortPointEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.service.FreeEffortService;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2017/2/4.
 */

public class RunningIndoorActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "RunningIndoorActivity";

    /* view */
    @BindView(R.id.tv_curr_duration)
    TextView mCurrDurationTv;
    @BindView(R.id.tv_step_count)
    TextView mStepCountTv;
    @BindView(R.id.tv_curr_cadence)
    TextView mCurrCadenceTv;
    @BindView(R.id.tv_curr_hr)
    TextView mCurrHrTv;

    @BindView(R.id.v_ble_status)
    View mBleStatusV;
    @BindView(R.id.tv_ble_status)
    TextView mBleStatusTv;
    @BindView(R.id.v_play_voice)
    View mPlayVoiceV;
    @BindView(R.id.tv_play_voice)
    TextView mPlayVoiceTv;
    @BindView(R.id.tv_time)
    TextView mDurationTv;//倒计时时间文本
    @BindView(R.id.btn_finish)
    Button mFinishBtn;//结束按钮


    @BindView(R.id.tv_hr_target_normal)
    TextView mHrTargetNormalTv;//靶心率描述文本
    @BindView(R.id.v_hr_pos)
    View mHrPosV;//当前心率位置图示
    LinearLayout.LayoutParams mHrPosLp;

    @BindView(R.id.iv_progress)
    ImageView mProgressIv;//运动进度
    private ClipDrawable mClipDrawable;
    @BindView(R.id.v_warm_end)
    View mWarmEndV;//热身结束位置图示
    @BindView(R.id.v_effort_end)
    View mEffortEndV;//运动结束位置图示
    @BindView(R.id.v_effort_finish)
    View mEffortFinishV;//冷身结束位置图示
    @BindView(R.id.tv_warm_end)
    TextView mWarmEndTv;//热身结束文本
    @BindView(R.id.tv_effort_end)
    TextView mEffortEndTv;//运动结束文本
    @BindView(R.id.tv_effort_finish)
    TextView mEffortFinishTv;//冷身结束文本

    @BindView(R.id.v_v_effort_length)
    View mVEffortLengthTv;//锻炼距离
    @BindView(R.id.v_tv_effort_length)
    View mTvEffortLengthTv;//锻炼文本距离


    /* local data */
    private UserDE mUserDE;
    private WorkoutDE mWorkoutDE;
    private DialogBuilder mDialogBuilder;

    private boolean mDialogHasShow = false;

    private int mTargetLow;
    private int mTargetHigh;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_running_indoor;
    }

    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
//        Log.v(TAG,"onBleEventBus msg :" + event.msg);
        //心率信息
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            mCurrHrTv.setText(event.hr + "");
            mCurrCadenceTv.setText(event.cadence + "");
            if (event.hr < mTargetLow) {
                mCurrHrTv.setTextColor(getResources().getColor(R.color.hr_target_low));
                mHrPosLp.leftMargin = (int) DeviceU.dpToPixel(96 * (event.hr - mUserDE.restHr) / (mTargetLow - mUserDE.restHr));
            } else if (event.hr < mTargetHigh) {
                mCurrHrTv.setTextColor(getResources().getColor(R.color.hr_target_normal));
                mHrPosLp.leftMargin = (int) DeviceU.dpToPixel(96 + 96 * (event.hr - mTargetLow) / (mTargetHigh - mTargetLow));
            } else {
                mCurrHrTv.setTextColor(getResources().getColor(R.color.hr_target_high));
                mHrPosLp.leftMargin = (int) DeviceU.dpToPixel(192 + 96 * (event.hr - mTargetHigh) / (mUserDE.maxHr - mTargetHigh));
            }
            mHrPosV.setLayoutParams(mHrPosLp);

            //连接断开信息
        } else if (event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_CONNECT_FAIL) {
            mBleStatusTv.setText("设备已断开");
            mBleStatusV.setBackgroundResource(R.drawable.ic_ble_disconnect);
            mCurrHrTv.setText("- -");
        } else if (event.msg == BleManager.MSG_CONNECTED) {
            mBleStatusTv.setText("设备已连接");
            mBleStatusV.setBackgroundResource(R.drawable.ic_ble_connected);
        }
    }

    /**
     * 时间发生改变
     *
     * @param eventEffortTime
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEventBus(RunningTimeEE eventEffortTime) {
        mCurrDurationTv.setText(eventEffortTime.time / 60 + "");
        mStepCountTv.setText(eventEffortTime.stepCount + "");

        int lostTime = 0;
        if (eventEffortTime.time / 60 < 5) {
            lostTime = (int) (5 * 60 - eventEffortTime.time);
        } else if (eventEffortTime.time / 60 < (mWorkoutDE.targetTime - 5)) {
            lostTime = (int) ((mWorkoutDE.targetTime - 5) * 60 - eventEffortTime.time);
        } else {
            lostTime = (int) (mWorkoutDE.targetTime * 60 - eventEffortTime.time);
        }
        if (lostTime <= 0) {
            lostTime = 0;
            if (eventEffortTime.time >= mWorkoutDE.targetTime * 60) {
                showInputLengthDialog();
            }
        }
        mDurationTv.setText(TimeU.formatSecondsToLongHourTime(lostTime));
    }

    /**
     * 过了整分钟
     *
     * @param effortPointEE
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEffortPointChange(EffortPointEE effortPointEE) {
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        updateEffortProgress();
    }


    @OnClick({R.id.v_play_voice, R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击声音按钮
            case R.id.v_play_voice:
                break;
            //点击结束按钮
            case R.id.btn_finish:
                onFinishBtnClick();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mUserDE = LocalApplication.getInstance().getLoginUser(RunningIndoorActivity.this);
        mClipDrawable = (ClipDrawable) mProgressIv.getDrawable();
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mHrPosLp = (LinearLayout.LayoutParams) mHrPosV.getLayoutParams();
        if (mWorkoutDE.targetType == SportEnum.TargetType.FAT){
            mTargetHigh = (int) (mUserDE.maxHr * 0.75);
            mTargetLow = (int) (mUserDE.maxHr * 0.55);
        }else if (mWorkoutDE.targetType == SportEnum.TargetType.STRONGER){
            mTargetHigh = (int) (mUserDE.maxHr * 0.8);
            mTargetLow = (int) (mUserDE.maxHr * 0.6);
        }else {
            mTargetHigh = mUserDE.targetHrHigh;
            mTargetLow = mUserDE.targetHrLow;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initVoiceView();
        initTargetView();
    }

    @Override
    protected void doMyCreate() {
        Intent intent = new Intent(getApplicationContext(), FreeEffortService.class);
        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //若用户在这个页面 ，希望保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //在还未收到事件通知的时候，更新页面和数据
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);

        if (BleManager.getBleManager().mBleConnectE != null) {
            if (BleManager.getBleManager().mBleConnectE.mIsConnected) {
                mBleStatusTv.setText("设备已连接");
                mBleStatusV.setBackgroundResource(R.drawable.ic_ble_connected);
            } else {
                mBleStatusTv.setText("设备已断开");
                mBleStatusV.setBackgroundResource(R.drawable.ic_ble_disconnect);
                mCurrHrTv.setText("- -");
            }
        }
        mStepCountTv.setText(mWorkoutDE.endStep + "");
        if (mWorkoutDE.userId != mWorkoutDE.ownerId) {
            mFinishBtn.setText("结束私教");
        }
        //锻炼进度
        updateEffortProgress();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void causeGC() {

    }


    /**
     * 点击结束按钮
     */
    private void onFinishBtnClick() {
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        //若不足1分钟
        if (mWorkoutDE.duration < 60) {
            Intent intent = new Intent(this, FreeEffortService.class);
            intent.putExtra("CMD", FreeEffortService.CMD_FINISH);
            startService(intent);
            T.showShort(RunningIndoorActivity.this, "运动不足1分钟，不作记录");
            finish();
            //锻炼没到时间
        } else if (mWorkoutDE.duration < (mWorkoutDE.targetTime * 60)) {
            mDialogBuilder.showChoiceDialog(RunningIndoorActivity.this, "尚未达到运动目标", "运动尚未达到目标<" + mWorkoutDE.targetTime + "分钟>是否仍然结束?", "结束锻炼");
            mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
                @Override
                public void onConfirmBtnClick() {
                    showInputLengthDialog();
                }
            });
        } else {
            showInputLengthDialog();
        }
    }

    /**
     * 结束记录
     */
    private void finishWorkout(float length) {
        Intent intent = new Intent(this, FreeEffortService.class);
        intent.putExtra("CMD", FreeEffortService.CMD_FINISH);
        intent.putExtra("length", length);
        startService(intent);
        //若是自己的锻炼记录，跳转到记录列表
        if (mWorkoutDE.ownerId == mUserDE.userId){
            startActivity(WorkoutListActivity.class);
        }

        Bundle bundle = new Bundle();
        bundle.putInt("workoutId", 0);
        bundle.putString("workoutStartTime", mWorkoutDE.startTime);
        startActivity(WorkoutIndoorActivity.class, bundle);
        startActivity(RunningIndoorColdDownActivity.class);
        finish();

        if (mWorkoutDE.userId != mWorkoutDE.ownerId) {
            BleManager.getBleManager().mBleConnectE.disConnect();
            String mac = LocalApplication.getInstance().getLoginUser(RunningIndoorActivity.this).bleMac;

            if (!mac.equals("")) {
                BleManager.getBleManager().addNewConnect(mac);
            }
        }else {
            if (BleManager.getBleManager().mBleConnectE != null &&
                    BleManager.getBleManager().mBleConnectE.mIsConnected){
                BleManager.getBleManager().mBleConnectE.reStartSync();
            }
        }
    }

    /**
     * 初始化声音相关UI
     */
    private void initVoiceView() {
        if (SportSpData.getTtsEnable(RunningIndoorActivity.this)) {
            mPlayVoiceV.setBackgroundResource(R.drawable.ic_play_voice_open);
            mPlayVoiceTv.setText(R.string.play_open);
        } else {
            mPlayVoiceV.setBackgroundResource(R.drawable.ic_play_voice_close);
            mPlayVoiceTv.setText(R.string.play_close);
        }
    }

    /**
     * 更新锻炼目标相关UI
     */
    private void initTargetView() {
        mHrTargetNormalTv.setText("合适(" + mTargetLow + "~" + mTargetHigh + ")");
        if (mWorkoutDE.targetType == SportEnum.TargetType.FAT) {
            mWarmEndTv.setText(R.string.effort_target_type_1);
        } else {
            mWarmEndTv.setText(R.string.effort_target_type_2);
        }
        LinearLayout.LayoutParams PosVLayoutParams = (LinearLayout.LayoutParams) mVEffortLengthTv.getLayoutParams();
        LinearLayout.LayoutParams PosTvLayoutParams = (LinearLayout.LayoutParams) mTvEffortLengthTv.getLayoutParams();
        PosVLayoutParams.weight = mWorkoutDE.targetTime;
        PosTvLayoutParams.weight = mWorkoutDE.targetTime;
        mVEffortLengthTv.setLayoutParams(PosVLayoutParams);
        mTvEffortLengthTv.setLayoutParams(PosTvLayoutParams);
    }

    /**
     * 更新运动进度
     */
    private void updateEffortProgress() {
//        Log.v(TAG, "mWorkoutDE.duration:" + mWorkoutDE.duration
//                + ",mWorkoutDE.targetTime:" + mWorkoutDE.targetTime);
        mClipDrawable.setLevel((int) (mWorkoutDE.duration * 10000 / (mWorkoutDE.targetTime * 60)));
        int workoutMin = (int) ((mWorkoutDE.duration + 2) / 60);
        //若已经过了暖身阶段
        if (workoutMin >= 5) {
            mWarmEndV.setBackgroundResource(R.drawable.ic_effort_pos_accent);
            mWarmEndTv.setTextColor(getResources().getColor(R.color.accent));
        } else {
            mWarmEndV.setBackgroundResource(R.drawable.ic_effort_pos_normal);
            mWarmEndTv.setTextColor(getResources().getColor(R.color.tv_mostly));
        }
        //若已经过了锻炼阶段
        if ((mWorkoutDE.targetTime - workoutMin) <= 5) {
            mEffortEndV.setBackgroundResource(R.drawable.ic_effort_pos_accent);
            mEffortEndTv.setTextColor(getResources().getColor(R.color.accent));
        } else {
            mEffortEndV.setBackgroundResource(R.drawable.ic_effort_pos_normal);
            mEffortEndTv.setTextColor(getResources().getColor(R.color.tv_mostly));
        }
        //若已经结束了
        if (workoutMin >= mWorkoutDE.targetTime) {
            mEffortFinishV.setBackgroundResource(R.drawable.ic_effort_pos_accent);
            mEffortFinishTv.setTextColor(getResources().getColor(R.color.accent));
            showInputLengthDialog();
        } else {
            mEffortFinishV.setBackgroundResource(R.drawable.ic_effort_pos_normal);
            mEffortFinishTv.setTextColor(getResources().getColor(R.color.tv_mostly));
        }
    }

    /**
     * 显示输入距离的对话框
     */
    private void showInputLengthDialog() {
        if (mDialogHasShow) {
            return;
        }
        mDialogHasShow = true;
        mDialogBuilder.showInputLengthDialog(RunningIndoorActivity.this, mWorkoutDE.length);
        mDialogBuilder.setListener(new DialogBuilder.InputLengthDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkRunningLengthError(RunningIndoorActivity.this, mInputText);
                if (!error.equals("")) {
                    T.showShort(RunningIndoorActivity.this, error);
                } else {
                    finishWorkout(Float.valueOf(mInputText) * 1000);
                    mDialogBuilder.mInputLengthDialog.dismiss();
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 500);
    }

}
