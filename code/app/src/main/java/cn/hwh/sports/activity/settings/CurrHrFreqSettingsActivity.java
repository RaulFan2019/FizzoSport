package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.data.sp.SportSpData;

/**
 * Created by Raul.Fan on 2017/5/12.
 */

public class CurrHrFreqSettingsActivity extends BaseActivity {


    @BindView(R.id.v_select_1)
    View vSelect1;
    @BindView(R.id.v_select_2)
    View vSelect2;
    @BindView(R.id.v_select_5)
    View vSelect5;
    @BindView(R.id.v_select_never)
    View vSelectNever;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_curr_hr_freq_set;
    }

    @OnClick({R.id.btn_back, R.id.ll_1_mint, R.id.ll_2_mint, R.id.ll_5_mint, R.id.ll_never})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_1_mint:
                SportSpData.setCurrHrFreqTts(CurrHrFreqSettingsActivity.this,1);
                selectFreq(1);
                break;
            case R.id.ll_2_mint:
                SportSpData.setCurrHrFreqTts(CurrHrFreqSettingsActivity.this,2);
                selectFreq(2);
                break;
            case R.id.ll_5_mint:
                SportSpData.setCurrHrFreqTts(CurrHrFreqSettingsActivity.this,5);
                selectFreq(5);
                break;
            case R.id.ll_never:
                SportSpData.setCurrHrFreqTts(CurrHrFreqSettingsActivity.this,999);
                selectFreq(999);
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        selectFreq(SportSpData.getCurrHrFreqTts(CurrHrFreqSettingsActivity.this));
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    private void selectFreq(int freq){
        reSetSelect();
        if (freq == 1){
            vSelect1.setVisibility(View.VISIBLE);
        }else if (freq == 2){
            vSelect2.setVisibility(View.VISIBLE);
        }else if (freq == 5){
            vSelect5.setVisibility(View.VISIBLE);
        }else {
            vSelectNever.setVisibility(View.VISIBLE);
        }
    }

    private void reSetSelect(){
        vSelect1.setVisibility(View.GONE);
        vSelect2.setVisibility(View.GONE);
        vSelect5.setVisibility(View.GONE);
        vSelectNever.setVisibility(View.GONE);
    }

}

