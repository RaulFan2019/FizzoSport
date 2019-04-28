package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;

/**
 * Created by Raul.Fan on 2017/1/12.
 */
public class SystemSettingsActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_settings;
    }

    @OnClick({R.id.btn_back, R.id.rl_about_us, R.id.rl_advice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }


}
