package cn.hwh.sports.activity.sporting;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.utils.StringU;

/**
 * Created by Raul.Fan on 2017/4/24.
 */

public class SportingSubmitActivity extends BaseActivity {


    @BindView(R.id.ll_length)
    LinearLayout mLengthLl;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.et_length)
    EditText mLengthEt;

    private String mStartTime;

    private float mLength = 0;
    private int mFeel = 3;


    private WorkoutDE mWorkoutDe;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sporting_submit;
    }

    @OnClick(R.id.btn_save)
    public void onClick() {
        submit();

    }

    @Override
    protected void initData() {
        mStartTime = getIntent().getExtras().getString("startTime");
        mWorkoutDe = WorkoutDBData.getWorkoutByStartTime(LocalApplication.getInstance().getLoginUser(SportingSubmitActivity.this).userId, mStartTime);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mWorkoutDe.type == SportEnum.EffortType.RUNNING_INDOOR) {
            mLengthLl.setVisibility(View.VISIBLE);
        } else {
            mLengthLl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    private void submit() {
        if (mWorkoutDe.type == SportEnum.EffortType.RUNNING_INDOOR) {
            String inputText = mLengthEt.getText().toString();
            String error = StringU.checkRunningLengthError(SportingSubmitActivity.this, inputText);
            if (!error.equals("")) {
                Toasty.error(SportingSubmitActivity.this, error).show();
                return;
            }
            mLength = Float.valueOf(inputText);
        }

        mFeel = (int) mRatingBar.getRating();
        mWorkoutDe.length = mLength * 1000;
        mWorkoutDe.feel = mFeel;
        WorkoutDBData.update(mWorkoutDe);
        UploadDBData.finishWorkout(mWorkoutDe);
        finish();
    }

}
