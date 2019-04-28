package cn.hwh.sports.fragment.workout;

/**
 * Created by Raul.Fan on 2017/4/26.
 */

public class WorkoutTrendMouthFragment extends WorkoutTrendFragment {


    public static WorkoutTrendMouthFragment newInstance() {
        WorkoutTrendMouthFragment fragment = new WorkoutTrendMouthFragment();
        return fragment;
    }

    @Override
    protected int getType() {
        return 2;
    }
}
