package cn.hwh.sports.activity.monitor;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Administrator on 2016/12/4.
 */

public class RecordSleepActivity extends BaseActivity {
    /* contains */
    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;

    /* view */
    @BindView(R.id.tv_start_time_value)
    TextView mStartTimeTv;
    @BindView(R.id.tv_end_time_value)
    TextView mEndTimeTv;

    DialogBuilder mDialog;

    /* data */
    private Callback.Cancelable mPostCancel;
    private boolean mPostEnable;

    private String mStartTime;
    private String mEndTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_sleep;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            mDialog.dismiss();
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(RecordSleepActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    T.showShort(RecordSleepActivity.this, "已添加");
                    finish();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.tv_sleep_record_save, R.id.ll_start_time_value, R.id.ll_end_time_value})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_sleep_record_save:
                addSleepRecord();
                break;
            case R.id.ll_start_time_value:
                showStartDateDialog();
                break;
            case R.id.ll_end_time_value:
                showEndDateDialog();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialog = new DialogBuilder();
        mStartTime = TimeU.nowTime();
        mEndTime = TimeU.nowTime();
        mPostEnable = true;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mStartTimeTv.setText(formatDate(mStartTime,TimeU.refreshTime()));
        mEndTimeTv.setText(formatDate(mEndTime,TimeU.refreshTime()));
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void causeGC() {
        if (mPostCancel != null && !mPostCancel.isCancelled()) {
            mPostCancel.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
        mDialog.dismiss();
    }

    /**
     * 开始时间日期
     */
    private void showStartDateDialog() {
        mDialog.showDateDialog(this, "日期", "确定");
        mDialog.setListener(new DialogBuilder.DateDialogListener() {
            @Override
            public void onConfirmBtnClick(String date) {
                mStartTime = date+" ";
                showStartTimeDialog();
            }
        });
    }

    /**
     * 开始时间 时间
     */
    private void showStartTimeDialog() {
        mDialog.showTimeDialog(this, "时间", "确定");
        mDialog.setListener(new DialogBuilder.TimeDialogListener() {
            @Override
            public void onConfirmBtnClick(String time) {
                mStartTime += time;
                mStartTimeTv.setText(formatDate(mStartTime,time));
            }
        });
    }

    /**
     * 结束时间 日期
     */
    private void showEndDateDialog(){
        mDialog.showDateDialog(this, "日期", "确定");
        mDialog.setListener(new DialogBuilder.DateDialogListener() {
            @Override
            public void onConfirmBtnClick(String date) {
                mEndTime = date+" ";
                showEndTimeDialog();
            }
        });
    }

    /**
     * 结束时间 时间
     */
    private void showEndTimeDialog(){
        mDialog.showTimeDialog(this, "时间", "确定");
        mDialog.setListener(new DialogBuilder.TimeDialogListener() {
            @Override
            public void onConfirmBtnClick(String time) {
                mEndTime += time;
                mEndTimeTv.setText(formatDate(mEndTime,time));
            }
        });
    }

    /**
     * 将yyyy-MM-dd HH:mm 显示成 2016年12月04日 周日 23:00格式
     *
     * @param date yyyy-MM-dd 格式日期
     * @return
     */
    private String formatDate(String date,String time) {
        String lWeekDay = TimeU.getWeekByDayStr(date);
        String lShowDay = TimeU.formatDateStrShow(date,TimeU.FORMAT_TYPE_3,"yyyy年MM月dd日 "+lWeekDay +" "+time);
        return lShowDay;
    }


    private void addSleepRecord() {

        if(!mPostEnable){
            return;
        }

        if(TimeU.compareDate(mStartTime,mEndTime) >= 0){
            T.showShort(RecordSleepActivity.this,"开始时间不能晚于或等于结束时间");
            return;
        }

        long dateDiff = TimeU.getTimeDiff(mStartTime,mEndTime,TimeU.FORMAT_TYPE_2);
        if(Math.abs( dateDiff ) > 24*60*60){
            T.showShort(RecordSleepActivity.this,"开始时间与结束时间差不能超过24小时");
            return;
        }

        mPostEnable = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildAddRecordSleepParamRP(RecordSleepActivity.this, UrlConfig.URL_ADD_DAY_SLEEP,
                        LocalApplication.getInstance().mLoginUser.userId, mStartTime, mEndTime);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_POST_OK;
                                msg.obj = result.result;
                                mPostHandler.sendMessage(msg);
                            } else {
                                Message msg = new Message();
                                msg.what = MSG_POST_ERROR;
                                msg.obj = result.errormsg;
                                mPostHandler.sendMessage(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mPostHandler != null) {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                            mPostHandler.sendMessage(msg);
                        }
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
        mDialog.showProgressDialog(RecordSleepActivity.this, "正在保存...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mPostCancel != null && !mPostCancel.isCancelled()) {
                    mPostCancel.cancel();
                }
            }
        });
    }
}
