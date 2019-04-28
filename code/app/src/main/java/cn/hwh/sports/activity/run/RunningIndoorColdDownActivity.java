package cn.hwh.sports.activity.run;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.EffortExplainAdapter;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.adapter.EffortExplainAE;
import cn.hwh.sports.ui.indicator.EffortExplainIndicator;

/**
 * Created by Raul.Fan on 2017/2/20.
 * 跑前热身
 */
public class RunningIndoorColdDownActivity extends BaseActivity {


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
        return R.layout.activity_running_indoor_cold_down;
    }


    @OnClick(R.id.btn_start)
    public void onClick() {
        finish();
    }

    @Override
    protected void initData() {
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_1), R.drawable.bg_cold_1, getString(R.string.effort_cold_info_1),"30秒"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_2), R.drawable.bg_cold_2, getString(R.string.effort_cold_info_2),"30秒"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_3), R.drawable.bg_cold_3, getString(R.string.effort_cold_info_3),"30秒"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_4), R.drawable.bg_cold_4, getString(R.string.effort_cold_info_4),"30秒"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_5), R.drawable.bg_cold_5, getString(R.string.effort_cold_info_5),"30秒"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_6), R.drawable.bg_cold_6, getString(R.string.effort_cold_info_6),"30秒"));
        mExplainList.add(new EffortExplainAE(getString(R.string.effort_cold_title_7), R.drawable.bg_cold_7, getString(R.string.effort_cold_info_7),"30秒"));

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
            LocalApplication.getInstance().getUscTtsUtil(RunningIndoorColdDownActivity.this).showVoice("来做几组跑后拉伸动作吧");
        }
    }

    @Override
    protected void causeGC() {
        mExplainList.clear();
    }

}
