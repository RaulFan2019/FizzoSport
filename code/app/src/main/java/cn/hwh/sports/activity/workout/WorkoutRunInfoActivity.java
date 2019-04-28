package cn.hwh.sports.activity.workout;

import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;

/**
 * Created by Raul.Fan on 2016/12/22.
 */
public class WorkoutRunInfoActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_info_run;
    }

    @OnClick({R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            //回退
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
