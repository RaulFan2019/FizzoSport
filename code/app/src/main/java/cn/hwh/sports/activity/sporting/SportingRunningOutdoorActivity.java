package cn.hwh.sports.activity.sporting;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
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
import cn.hwh.sports.activity.workout.WorkoutRunningOutdoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.RunningLocationEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.fragment.spoting.RunningOutdoorMapFragment;
import cn.hwh.sports.service.RunningService;
import cn.hwh.sports.ui.common.CircleProgressView;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.RunningOutdoorLayout;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.HrZoneU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TTsU;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by machenike on 2017/5/25 0025.
 */
public class SportingRunningOutdoorActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "SportingRunningOutdoorActivity";

    private static final int MSG_ANIM_COUNT_BIG = 0x01;
    private static final int MSG_ANIM_COUNT_FINISH = 0x02;

    /* local view */
    @BindView(R.id.ll_base)
    RunningOutdoorLayout mBaseLl;
    @BindView(R.id.fl_main)
    FrameLayout flMain;

    //控制按钮布局
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.fl_finish_btn_layout)
    FrameLayout flFinishBtnLayout;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.fl_continue_btn_layout)
    FrameLayout flContinueBtnLayout;
    @BindView(R.id.btn_pause)
    Button btnPause;
    @BindView(R.id.include_running_btns)
    View includeRunningBtns;// 正在跑的界面
    @BindView(R.id.include_pause_btns)
    View includePausedBtns;// 暂停界面

    //倒计时布局和文本
    @BindView(R.id.tv_count_num)
    TextView tvCountNum;
    @BindView(R.id.ll_count_num)
    LinearLayout llCountNum;

    //心率表盘
    @BindView(R.id.iv_needle)
    ImageView ivNeedle;//指针
    @BindView(R.id.tv_hr)
    TextView tvHr;//心率文本
    @BindView(R.id.tv_hr_state)
    TextView tvHrState;//心率状态文本

    //状态图标
    @BindView(R.id.v_sport_state_sound)
    View vSportStateSound;//声音状态图标
    @BindView(R.id.v_sport_map)
    View vMap;
    @BindView(R.id.tv_length)
    TextView tvLength;
    @BindView(R.id.tv_cadence)
    TextView tvCadence;
    @BindView(R.id.tv_pace)
    TextView tvPace;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.progressbar_pause)
    CircleProgressView progressbarPause;
    @BindView(R.id.tv_length_name)
    TextView tvLengthName;
    @BindView(R.id.tv_cadence_name)
    TextView tvCadenceName;
    @BindView(R.id.tv_pace_name)
    TextView tvPaceName;
//    @BindView(R.id.tv_duration_name)
//    TextView tvDurationName;


    //地图Fragment布局
    @BindView(R.id.fl_map_fragment_root)
    FrameLayout flMapFragmentRoot;
    @BindView(R.id.ll_other_data)
    LinearLayout llOtherData;
    @BindView(R.id.ll_duration_data)
    LinearLayout llDurationData;


    private DialogBuilder mDialogBuilder;
    /* local data */
    private UserDE mUserDE;//用户信息
    private WorkoutDE mWorkoutDE;//跑步信息

    //UI
    private int mScreenWidth;// 宽度
    private Typeface mTypeFace;//数字字体
    private long mBeginDegree = 0;//上次指针的角度

    //倒计时
    private Animation mBigAnimation;// 变大动画
    private int mCount = 4;// 计数数字
    private boolean mFirstStart = false;
    //按钮动画时间
    private int mFadeAnimationTime;

    private boolean mIsRunning = true;//是否正在跑状态
    private boolean mMapState = false;//地图显示状态

    private RunningOutdoorMapFragment mMapFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sporting_running_outdoor;
    }


    /**
     * on Click
     *
     * @param view
     */
    @OnClick({R.id.btn_finish, R.id.btn_continue,
            R.id.v_sport_map, R.id.v_sport_state_sound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //点击结束
            case R.id.btn_finish:
                onFinishBtnClick();
                break;
            //点击继续
            case R.id.btn_continue:
                onContinueBtnClick();
                break;
            //点击地图
            case R.id.v_sport_map:
                openMap();
                break;
            //点击声音控制按钮
            case R.id.v_sport_state_sound:
                onVoiceClick();
                break;
        }
    }

    /**
     * 动画Handler
     */
    Handler mLocalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //计时变大动画
                case MSG_ANIM_COUNT_BIG:
                    animCountBig();
                    break;
                //计时结束
                case MSG_ANIM_COUNT_FINISH:
                    animCountFinish();
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
        //心率信息
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            updateHrView(event.hr, event.cadence);
            //连接断开信息
        } else if (event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_CONNECT_FAIL) {
            tvHr.setText("- -");
            tvHrState.setText("连接断开");
        }
    }

    /**
     * 接收到时间变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RunningTimeEE event) {
        long time = event.time;
        if (time > 0) {
            tvDuration.setText(TimeU.formatSecondsToShortHourTime(time));
        }
    }

    /**
     * 接收到距离变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RunningLocationEE event) {
        tvLength.setText(LengthUtils.formatLength(event.length));
        int speed = event.speed;
        if (speed != 0) {
            tvPace.setText(TimeU.formatSecondsToPace(speed));
        } else {
            tvPace.setText("- -");
        }
    }


    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mUserDE = LocalApplication.getInstance().getLoginUser(SportingRunningOutdoorActivity.this);
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        //是否通过用户手动启动的
        if (getIntent().hasExtra("FirstStart")) {
            mFirstStart = true;
        } else {
            mFirstStart = false;
        }
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //文本样式
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        tvDuration.setTypeface(mTypeFace);
        tvHr.setTypeface(mTypeFace);
        tvLength.setTypeface(mTypeFace);
        tvCadence.setTypeface(mTypeFace);
        tvPace.setTypeface(mTypeFace);
        //初始化声音
        if (SportSpData.getTtsEnable(SportingRunningOutdoorActivity.this)) {
            vSportStateSound.setBackgroundResource(R.drawable.ic_voice_on_sporting);
        } else {
            vSportStateSound.setBackgroundResource(R.drawable.ic_voice_off_sporting);
        }
        //长按事件
        btnPause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        progressbarPause.pressed();
                        break;
                    case MotionEvent.ACTION_UP:
                        progressbarPause.released();
                        break;
                }
                return false;
            }
        });
        // 长按暂停监听
        progressbarPause.setOnProgressListener(new CircleProgressView.onProgressListener() {
            @Override
            public void onEnd() {
                progressbarPause.released();
                progressbarPause.setProgress(0);
                onPauseBtnClick();
            }

            @Override
            public void onInit() {

            }
        });
    }

    @Override
    protected void doMyCreate() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //判断是否显示倒数动画
        if (!mFirstStart) {
            mLocalHandler.sendEmptyMessage(MSG_ANIM_COUNT_FINISH);
        } else {
            mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_tv_big);
            mLocalHandler.sendEmptyMessageDelayed(MSG_ANIM_COUNT_BIG, 1000);
            mLocalHandler.sendEmptyMessageDelayed(MSG_ANIM_COUNT_FINISH, 10000);
        }
        // 启动服务
        Intent intent = new Intent(this, RunningService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        if (mWorkoutDE != null) {
            tvDuration.setText(TimeU.formatSecondsToShortHourTime(mWorkoutDE.duration));
            tvLength.setText(LengthUtils.formatLength(mWorkoutDE.length));
        }
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
     * 点击暂停
     */
    private void onPauseBtnClick() {
        pauseAnimation();
        setRunningStatus(RunningService.CMD_PAUSE);
        setPauseView(true);
    }

    /**
     * 点击继续
     */
    private void onContinueBtnClick() {
        continueAnimation();
        setRunningStatus(RunningService.CMD_CONTINUE);
        setPauseView(false);
    }

    /**
     * 点击继续
     */
    private void onFinishBtnClick() {
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        //若不足1分钟
        if (mWorkoutDE.length < 100) {
            showShortTimeFinishDialog();
            //锻炼没到时间
        } else {
            showInputLengthDialog();
        }
    }


    /**
     * 打开地图
     */
    private void openMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mMapFragment == null) {
            mMapFragment = RunningOutdoorMapFragment.newInstance();
            transaction.replace(R.id.fl_map_fragment_root, mMapFragment);
        } else {
            transaction.show(mMapFragment);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
        mBaseLl.mMapState = true;
        flMapFragmentRoot.setVisibility(View.VISIBLE);
        flMapFragmentRoot.post(new Runnable() {
            @Override
            public void run() {
                // 圆形动画的x坐标  位于View的中心
                int cx = (vMap.getLeft() + vMap.getRight()) / 2;

                //圆形动画的y坐标  位于View的中心
                int cy = (vMap.getTop() + vMap.getBottom()) / 2;

                //起始大小半径
                float startX = 0f;

                //结束大小半径 大小为图片对角线的一半
                float startY = (float) Math.sqrt(cx * cx + cy * cy);
                Animator animator = ViewAnimationUtils.createCircularReveal(flMapFragmentRoot, cx, cy, startX, startY);

                //在动画开始的地方速率改变比较慢,然后开始加速
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(600);
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });
    }

    /**
     * 关闭地图
     */
    public void closeMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        mBaseLl.mMapState = false;
        flMapFragmentRoot.post(new Runnable() {
            @Override
            public void run() {
                // 圆形动画的x坐标  位于View的中心
                int cx = (vMap.getLeft() + vMap.getRight()) / 2;

                //圆形动画的y坐标  位于View的中心
                int cy = (vMap.getTop() + vMap.getBottom()) / 2;

                //起始大小半径
                float startX = 0;

                //结束大小半径 大小为图片对角线的一半
                float startY = (float) Math.sqrt(cx * cx + cy * cy);
                Animator animator = ViewAnimationUtils.createCircularReveal(flMapFragmentRoot, cx, cy, startY, startX);

                //在动画开始的地方速率改变比较慢,然后开始加速
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(600);
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        flMapFragmentRoot.setVisibility(View.GONE);
                        transaction.hide(mMapFragment);
                        transaction.commitAllowingStateLoss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });
    }


    /**
     * 点击声音按钮
     */
    private void onVoiceClick() {
        if (SportSpData.getTtsEnable(SportingRunningOutdoorActivity.this)) {
            SportSpData.setTtsEnable(SportingRunningOutdoorActivity.this, false);
            vSportStateSound.setBackgroundResource(R.drawable.ic_voice_off_sporting);
            Toasty.info(SportingRunningOutdoorActivity.this, "语音关闭").show();
        } else {
            SportSpData.setTtsEnable(SportingRunningOutdoorActivity.this, true);
            vSportStateSound.setBackgroundResource(R.drawable.ic_voice_on_sporting);
            Toasty.info(SportingRunningOutdoorActivity.this, "语音打开").show();
        }
    }

    /**
     * 改变跑步状态
     *
     * @param msg
     */
    public void setRunningStatus(int msg) {
        switch (msg) {
            //暂停跑步
            case RunningService.CMD_PAUSE:
                mIsRunning = false;
                TTsU.playPauseRun(SportingRunningOutdoorActivity.this);
                break;
            //跑步继续
            case RunningService.CMD_CONTINUE:
                mIsRunning = true;
                TTsU.playContinueRun(SportingRunningOutdoorActivity.this);
                break;
            //跑步结束
            case RunningService.CMD_FINISH:
                break;
        }
        Intent intent = new Intent(this, RunningService.class);
        intent.putExtra("CMD", msg);
        startService(intent);
    }


    /**
     * 计时变大动画
     */
    private void animCountBig() {
        mCount--;
        tvCountNum.setText("" + mCount);
        mBigAnimation.reset();
        tvCountNum.startAnimation(mBigAnimation);
        if (mCount == 0) {
            mLocalHandler.sendEmptyMessageDelayed(MSG_ANIM_COUNT_FINISH, 1000);
        } else {
            mLocalHandler.sendEmptyMessageDelayed(MSG_ANIM_COUNT_BIG, 1000);
        }
    }

    /**
     * 倒计时动画结束
     */
    private void animCountFinish() {
        tvCountNum.clearAnimation();
        tvCountNum.setVisibility(View.GONE);
        mLocalHandler.removeMessages(MSG_ANIM_COUNT_FINISH);
        llCountNum.setVisibility(View.GONE);
//        TTsU.playStartRun(SportingRunningOutdoorActivity.this, 0);
    }

    /**
     * 更新心率相关的UI
     */
    private void updateHrView(int hr, int cadence) {
        int percent = hr * 100 / mUserDE.maxHr;
        tvHrState.setText(HrZoneU.getDescribeByHrPercent(percent));
        tvHr.setTextColor(ColorU.getColorByHeartbeat(percent));
        if (percent < 75) {
            percent -= 49;
        } else {
            percent -= 50;
        }

        if (percent < 0) {
            percent = 0;
        }
        if (percent > 49) {
            percent = 49;
        }
        tvHr.setText(hr + "");
        tvCadence.setText(cadence + "");
        startNeedleAnimation(percent);
    }

    /**
     * 开始指针动画
     *
     * @param percent
     */
    private void startNeedleAnimation(int percent) {
        AnimationSet animationSet = new AnimationSet(true);
        int end = getDegree(percent);
        RotateAnimation rotateAnimation = new RotateAnimation(mBeginDegree, end, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation.setDuration(1000);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);
        ivNeedle.startAnimation(animationSet);
        mBeginDegree = end;
    }

    /**
     * 获取指针角度
     *
     * @param percent
     * @return
     */
    private int getDegree(int percent) {
        double a = percent * 180 / 50;
        return (int) a;
    }

    /**
     * 暂停动画
     */
    private void pauseAnimation() {
        mFadeAnimationTime = 0;
        AnimatorSet set = new AnimatorSet();
        AnimatorSet set1 = new AnimatorSet();
        fadeAnimation(includeRunningBtns, 1, 0, 300, 0);
        fadeAnimation(includePausedBtns, 0, 1, 400, 100);
        set.playTogether(ObjectAnimator.ofFloat(flFinishBtnLayout, "translationX", 0, mScreenWidth / 4),
                ObjectAnimator.ofFloat(flFinishBtnLayout, "alpha", 0, 1));
        set1.playTogether(ObjectAnimator.ofFloat(flContinueBtnLayout, "translationX", 0, -mScreenWidth / 4),
                ObjectAnimator.ofFloat(flContinueBtnLayout, "alpha", 0, 1));
        set.setStartDelay(0);
        set1.setStartDelay(0);
        set.setDuration(500).start();
        set1.setDuration(500).start();
        includePausedBtns.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                includeRunningBtns.setVisibility(View.GONE);
            }
        }, 300);
    }

    /**
     * 继续动画
     */
    private void continueAnimation() {
        mFadeAnimationTime = 0;
        AnimatorSet set = new AnimatorSet();
        AnimatorSet set1 = new AnimatorSet();
        fadeAnimation(includeRunningBtns, 0, 1, 800, 200);
        fadeAnimation(includePausedBtns, 1, 0, 500, 0);
        set.playTogether(ObjectAnimator.ofFloat(flFinishBtnLayout, "translationX", mScreenWidth / 4, 0),
                ObjectAnimator.ofFloat(flFinishBtnLayout, "alpha", 1, 0));
        set1.playTogether(ObjectAnimator.ofFloat(flContinueBtnLayout, "translationX", -mScreenWidth / 4, 0),
                ObjectAnimator.ofFloat(flContinueBtnLayout, "alpha", 1, 0));
        set.setStartDelay(0);
        set1.setStartDelay(0);
        set.setDuration(500).start();
        set1.setDuration(500).start();
        includeRunningBtns.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                includePausedBtns.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 动画
     *
     * @param view
     * @param from
     * @param to
     * @param durationMillis
     * @param delayMillis
     */
    private void fadeAnimation(final View view, final float from, final float to, final long durationMillis,
                               final long delayMillis) {
        AlphaAnimation animation = new AlphaAnimation(from, to);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                mFadeAnimationTime++;
                if (mFadeAnimationTime == 2) {

                }
            }
        });
        view.startAnimation(animation);
    }

    /**
     * 设置结束页面
     */
    private void setPauseView(boolean pause) {
        if (pause) {
            flMain.setBackgroundResource(R.drawable.bg_sporting_pause);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.4f);
            alphaAnimation.setDuration(1000);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);

            llOtherData.setAnimation(alphaAnimation);
            llDurationData.setAnimation(alphaAnimation);

            alphaAnimation.start();
        } else {
            flMain.setBackgroundResource(R.drawable.bg_sporting);
            llOtherData.clearAnimation();
            llDurationData.clearAnimation();
        }
    }

    /**
     * 显示结束的对话框
     */
    private void showShortTimeFinishDialog() {
        mDialogBuilder.showChoiceDialog(SportingRunningOutdoorActivity.this,
                "结束本次锻炼？", "本次跑步未超过100米，无法保存记录，确定已经尽力了？", "确定");
        mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                setRunningStatus(RunningService.CMD_FINISH);
                T.showShort(SportingRunningOutdoorActivity.this, "跑步不足100米，不作记录");
                finish();
            }
        });
    }

    /**
     * 显示输入距离的对话框
     */
    private void showInputLengthDialog() {
        mDialogBuilder.showChoiceDialog(SportingRunningOutdoorActivity.this, "结束当前跑步？", null, "确定");
        mDialogBuilder.setListener(new DialogBuilder.ChoiceDialogListener() {
            @Override
            public void onConfirmBtnClick() {
                finishWorkout();
            }
        });
    }

    /**
     * 结束记录
     */
    private void finishWorkout() {
        setRunningStatus(RunningService.CMD_FINISH);

        //若是自己的锻炼记录，跳转到记录列表
        Bundle bundle = new Bundle();
        bundle.putInt("workoutId", 0);
        bundle.putString("workoutStartTime", mWorkoutDE.startTime);
        startActivity(WorkoutRunningOutdoorActivity.class, bundle);

        finish();
        if (BleManager.getBleManager().mBleConnectE != null &&
                BleManager.getBleManager().mBleConnectE.mIsConnected) {
            BleManager.getBleManager().mBleConnectE.reStartSync();
        }
    }

}
