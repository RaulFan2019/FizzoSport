package cn.hwh.sports.activity.monitor;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
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
 * Created by Administrator on 2016/12/2.
 */

public class RecordWeightActivity extends BaseActivity {
    /* contains */
    private static final String TAG = "RecordWeightActivity";
    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;

    /* view */
    @BindView(R.id.tv_date_value)
    TextView mDateValueTv;
    @BindView(R.id.tv_weight_value)
    TextView mWeightValueTv;
    @BindView(R.id.tv_fat_rate_value)
    TextView mFatRateValueTv;

    DialogBuilder mDialog;

    /* data */
    private float mWeightValue;
    private float mFatRateValue;
    private String mWeightDate;

    private Callback.Cancelable mPostCancel;
    private boolean mPostEnable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_weight;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            mDialog.dismiss();

            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(RecordWeightActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    T.showShort(RecordWeightActivity.this, "保存成功！");
                    finish();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.ll_date_value, R.id.ll_weight_value,
            R.id.ll_fat_rate_value, R.id.tv_weight_record_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_date_value:
                showDateDialog();
                break;
            case R.id.ll_weight_value:
                showWeightDialog();
                break;
            case R.id.ll_fat_rate_value:
                showFatRatioDialog();
                break;
            //save
            case R.id.tv_weight_record_save:
                addRecord();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialog = new DialogBuilder();
        mWeightValue = LocalApplication.getInstance().getLoginUser(this).targetWeight;
        mFatRateValue = LocalApplication.getInstance().getLoginUser(this).targetFatRate;
        mWeightDate = TimeU.nowDay();
        mPostEnable = true;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mWeightValueTv.setText(mWeightValue +"");
        mFatRateValueTv.setText(mFatRateValue + "");
        mDateValueTv.setText(formatDate(TimeU.nowDay()));
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
            mPostHandler = null;
        }
        mDialog.dismiss();
    }

    /**
     * 显示日期弹框
     */
    private void showDateDialog() {
        mDialog.showDateDialog(this, "日期", "确定");
        mDialog.setListener(new DialogBuilder.DateDialogListener() {
            @Override
            public void onConfirmBtnClick(String date) {
                mWeightDate = date;
                mDateValueTv.setText(formatDate(date));
            }
        });
    }

    /**
     * 体重弹框
     */
    private void showWeightDialog() {
        mDialog.showEditDialog(this, "体重", "确定", InputType.TYPE_NUMBER_FLAG_DECIMAL, String.valueOf(mWeightValue));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.equals("0")
                        && mInputText.matches("^[0-9]+\\.?[0-9]{0,2}$")) {
                    if (Float.valueOf(mInputText) > 335 || Float.valueOf(mInputText) < 35) {
                        T.showShort(RecordWeightActivity.this, "请输入正确的体重");
                        return;
                    }
                    mWeightValue = Float.valueOf(mInputText);
                    mWeightValueTv.setText(String.valueOf(mWeightValue));
                } else {
                    T.showShort(RecordWeightActivity.this, "请输入正确的体重");
                }
            }
        });
    }

    /**
     * 体脂率弹框
     */
    private void showFatRatioDialog() {
        mDialog.showEditDialog(this, "体脂率", "保存", InputType.TYPE_NUMBER_FLAG_DECIMAL, String.valueOf(mFatRateValue));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.equals("0") && mInputText.matches("^[0-9]+\\.?[0-9]{0,2}$")) {
                    if (Float.valueOf(mInputText) > 100) {
                        T.showShort(RecordWeightActivity.this, "请输入正确的体脂率");
                        return;
                    }

                    mFatRateValue = Float.valueOf(mInputText);
                    mFatRateValueTv.setText(String.valueOf(mFatRateValue));
                } else {
                    T.showShort(RecordWeightActivity.this, "请输入正确的体脂率");
                }
            }
        });
    }

    /**
     * 将yyyy-MM-dd 显示成 12月04日 周日 格式
     *
     * @param date yyyy-MM-dd 格式日期
     * @return
     */
    private String formatDate(String date) {
        String lWeekDay = TimeU.getWeekByDayStr(date);
        String lShowDay = TimeU.formatDateStrShow(date, TimeU.FORMAT_TYPE_3, "MM月dd日") + " " + lWeekDay;
        return lShowDay;
    }

    /**
     * 保存数据至服务器
     */
    private void addRecord() {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildAddRecordWeightParamRP(RecordWeightActivity.this,
                        UrlConfig.URL_ADD_DAY_WEIGHT, LocalApplication.getInstance().mLoginUser.userId,
                        mWeightDate, mWeightValue, mFatRateValue);
                mPostCancel = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
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

        mDialog.showProgressDialog(RecordWeightActivity.this, "正在保存...", true);
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
