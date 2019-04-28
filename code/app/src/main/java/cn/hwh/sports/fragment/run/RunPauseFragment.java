package cn.hwh.sports.fragment.run;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class RunPauseFragment extends BaseFragment {


    @BindView(R.id.tv_hr)
    TextView tvHr;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_length)
    TextView tvLength;
    @BindView(R.id.tv_step_count)
    TextView tvStepCount;
    @BindView(R.id.tv_cadence)
    TextView tvCadence;
    @BindView(R.id.tv_pace)
    TextView tvPace;


    /* local data */
    private Typeface mTypeFace;//数字字体
    private UserDE mUser;

    /* 构造函数 */
    public static RunPauseFragment newInstance() {
        RunPauseFragment fragment = new RunPauseFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_run_outdoor_pause;
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
//            updateHrView(event.hr,event.cadence);
            //连接断开信息
        } else if (event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_CONNECT_FAIL) {
            tvHr.setText("- -");
        }
    }

    @Override
    protected void initParams() {
        mUser = LocalApplication.getInstance().getLoginUser(getActivity());

        mTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        tvDuration.setTypeface(mTypeFace);
        tvHr.setTypeface(mTypeFace);
        tvLength.setTypeface(mTypeFace);
        tvStepCount.setTypeface(mTypeFace);
        tvCadence.setTypeface(mTypeFace);
        tvPace.setTypeface(mTypeFace);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onVisible() {
        WorkoutDE workout = WorkoutDBData.getUnFinishWorkout(mUser.userId);
        if (workout != null) {
            tvDuration.setText(TimeU.formatSecondsToShortHourTime(workout.duration));
            tvLength.setText(LengthUtils.formatLength(workout.length));
            tvStepCount.setText(workout.endStep);
        }
    }

    @Override
    protected void onInVisible() {

    }

}
