package cn.hwh.sports.activity.workout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;

/**
 * Created by machenike on 2017/6/12 0012.
 */

public class WorkoutRenameActivity extends BaseActivity {


    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;

    @BindView(R.id.et_name)
    EditText etName;

    private int mWorkoutId;
    private String mWorkoutName;
    private String mWorkoutStartTime;
    private Callback.Cancelable mCancelable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_rename;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_POST_ERROR:
                    Toasty.error(WorkoutRenameActivity.this,(String)msg.obj).show();
                    break;
                case MSG_POST_OK:
                    WorkoutDE workout = WorkoutDBData.getWorkoutByStartTime(LocalApplication.getInstance().getLoginUser(WorkoutRenameActivity.this).userId,mWorkoutStartTime);
                    workout.name = mWorkoutName;
                    WorkoutDBData.update(workout);
                    Toasty.success(WorkoutRenameActivity.this,"修改成功").show();
                    finish();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                checkName();
                break;
        }
    }

    @Override
    protected void initData() {
        mWorkoutId = getIntent().getExtras().getInt("id");
        mWorkoutName = getIntent().getExtras().getString("name");
        mWorkoutStartTime = getIntent().getExtras().getString("startTime");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        etName.setText(mWorkoutName);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        if (mCancelable != null){
            mCancelable.cancel();
        }
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void checkName(){
        mWorkoutName = etName.getText().toString().trim();
        if (mWorkoutName != null && !mWorkoutName.equals("")){
            postRename();
        }else {
            Toasty.error(WorkoutRenameActivity.this,"名称不能是空").show();
        }
    }

    private void postRename(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUpdateWorkoutName(WorkoutRenameActivity.this,
                        UrlConfig.URL_UPDATE_WORKOUT_NAME,mWorkoutId,mWorkoutName);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE){
                            mHandler.sendEmptyMessage(MSG_POST_OK);
                        }else {
                            Message msg = mHandler.obtainMessage();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = MSG_POST_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });


    }

}
