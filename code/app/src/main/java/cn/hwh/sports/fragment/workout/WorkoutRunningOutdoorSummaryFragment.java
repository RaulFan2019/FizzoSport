package cn.hwh.sports.fragment.workout;

import cn.hwh.sports.R;
import cn.hwh.sports.fragment.BaseFragment;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class WorkoutRunningOutdoorSummaryFragment extends BaseFragment {


    public static WorkoutRunningOutdoorSummaryFragment newInstance() {
        WorkoutRunningOutdoorSummaryFragment fragment = new WorkoutRunningOutdoorSummaryFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout_running_outdoor_summary;
    }

    @Override
    protected void initParams() {

    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {

    }

    @Override
    protected void onInVisible() {

    }

    /**
     * activity 调用更新
     */
    public void updateViewByActivity(final String workoutStartTime){

    }

}
