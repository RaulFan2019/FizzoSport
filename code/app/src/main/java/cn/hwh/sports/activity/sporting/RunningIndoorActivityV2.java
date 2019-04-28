package cn.hwh.sports.activity.sporting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.workout.WorkoutIndoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.EffortPointEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.service.FreeEffortService;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.HrZoneU;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2017/4/17.
 */

public class RunningIndoorActivityV2 extends BaseActivity {

    private static final int MSG_ANIM_BIG = 0x01;
    private static final int MSG_ANIM_FINISH = 0x02;


    @BindView(R.id.iv_needle)
    ImageView mNeedleIv;//指针图片
    @BindView(R.id.tv_hr)
    TextView mCurrHrTv;//心率文本
    @BindView(R.id.tv_hr_state)
    TextView mHrStateTv;//心率状态文本
    @BindView(R.id.tv_calorie)
    TextView mCalorieTv;
    @BindView(R.id.tv_cadence)
    TextView mCadenceTv;
    @BindView(R.id.tv_step)
    TextView mStepTv;
    @BindView(R.id.tv_duration)
    TextView mDurationTv;
    @BindView(R.id.v_sport_state_sound)
    View mSportStateSoundV;

    @BindView(R.id.tv_count_num)
    TextView mCountTv;//倒计时文本
    @BindView(R.id.ll_count_num)
    LinearLayout mCountLl;//倒计时页面


    /* local data */
    private UserDE mUserDE;
    private WorkoutDE mWorkoutDE;
    private Typeface mTypeFace;//数字字体
    private long begin = 0;//上次指针的角度

    private boolean mDialogHasShow = false;
    private DialogBuilder mDialogBuilder;

    //倒计时
    private Animation mBigAnimation;// 变大动画
    private int count = 4;// 计数数字
    private boolean mFirstStart = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sporting_running_indoor;
    }

    @OnClick({R.id.btn_stop_sport,R.id.v_sport_state_sound})
    public void onClick(View view) {
        switch (view.getId()) {
            //结束锻炼
            case R.id.btn_stop_sport:
                onFinishBtnClick();
                break;
            //点击声音按钮
            case R.id.v_sport_state_sound:
                if (SportSpData.getTtsEnable(RunningIndoorActivityV2.this)) {
                    SportSpData.setTtsEnable(RunningIndoorActivityV2.this, false);
                    mSportStateSoundV.setBackgroundResource(R.drawable.ic_voice_off_sporting);
                    Toasty.info(RunningIndoorActivityV2.this,"语音关闭").show();
                } else {
                    SportSpData.setTtsEnable(RunningIndoorActivityV2.this, true);
                    mSportStateSoundV.setBackgroundResource(R.drawable.ic_voice_on_sporting);
                    Toasty.info(RunningIndoorActivityV2.this,"语音打开").show();
                }
                break;
        }
    }

    /**
     * 动画Handler
     */
    Handler animHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIM_BIG:
                    count--;
                    mCountTv.setText("" + count);
                    mBigAnimation.reset();
                    mCountTv.startAnimation(mBigAnimation);
                    if (count == 0) {
                        animHandler.sendEmptyMessageDelayed(MSG_ANIM_FINISH, 1000);
                    } else {
                        animHandler.sendEmptyMessageDelayed(MSG_ANIM_BIG, 1000);
                    }
                    break;
                case MSG_ANIM_FINISH:
                    Intent intent = new Intent(getApplicationContext(), FreeEffortService.class);
                    startService(intent);
                    mCountTv.clearAnimation();
                    mCountLl.setVisibility(View.GONE);
                    animHandler.removeMessages(MSG_ANIM_FINISH);
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
//        Log.v(TAG,"onBleEventBus msg :" + event.msg);
        //心率信息
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            updateHrView(event.hr, event.cadence);
            //连接断开信息
        } else if (event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_CONNECT_FAIL) {
            mCurrHrTv.setText("- -");
            //TODO
        } else if (event.msg == BleManager.MSG_CONNECTED) {
            //TODO
        }
    }

    /**
     * 过了整分钟
     *
     * @param effortPointEE
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEffortPointChange(EffortPointEE effortPointEE) {
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mCalorieTv.setText((int) mWorkoutDE.calorie + "");
    }

    /**
     * 时间发生改变
     *
     * @param eventEffortTime
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEventBus(RunningTimeEE eventEffortTime) {
        mStepTv.setText(eventEffortTime.stepCount + "");
        mDurationTv.setText(TimeU.formatSecondsToLongHourTime(eventEffortTime.time));
    }


    @Override
    protected void initData() {
        mUserDE = LocalApplication.getInstance().getLoginUser(RunningIndoorActivityV2.this);
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mDialogBuilder = new DialogBuilder();
        //是否通过用户手动启动的
        if (getIntent().hasExtra("FirstStart")) {
            mFirstStart = true;
        } else {
            mFirstStart = false;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mCurrHrTv.setTypeface(mTypeFace);
        mCadenceTv.setTypeface(mTypeFace);
        mCalorieTv.setTypeface(mTypeFace);
        mStepTv.setTypeface(mTypeFace);
        mDurationTv.setTypeface(mTypeFace);

        if (SportSpData.getTtsEnable(RunningIndoorActivityV2.this)) {
            mSportStateSoundV.setBackgroundResource(R.drawable.ic_voice_on_sporting);
        } else {
            mSportStateSoundV.setBackgroundResource(R.drawable.ic_voice_off_sporting);
        }
    }

    @Override
    protected void doMyCreate() {
        //判断是否显示倒数动画
        if (!mFirstStart) {
            mCountLl.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), FreeEffortService.class);
            startService(intent);
        } else {
            mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_tv_big);
            animHandler.sendEmptyMessageDelayed(1, 1000);
            animHandler.sendEmptyMessageDelayed(2, 10000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //若用户在这个页面 ，希望保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //在还未收到事件通知的时候，更新页面和数据
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mDurationTv.setText(TimeU.formatSecondsToLongHourTime(mWorkoutDE.duration));
        mStepTv.setText(mWorkoutDE.endStep + "");
        mCalorieTv.setText((int) mWorkoutDE.calorie + "");
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
     * 更新心率相关的UI
     */
    private void updateHrView(int hr, int cadence) {
        int percent = hr * 100 / mUserDE.maxHr;
        mHrStateTv.setText(HrZoneU.getDescribeByHrPercent(percent));
        mCurrHrTv.setTextColor(ColorU.getColorByHeartbeat(percent));
        if (percent < 75){
            percent -= 49;
        }else {
            percent -= 50;
        }

        if (percent < 0) {
            percent = 0;
        }
        if (percent > 49) {
            percent = 49;
        }
        mCurrHrTv.setText(hr + "");
        mCadenceTv.setText(cadence + "");
        startAnimation(percent);
    }

    protected void startAnimation(int percent) {
        AnimationSet animationSet = new AnimationSet(true);
        /**
         * 前两个参数定义旋转的起始和结束的度数，后两个参数定义圆心的位置
         */
        // Random random = new Random();
        int end = getDegree(percent);
        RotateAnimation rotateAnimation = new RotateAnimation(begin, end, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation.setDuration(1000);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);
        mNeedleIv.startAnimation(animationSet);
        begin = end;
    }

    private int getDegree(int percent) {
        double a = percent * 180 / 50;
        return (int) a;
    }

    private void onFinishBtnClick() {
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        //若不足1分钟
        if (mWorkoutDE.duration < 60) {
            showShortTimeFinishDialog();
            //锻炼没到时间
        } else {
            showInputLengthDialog();
        }
    }

    /**
     * 显示结束的对话框
     */
    private void showShortTimeFinishDialog() {
        mDialogBuilder.showChoiceDialog(RunningIndoorActivityV2.this, "结束本次锻炼？","此次锻炼未超过1分钟，无法保存记录，确定已经尽力了？",  "确定");
        mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                Intent intent = new Intent(RunningIndoorActivityV2.this, FreeEffortService.class);
                intent.putExtra("CMD", FreeEffortService.CMD_FINISH);
                startService(intent);
//                BleManager.getBleManager().mBleConnectE.controlLight(false);
                T.showShort(RunningIndoorActivityV2.this, "运动不足1分钟，不作记录");
                finish();
            }
        });
    }

    /**
     * 显示输入距离的对话框
     */
    private void showInputLengthDialog() {
        mDialogBuilder.showChoiceDialog(RunningIndoorActivityV2.this, "结束当前锻炼？", null, "确定");
        mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                finishWorkout(0);
            }
        });
//        if (mDialogHasShow) {
//            return;
//        }
//        mDialogHasShow = true;
//        mDialogBuilder.showInputLengthDialog(RunningIndoorActivityV2.this, mWorkoutDE.length);
//        mDialogBuilder.setListener(new DialogBuilder.InputLengthDialogListener() {
//            @Override
//            public void onConfirmBtnClick(String mInputText) {
//                String error = StringU.checkRunningLengthError(RunningIndoorActivityV2.this, mInputText);
//                if (!error.equals("")) {
//                    Toasty.error(RunningIndoorActivityV2.this, error).show();
//                } else {
//                    finishWorkout(Float.valueOf(mInputText) * 1000);
//                    mDialogBuilder.mInputLengthDialog.dismiss();
//                }
//            }
//        });
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }, 500);
    }

    /**
     * 结束记录
     */
    private void finishWorkout(float length) {
//        if (BleManager.getBleManager().mBleConnectE != null){
//            BleManager.getBleManager().mBleConnectE.controlLight(false);
//        }
        Intent intent = new Intent(this, FreeEffortService.class);
        intent.putExtra("CMD", FreeEffortService.CMD_FINISH);
        intent.putExtra("length", length);
        startService(intent);

        //若是自己的锻炼记录，跳转到记录列表
        Bundle bundle = new Bundle();
        bundle.putInt("workoutId", 0);
        bundle.putString("workoutStartTime", mWorkoutDE.startTime);
        startActivity(WorkoutIndoorActivity.class, bundle);

        //跳转到感受页面
//        Bundle subB = new Bundle();
//        subB.putString("startTime",mWorkoutDE.startTime);
//        startActivity(SportingSubmitActivity.class,subB);
        finish();
        if (mWorkoutDE.userId != mWorkoutDE.ownerId) {
            BleManager.getBleManager().mBleConnectE.disConnect();
            String mac = LocalApplication.getInstance().getLoginUser(RunningIndoorActivityV2.this).bleMac;

            if (!mac.equals("")) {
                BleManager.getBleManager().addNewConnect(mac);
            }
        } else {
            if (BleManager.getBleManager().mBleConnectE != null &&
                    BleManager.getBleManager().mBleConnectE.mIsConnected) {
                BleManager.getBleManager().mBleConnectE.reStartSync();
            }
        }
    }
}
