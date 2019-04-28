package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.MyBuildConfig;

/**
 * Created by Raul.Fan on 2017/1/12.
 */

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView mVersionTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings_about_us;
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
        mVersionTv.setText(MyBuildConfig.Version);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }


}
