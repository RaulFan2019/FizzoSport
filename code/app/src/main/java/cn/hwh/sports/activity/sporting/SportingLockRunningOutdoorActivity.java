package cn.hwh.sports.activity.sporting;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import cn.hwh.sports.activity.run.FreeEffortLockActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.RunningLocationEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.ui.common.SlideUnlockView;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class SportingLockRunningOutdoorActivity extends Activity {


    @BindView(R.id.tv_hr)
    TextView tvHr;
    @BindView(R.id.tv_length)
    TextView tvLength;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.slideUnlockView)
    SlideUnlockView slideUnlockView;

    private UserDE mUserDE;

    //声明键盘管理器
    KeyguardManager mKeyguardManager = null;
    //声明键盘锁
    private KeyguardManager.KeyguardLock mKeyguardLock = null;

    private Typeface mTypeFace;//数字字体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sporting_lock_running_out_door);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        mUserDE = LocalApplication.getInstance().getLoginUser(SportingLockRunningOutdoorActivity.this);
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
        slideUnlockView.setOnUnLockListener(new SlideUnlockView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLock) {
                // 如果是true，证明解锁
                if (unLock) {
                    //初始化键盘锁，可以锁定或解开键盘锁
                    mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                    //禁用显示键盘锁定
                    mKeyguardLock.disableKeyguard();
                    Intent freeI = new Intent(SportingLockRunningOutdoorActivity.this, SportingRunningOutdoorActivity.class);
                    startActivity(freeI);
                    finish();
                }
            }
        });

        if (tvDuration == null) {
            return;
        }
        tvLength.setTypeface(mTypeFace);
        tvDuration.setTypeface(mTypeFace);
        tvHr.setTypeface(mTypeFace);
        // 时间
        tvDuration.setText(workout.duration/60 + "");
        tvLength.setText(LengthUtils.formatLength(workout.length));
    }

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
            tvHr.setText(event.hr + "");
            tvHr.setTextColor(ColorU.getColorByHeartbeat(event.hr * 100 /mUserDE.maxHr));
        } else if (event.msg == BleManager.MSG_DISCONNECT) {
            tvHr.setText("- -");

        } else if (event.msg == BleManager.MSG_CONNECTED) {
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
            tvDuration.setText(time/60 + "");
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
}
