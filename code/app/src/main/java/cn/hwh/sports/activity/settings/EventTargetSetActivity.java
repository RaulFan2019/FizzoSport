package cn.hwh.sports.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.EventTargetSetRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;

/**
 * Created by Administrator on 2016/11/21.
 */

public class EventTargetSetActivity extends BaseActivity {

    /* contains */
    public static final int TYPE_STEP = 0x10;
    public static final int TYPE_LENGTH = 0x11;
    public static final int TYPE_CALORIE = 0x12;
    public static final int TYPE_POINT = 0x13;
    public static final int TYPE_SPORT_TIME = 0x14;


    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_UPDATE_POST_OK = 0x02;

    /* view */
    @BindView(R.id.tv_step_target)
    TextView mStepTargetTv;     //步数显示
    @BindView(R.id.tv_distance_target)
    TextView mDistanceTargetTv;     //距离显示
    @BindView(R.id.tv_calorie_target)
    TextView mCalorieTargetTv;      //卡路里显示
    @BindView(R.id.tv_sport_point_target)
    TextView mSportPointTargetTv;       //运动点数显示
    @BindView(R.id.tv_active_time_target)
    TextView mActiveTimeTargetTv;       //活动时间显示

    DialogBuilder mDialog;

    /* data */
    private boolean mPostEnable;
    private Callback.Cancelable mPostCancelable;

    private UserDE mUserDe;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_target_set;
    }

    @OnClick({R.id.rl_sign_step_target_set, R.id.rl_sign_distance_target_set,
            R.id.rl_sign_calorie_target_set, R.id.rl_sign_sport_point_target_set,
            R.id.rl_sign_active_time_target_set, R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            //步数
            case R.id.rl_sign_step_target_set:
                showStepDialog();
                break;
            //距离
            case R.id.rl_sign_distance_target_set:
                showDistanceDialog();
                break;
            //卡路里数
            case R.id.rl_sign_calorie_target_set:
                showCalorieDialog();
                break;
            //锻炼点数
            case R.id.rl_sign_sport_point_target_set:
                showSportPointDialog();
                break;
            //活跃分钟数
            case R.id.rl_sign_active_time_target_set:
                showActiveTimeDialog();
                break;
        }
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDialog.dismiss();
            mPostEnable = true;
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(EventTargetSetActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    updateData(msg.obj.toString());
                    break;
                case MSG_UPDATE_POST_OK:
                    UserDBData.update(mUserDe);
                    LocalApplication.getInstance().mLoginUser = mUserDe;
                    refreshView();
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mDialog = new DialogBuilder();
        mPostEnable = true;
        mUserDe = LocalApplication.getInstance().getLoginUser(EventTargetSetActivity.this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        refreshView();
    }

    @Override
    protected void doMyCreate() {
        getEventTargetInfo();
    }

    @Override
    protected void causeGC() {
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
        if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
            mPostCancelable.cancel();
        }
        mDialog.dismiss();

    }

    /**
     * 刷新数据
     */
    private void updateData(String entity) {
        EventTargetSetRE targetSetRE = JSON.parseObject(entity, EventTargetSetRE.class);

        mUserDe.targetStepCount = targetSetRE.getStepcount_target().getStepcount();
        mUserDe.targetLength = targetSetRE.getLength_target().getLength();
        mUserDe.targetCalorie = targetSetRE.getCalorie_target().getCalorie();
        mUserDe.targetPoint = targetSetRE.getEffort_point_target().getEffort_point();
        mUserDe.targetSportMinutes = targetSetRE.getExercise_minutes_target().getExercise_minutes();

        UserDBData.update(mUserDe);
        LocalApplication.getInstance().mLoginUser = mUserDe;
        refreshView();
    }

    /**
     * 刷新页面
     */
    private void refreshView() {
        Spanned lStepTarget = Html.fromHtml("<font color=\"#323333\">" + StringU.numFormat(mUserDe.targetStepCount)
                + "</font> 步");
        Spanned lLengthTarget = Html.fromHtml("<font color=\"#323333\">" + mUserDe.targetLength / 1000.0
                + "</font> 公里");
        Spanned lCalorieTarget = Html.fromHtml("<font color=\"#323333\">" + StringU.numFormat(mUserDe.targetCalorie)
                + "</font> 千卡");
        Spanned lEffortPointTarget = Html.fromHtml("<font color=\"#323333\">" + StringU.numFormat(mUserDe.targetPoint)
                + "</font> 点");
        Spanned lExerciseMinutesTarget = Html.fromHtml("<font color=\"#323333\">" + StringU.numFormat(mUserDe.targetSportMinutes)
                + "</font> 分钟");
        mStepTargetTv.setText(lStepTarget);
        mDistanceTargetTv.setText(lLengthTarget);
        mCalorieTargetTv.setText(lCalorieTarget);
        mSportPointTargetTv.setText(lEffortPointTarget);
        mActiveTimeTargetTv.setText(lExerciseMinutesTarget);
    }

    /**
     * 步数弹框
     */
    private void showStepDialog() {
        mDialog.showEditDialog(this, "目标步数", "保存", InputType.TYPE_CLASS_NUMBER, String.valueOf(mUserDe.targetStepCount));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkTargetStepError(EventTargetSetActivity.this, mInputText);
                if (error.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
                    mUserDe.targetStepCount = Integer.valueOf(mInputText);
                    updateEventTargetInfo(mInputText, TYPE_STEP);
                } else {
                    T.showShort(EventTargetSetActivity.this, error);
                }
            }
        });
    }

    /**
     * 距离弹框
     */
    private void showDistanceDialog() {
        mDialog.showEditDialog(this, "目标距离", "保存", InputType.TYPE_NUMBER_FLAG_DECIMAL, String.valueOf((float) mUserDe.targetLength/1000));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkTargetLengthError(EventTargetSetActivity.this, mInputText);
                if (error.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
                    mUserDe.targetLength = (int) (Float.valueOf(mInputText) * 1000);
                    updateEventTargetInfo(mUserDe.targetLength + "", TYPE_LENGTH);
                } else {
                    T.showShort(EventTargetSetActivity.this, error);
                }
            }
        });
    }

    /**
     * 卡路里弹框
     */
    private void showCalorieDialog() {
        mDialog.showEditDialog(this, "目标卡路里", "保存", InputType.TYPE_CLASS_NUMBER, String.valueOf(mUserDe.targetCalorie));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkTargetCalorieError(EventTargetSetActivity.this, mInputText);
                if (error.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
                    mUserDe.targetCalorie = Integer.valueOf(mInputText);
                    updateEventTargetInfo(mInputText, TYPE_CALORIE);
                } else {
                    T.showShort(EventTargetSetActivity.this, error);
                }
            }
        });
    }

    /**
     * 锻炼点数
     */
    private void showSportPointDialog() {
        mDialog.showEditDialog(this, "目标点数", "保存", InputType.TYPE_CLASS_NUMBER, String.valueOf(mUserDe.targetPoint));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkTargetPointError(EventTargetSetActivity.this, mInputText);
                if (error.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
                    mUserDe.targetPoint = Integer.valueOf(mInputText);
                    updateEventTargetInfo(mInputText, TYPE_POINT);
                } else {
                    T.showShort(EventTargetSetActivity.this, error);
                }
            }
        });
    }

    /**
     * 活动分钟
     */
    private void showActiveTimeDialog() {
        mDialog.showEditDialog(this, "活跃分钟数", "保存", InputType.TYPE_CLASS_NUMBER, String.valueOf(mUserDe.targetSportMinutes));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                String error = StringU.checkTargetSportTimeError(EventTargetSetActivity.this, mInputText);
                if (error.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
                    mUserDe.targetSportMinutes = Integer.valueOf(mInputText);
                    updateEventTargetInfo(mInputText, TYPE_SPORT_TIME);
                } else {
                    T.showShort(EventTargetSetActivity.this, error);
                }
            }
        });
    }

    /**
     * 获取目标值
     */
    private void getEventTargetInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lSearchId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = RequestParamsBuilder.buildGetDailyExerciseTargetRP(EventTargetSetActivity.this, UrlConfig.URL_GET_DAILY_EXERCISE_TARGET, lSearchId);
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
        mDialog.showProgressDialog(EventTargetSetActivity.this, "正在获取...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mPostCancelable != null){
                    mPostCancelable.cancel();
                }
                finish();
            }
        });
    }

    /**
     * 更新活动目标参数
     *
     * @param value
     * @param type
     */
    private void updateEventTargetInfo(final String value, final int type) {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lUpdateId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = RequestParamsBuilder.buildUpdateDailyExerciseTargetRP(EventTargetSetActivity.this,
                        UrlConfig.URL_UPDATE_DAILY_EXERCISE_TARGET, lUpdateId, value, type);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_UPDATE_POST_OK;
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

        mDialog.showProgressDialog(EventTargetSetActivity.this, "正在修改...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                mPostEnable = true;
                if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
                    mPostCancelable.cancel();
                }
            }
        });
    }
}
