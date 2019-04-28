package cn.hwh.sports.activity.workout;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.rey.material.widget.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.WorkoutTrendAdapter;

import cn.hwh.sports.adapter.WorkoutTrendAdapter.Tab;

/**
 * Created by Raul.Fan on 2017/4/26.
 */

public class WorkoutTrendActivity extends BaseActivity {


    @BindView(R.id.tbi_group)
    TabPageIndicator mTpi;
    @BindView(R.id.vp)
    ViewPager vp;

    private WorkoutTrendAdapter mAdapter;
    private FragmentManager fragmentManager;

    private Tab[] mItems;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_trend;
    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();

        mItems = new Tab[]{Tab.WEEK, Tab.MOUTH,Tab.YEAR};
        mAdapter = new WorkoutTrendAdapter(getSupportFragmentManager(),mItems);
        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(2);
        mTpi.setViewPager(vp);
        vp.setCurrentItem(0);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

}
