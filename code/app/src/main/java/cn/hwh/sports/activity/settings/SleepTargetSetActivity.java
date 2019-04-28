package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.pickviewlibrary.OptionsPickerView;
import cn.hwh.pickviewlibrary.TimePickerView;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.SleepTargetSetRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Administrator on 2016/11/20.
 */

public class SleepTargetSetActivity extends BaseActivity {
    /* contains */
    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_SET_OK = 0x02;

    /* view */
    @BindView(R.id.tv_sleep_target)
    TextView mSleepTargetTv;

    private OptionsPickerView mSleepTargetPv;     //日期

    /* data */
    private Callback.Cancelable mPostCancelable;
    private boolean mPostEnable;

    private int mSleepTargetMin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sleep_target_set;
    }

    @OnClick({R.id.btn_back, R.id.rl_sign_sleep_target_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_sign_sleep_target_set:
                showSetDialog();
                break;
        }
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(SleepTargetSetActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    updateData(msg.obj.toString());
                    break;
                case MSG_POST_SET_OK:
                    T.showShort(SleepTargetSetActivity.this, "已设置");
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mPostEnable = true;
        getSleepTargetInfo();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mSleepTargetPv = new OptionsPickerView(SleepTargetSetActivity.this);
        int targetSleepMinutes = LocalApplication.getInstance()
                .getLoginUser(SleepTargetSetActivity.this).targetSleepMinutes;

        Spanned target = Html.fromHtml("<font color=\"#323333\">" + targetSleepMinutes / 60
                + "</font> 小时 " + "<font color=\"#323333\">" + targetSleepMinutes % 60
                + "</font> 分钟 ");
        mSleepTargetTv.setText(target);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
            mPostCancelable.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
            mPostHandler.removeMessages(MSG_POST_SET_OK);
        }
    }

    /**
     * s刷新数据
     *
     * @param entity
     */
    private void updateData(String entity) {
        SleepTargetSetRE targetSetRE = JSON.parseObject(entity, SleepTargetSetRE.class);
        mSleepTargetMin = targetSetRE.getMinutes();

        refreshView();
    }

    /**
     * 刷新页面
     */
    private void refreshView() {
        Spanned target = Html.fromHtml("<font color=\"#323333\">" + mSleepTargetMin / 60
                + "</font> 小时 " + "<font color=\"#323333\">" + mSleepTargetMin % 60
                + "</font> 分钟 ");
        mSleepTargetTv.setText(target);
    }

    /**
     * 显示设置
     */
    private void showSetDialog() {
        final ArrayList<Integer> lHourItem = new ArrayList<>();
        final ArrayList<ArrayList<Integer>> lMinItem = new ArrayList<>();
        int lDefaultHour = mSleepTargetMin / 60;
        int lDefaultMin = mSleepTargetMin % 60;
        for (int i = 0; i < 24; i++) {
            lHourItem.add(i);
            ArrayList<Integer> items = new ArrayList<>();
            for (int j = 0; j < 60; j++) {
                items.add(j);
            }
            lMinItem.add(items);
        }

        mSleepTargetPv.setCancelable(true);
        //初始化值
        mSleepTargetPv.setPicker(lHourItem, lMinItem, false);
        //设置是否无限滚动
        mSleepTargetPv.setCyclic(true);

        mSleepTargetPv.setTitle("睡眠时间");

        mSleepTargetPv.setLabels("时", "分钟");
        //
        mSleepTargetPv.setSelectOptions(lDefaultHour, lDefaultMin);

        mSleepTargetPv.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mSleepTargetMin = lHourItem.get(options1) * 60 + lMinItem.get(options1).get(option2);
                updateSleepTargetInfo(mSleepTargetMin);
                refreshView();
            }
        });
        mSleepTargetPv.show();
    }


    /**
     * 获取睡眠目标
     */
    private void getSleepTargetInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lSearchId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = RequestParamsBuilder.buildGetDailySleepTargetRP(SleepTargetSetActivity.this, UrlConfig.URL_GET_DAILY_SLEEP_TARGET, lSearchId);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
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
    }

    /**
     * 设置睡眠目标
     *
     * @param min
     */
    private void updateSleepTargetInfo(final int min) {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lUpdateId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = RequestParamsBuilder.buildUpdateDailySleepTargetRP(SleepTargetSetActivity.this, UrlConfig.URL_UPDATE_DAILY_SLEEP_TARGET, lUpdateId, min);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_POST_SET_OK;
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
                        mPostEnable = true;
                    }

                    @Override
                    public void onFinished() {

                    }
                });

            }
        });
    }
}
