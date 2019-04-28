package cn.hwh.sports.fragment.workout;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.adapter.WorkoutRunningPaceAdapter;
import cn.hwh.sports.data.db.LengthSplitDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class WorkoutRunningOutdoorPaceFragment extends BaseFragment {


    /* local view */
    @BindView(R.id.tv_pace_avg)
    TextView tvPaceAvg;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_pace_max)
    TextView tvPaceMax;
    @BindView(R.id.list_pace)
    ListView listPace;

    /* local data */
    private String mWorkoutStartTime;
    private WorkoutDE mWorkoutDe;
    private UserDE mUserDe;
    private List<LengthSplitDE> mSplits = new ArrayList<>();
    private int mPaceMin;
    private int mPaceMax;

    private Typeface typeFace;//字体
    private WorkoutRunningPaceAdapter mAdapter;

    public static WorkoutRunningOutdoorPaceFragment newInstance() {
        WorkoutRunningOutdoorPaceFragment fragment = new WorkoutRunningOutdoorPaceFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout_running_outdoor_pace;
    }

    @Override
    protected void initParams() {
        mUserDe = LocalApplication.getInstance().getLoginUser(getActivity());
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");

        tvPaceAvg.setTypeface(typeFace);
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        if (mWorkoutDe != null) {
            updateViews();
        }
    }

    @Override
    protected void onInVisible() {

    }

    /**
     * activity 调用更新
     */
    public void updateViewByActivity(final String workoutStartTime) {
        mWorkoutStartTime = workoutStartTime;
        updateWorkoutData();
        updateViews();
    }

    /**
     * 更新记录相关数据
     */
    private void updateWorkoutData() {
        mWorkoutDe = WorkoutDBData.getWorkoutByStartTime(mUserDe.userId, mWorkoutStartTime);
        mSplits = LengthSplitDBData.getAllSplitInWorkout(mWorkoutStartTime);
        mPaceMax = (int) mSplits.get(0).duration;
        mPaceMin = (int) mSplits.get(0).duration;
        for (int i = 0, size = mSplits.size() - 1; i < size; i++) {
            int mCurrPace = (int) mSplits.get(i).duration;
            if (mCurrPace > mPaceMax) {
                mPaceMax = mCurrPace;
            }
            if (mCurrPace < mPaceMin) {
                mPaceMin = mCurrPace;
            }
        }

    }


    /**
     * 更新页面
     */
    private void updateViews() {
        tvPaceAvg.setText(TimeU.formatSecondsToPace((long) (mWorkoutDe.duration * 1000 / mWorkoutDe.length)));
        tvDuration.setText(LengthUtils.formatLength(mWorkoutDe.length) + "公里用时"
                + (int) mWorkoutDe.duration / 60 + "分钟");
        tvPaceMax.setText("最快配速" + TimeU.formatSecondsToPace(mPaceMin));
        mAdapter = new WorkoutRunningPaceAdapter(getActivity(),mSplits,mPaceMax,mPaceMin);
        listPace.setAdapter(mAdapter);
    }

}
