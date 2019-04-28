package cn.hwh.sports.fragment.workout;

/**
 * Created by Raul.Fan on 2017/4/26.
 */

public class WorkoutTrendYearFragment extends WorkoutTrendFragment {


    public static WorkoutTrendYearFragment newInstance() {
        WorkoutTrendYearFragment fragment = new WorkoutTrendYearFragment();
        return fragment;
    }

    @Override
    protected int getType() {
        return 3;
    }
}
