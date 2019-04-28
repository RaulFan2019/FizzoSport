package cn.hwh.sports.activity.run;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.EffortExplainAdapter;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.adapter.EffortExplainAE;
import cn.hwh.sports.ui.indicator.CirclePageIndicator;
import cn.hwh.sports.ui.indicator.EffortExplainIndicator;

/**
 * Created by Raul.Fan on 2017/2/20.
 * 跑前热身
 */
public class RunningIndoorWarmUpActivity extends BaseActivity {


    /* local view */
    @BindView(R.id.vp)
    ViewPager mVp;
    @BindView(R.id.indicator)
    EffortExplainIndicator mIndicator;

    EffortExplainAdapter mAdapter;

    /* local data */
    private List<EffortExplainAE> mExplainList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_running_indoor_warm_up;
    }


    @OnClick(R.id.btn_start)
    public void onClick() {
        startActivity(RunningIndoorActivity.class);
        finish();
    }

    @Override
    protected void initData() {
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_warm_title_1), R.drawable.bg_warm_1, getString(R.string.effort_warm_info_1),"20组"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_warm_title_2), R.drawable.bg_warm_2, getString(R.string.effort_warm_info_2),"20组"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_warm_title_3), R.drawable.bg_warm_3, getString(R.string.effort_warm_info_3),"20组"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_warm_title_4), R.drawable.bg_warm_4, getString(R.string.effort_warm_info_4),"20组"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_warm_title_5), R.drawable.bg_warm_5, getString(R.string.effort_warm_info_5),"20组"));

        mAdapter = new EffortExplainAdapter(this,mExplainList);
        mVp.setAdapter(mAdapter);
        mVp.setOffscreenPageLimit(0);
        mIndicator.setViewPager(mVp);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void doMyCreate() {
        if (SportSpData.getTtsEnable(this)) {
            LocalApplication.getInstance().getUscTtsUtil(RunningIndoorWarmUpActivity.this).showVoice("先来做几组跑前拉伸动作");
        }
    }

    @Override
    protected void causeGC() {
        mExplainList.clear();
    }

}
