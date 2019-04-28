package cn.hwh.sports.fragment.workout;

/**
 * Created by Raul.Fan on 2017/4/26.
 */

public class WorkoutTrendWeekFragment extends WorkoutTrendFragment {


    public static WorkoutTrendWeekFragment newInstance() {
        WorkoutTrendWeekFragment fragment = new WorkoutTrendWeekFragment();
        return fragment;
    }

    @Override
    protected int getType() {
        return 1;
    }
}
