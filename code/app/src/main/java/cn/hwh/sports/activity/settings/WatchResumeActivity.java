package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.entity.event.SyncWatchWorkoutEE;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2017/4/24.
 */

public class WatchResumeActivity extends BaseActivity {


    private static final int MSG_UPDATE_TIMER = 0x01;

    @BindView(R.id.v_update_outside)
    View mUpdateOutsideV;
    @BindView(R.id.v_update_inside)
    View mUpdateInsideV;
    @BindView(R.id.tv_percent)
    TextView mPercentTv;
    @BindView(R.id.tv_percent_time)
    TextView mPercentTimeTv;

    private RotateAnimation mRotateOutsideAnim;
    private RotateAnimation mRotateInsideAnim;


    private int mTimer = 150;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_watch_resume;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPDATE_TIMER) {
                mTimer--;
                mPercentTv.setText((int) (100 - (100 * mTimer / 150)) + "%");
                mPercentTimeTv.setText("预计剩余：" + mTimer + "s");
                if (mTimer > 0) {
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, 1000);
                }else {
                    ActivityStackManager.getAppManager().finishAllWatchUpdateActivity();
                    finish();
                }
            }
        }
    };


    /**
     * 手表同步手机完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
        Log.v("onBleEventBus","event.msg:" + event.msg);
        //同步完成
        if (event.msg == BleManager.MSG_CONNECTED) {
            Toasty.success(WatchResumeActivity.this,"手表已恢复").show();
            ActivityStackManager.getAppManager().finishAllWatchUpdateActivity();
            finish();
        }
    }


    @Override
    protected void initData() {
        mRotateOutsideAnim = (RotateAnimation) AnimationUtils.loadAnimation(WatchResumeActivity.this, R.anim.rotating);
        mRotateInsideAnim = (RotateAnimation) AnimationUtils.loadAnimation(WatchResumeActivity.this, R.anim.rotating_contrary);
        mRotateOutsideAnim.setInterpolator(new LinearInterpolator());//不停顿
        mRotateInsideAnim.setInterpolator(new LinearInterpolator());//不停顿
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {
        mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
        mUpdateOutsideV.setAnimation(mRotateOutsideAnim);
        mUpdateInsideV.setAnimation(mRotateInsideAnim);
        mRotateOutsideAnim.start();
        mRotateInsideAnim.start();
        ActivityStackManager.getAppManager().addWatchUpdateActivity(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);;
        mHandler.removeCallbacksAndMessages(null);
        mRotateOutsideAnim.cancel();
        mRotateInsideAnim.cancel();
        LocalApplication.getInstance().showWatchResumeActivity = false;
    }

}
