package cn.hwh.sports.activity.run;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.sporting.RunningIndoorActivityV2;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.ui.common.SlideUnlockView;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/27.
 */

public class FreeEffortLockActivity extends Activity {

    /* view */
    @BindView(R.id.tv_effort)
    TextView mEffortTv;
    @BindView(R.id.tv_percent)
    TextView mPercentTv;
    @BindView(R.id.tv_hr)
    TextView mHrTv;
    @BindView(R.id.tv_duration)
    TextView mDurationTv;
    @BindView(R.id.slideUnlockView)
    SlideUnlockView mSlideLock;

    private UserDE mUserDE;

    //声明键盘管理器
    KeyguardManager mKeyguardManager = null;
    //声明键盘锁
    private KeyguardManager.KeyguardLock mKeyguardLock = null;


    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            int percent = event.hr * 100 / mUserDE.maxHr;
            mHrTv.setText(event.hr + "");
            mEffortTv.setText(percent + "");
            mPercentTv.setVisibility(View.VISIBLE);
        } else if (event.msg == BleManager.MSG_DISCONNECT) {
            mHrTv.setText("- -");
            mEffortTv.setText("- -");
            mPercentTv.setVisibility(View.GONE);
        } else if (event.msg == BleManager.MSG_CONNECTED) {
        }
    }

    /**
     * 时间发生改变
     *
     * @param eventEffortTime
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEventBus(RunningTimeEE eventEffortTime) {
        mDurationTv.setText(TimeU.formatSecondsToShortHourTime(eventEffortTime.time));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_free_effort);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        mUserDE = LocalApplication.getInstance().getLoginUser(FreeEffortLockActivity.this);
        final WorkoutDE workout = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        mSlideLock.setOnUnLockListener(new SlideUnlockView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLock) {
                // 如果是true，证明解锁
                if (unLock) {
                    //初始化键盘锁，可以锁定或解开键盘锁
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                    //禁用显示键盘锁定
                    mKeyguardLock.disableKeyguard();
                    if (workout.type == SportEnum.EffortType.RUNNING_INDOOR){
                        Intent freeI = new Intent(FreeEffortLockActivity.this, RunningIndoorActivityV2.class);
                        startActivity(freeI);
                    }else {
                        Intent freeI = new Intent(FreeEffortLockActivity.this, SportingIndoorActivity.class);
                        startActivity(freeI);
                    }

                    finish();
                }
            }
        });

        if (mDurationTv == null) {
            return;
        }
        // 时间
        mDurationTv.setText(TimeU.formatSecondsToShortHourTime(workout.duration));

    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onDestroy() {
        super.onDestroy();

    }

}
