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
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.BodyTargetSetRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.T;

/**
 * Created by Administrator on 2016/11/20.
 */

public class BodyTargetSetActivity extends BaseActivity {
    /* contains */
    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_SET_OK = 0x02;

    /* view */
    @BindView(R.id.tv_weight_target)
    TextView mWeightTargetTv;
    @BindView(R.id.tv_fat_ratio_target)
    TextView mFatRatioTargetTv;

    DialogBuilder mDialog;

    /* data */
    private float mWeightTarget;
    private float mFatRatioTarget;

    private Callback.Cancelable mPostCancelable;
    private boolean mPostEnable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_body_target_set;
    }

    @OnClick({R.id.btn_back, R.id.rl_weight, R.id.rl_fat_rid})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_weight:
                showWeightDialog();
                break;
            case R.id.rl_fat_rid:
                showFatRatioDialog();
                break;
        }
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            mDialog.dismiss();
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(BodyTargetSetActivity.this, msg.obj.toString());
                    break;
                case MSG_POST_OK:
                    updateData(msg.obj.toString());
                    break;
                case MSG_POST_SET_OK:
                    T.showShort(BodyTargetSetActivity.this, "已设置");
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mDialog = new DialogBuilder();
        mPostEnable = true;
        mWeightTarget = LocalApplication.getInstance().getLoginUser(BodyTargetSetActivity.this).targetWeight;
        mFatRatioTarget = LocalApplication.getInstance().getLoginUser(BodyTargetSetActivity.this).targetFatRate;
        getBodyTargetInfo();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Spanned lWeightTarget = Html.fromHtml("<font color=\"#323333\">" + mWeightTarget
                + "</font> kg ");
        mWeightTargetTv.setText(lWeightTarget);

        Spanned lFatRatioTarget = Html.fromHtml("<font color=\"#323333\">" + mFatRatioTarget
                + "</font> % ");
        mFatRatioTargetTv.setText(lFatRatioTarget);
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
            mPostHandler.removeMessages(MSG_POST_SET_OK);
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
        mDialog.dismiss();
    }

    /**
     * 更新数据
     */
    private void updateData(String entity) {
        BodyTargetSetRE targetSetRE = JSON.parseObject(entity, BodyTargetSetRE.class);
        mWeightTarget = targetSetRE.getWeight_target().getWeight();
        mFatRatioTarget = targetSetRE.getFatrate_target().getFatrate();

        refreshView();
    }

    /**
     * 刷新页面
     */
    private void refreshView() {
        Spanned lWeightTarget = Html.fromHtml("<font color=\"#323333\">" + mWeightTarget
                + "</font> kg ");
        mWeightTargetTv.setText(lWeightTarget);

        Spanned lFatRatioTarget = Html.fromHtml("<font color=\"#323333\">" + mFatRatioTarget
                + "</font> % ");
        mFatRatioTargetTv.setText(lFatRatioTarget);
    }

    /**
     * 体重弹框
     */
    private void showWeightDialog() {
        mDialog.showEditDialog(this, "体重", "保存", InputType.TYPE_NUMBER_FLAG_DECIMAL, String.valueOf(mWeightTarget));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.equals("0") && mInputText.matches("^[0-9]+\\.?[0-9]{0,2}$")) {
                    updateBodyTargetInfo(Float.valueOf(mInputText), 0);
                    mWeightTarget = Float.valueOf(mInputText);
                    refreshView();
                } else {
                    T.showShort(BodyTargetSetActivity.this, "请输入不为0的目标");
                }
            }
        });
    }

    /**
     * 体脂率弹框
     */
    private void showFatRatioDialog() {
        mDialog.showEditDialog(this, "体脂率", "保存", InputType.TYPE_NUMBER_FLAG_DECIMAL, String.valueOf(mFatRatioTarget));
        mDialog.setListener(new DialogBuilder.EditDialogListener() {
            @Override
            public void onConfirmBtnClick(String mInputText) {
                if (mInputText != null && !mInputText.equals("0") && mInputText.matches("^[0-9]+\\.?[0-9]{0,2}$")) {
                    updateBodyTargetInfo(0, Float.valueOf(mInputText));
                    mFatRatioTarget = Float.valueOf(mInputText);
                    refreshView();
                } else {
                    T.showShort(BodyTargetSetActivity.this, "请输入不为0的目标");
                }
            }
        });
    }

    /**
     * 获取体制信息
     */
    private void getBodyTargetInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lSearchId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams requestParams = RequestParamsBuilder.buildGetCharacteristicTargetRP(BodyTargetSetActivity.this, UrlConfig.URL_GET_CHARACTERISTIC_TARGET, lSearchId);
                mPostCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
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
        mDialog.showProgressDialog(BodyTargetSetActivity.this, "加载中...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
                    mPostCancelable.cancel();
                }
            }
        });
    }

    /**
     * 设置用户体征目标
     *
     * @param weight
     * @param fatRatio
     */
    private void updateBodyTargetInfo(final float weight, final float fatRatio) {

        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                int lUpdateId = LocalApplication.getInstance().mLoginUser.userId;
                RequestParams params = RequestParamsBuilder.buildUpdateCharacteristicTargetRP(BodyTargetSetActivity.this, UrlConfig.URL_UPDATE_CHARACTERISTIC_TARGET,
                        lUpdateId, weight, fatRatio);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
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

        mDialog.showProgressDialog(BodyTargetSetActivity.this, "正在修改...", true);
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
