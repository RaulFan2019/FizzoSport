package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.run.ReadyRunActivity;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.ui.common.SwitchView;

/**
 * Created by Administrator on 2016/11/23.
 */

public class WorkOutSetActivity extends BaseActivity {

    @BindView(R.id.sv_sport_tts)
    SwitchView mSportTtsSv;
    @BindView(R.id.ll_set)
    LinearLayout mSetLl;
    @BindView(R.id.sv_alert)
    SwitchView mAlertSv;//报警相关checkbox
    @BindView(R.id.tv_curr_hr)
    TextView mCurrTv;//实时心率播报频率

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_set;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_back, R.id.rl_curr_hr})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_curr_hr:
                startActivity(CurrHrFreqSettingsActivity.class);
                break;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mSportTtsSv.setOpened(SportSpData.getTtsEnable(this));
        if (SportSpData.getTtsEnable(this)){
            mSetLl.setVisibility(View.VISIBLE);
        }else {
            mSetLl.setVisibility(View.GONE);
        }

        mSportTtsSv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                SportSpData.setTtsEnable(WorkOutSetActivity.this, true);
                mSportTtsSv.setOpened(true);
                mSetLl.setVisibility(View.VISIBLE);
            }

            @Override
            public void toggleToOff(View view) {
                SportSpData.setTtsEnable(WorkOutSetActivity.this, false);
                mSportTtsSv.setOpened(false);
                mSetLl.setVisibility(View.GONE);
            }
        });


        mAlertSv.setOpened(SportSpData.getAlertTtsEnable(this));

        mAlertSv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                SportSpData.setAlertTtsEnable(WorkOutSetActivity.this, true);
                mAlertSv.setOpened(true);
            }

            @Override
            public void toggleToOff(View view) {
                SportSpData.setAlertTtsEnable(WorkOutSetActivity.this, false);
                mAlertSv.setOpened(false);
            }
        });


    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SportSpData.getCurrHrFreqTts(WorkOutSetActivity.this)  != 999 ) {
            mCurrTv.setText("每" + SportSpData.getCurrHrFreqTts(WorkOutSetActivity.this) + "分钟");
        }else {
            mCurrTv.setText("不提醒");
        }

    }

    @Override
    protected void causeGC() {

    }
}
