package cn.hwh.sports.activity.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.entity.net.PhoneNumIsRegisterRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.EditTextPhone;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;

/**
 * Created by Administrator on 2016/11/14.
 */

public class RecoverPswActivity extends BaseActivity {

    /**
     * contains
     **/
    private static final String TAG = "RecoverPswActivity";

    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_VC_OK = 0x01;
    private static final int MSG_POST_RECOVER_PSW_OK = 0x02;
    private static final int MSG_GET_VC_TIME = 0x03;//倒计时消息

    /**
     * local view
     **/
    @BindView(R.id.iv_bg_phone)
    ImageView mPhoneBgIv;   //电话号码输入背景
    @BindView(R.id.et_phone)
    EditTextPhone mPhoneEt; //电话输入框
    @BindView(R.id.iv_bg_v_code)
    ImageView mVCodeBgIv; //验证码背景
    @BindView(R.id.et_v_code)
    EditText mVCodeEt; //验证码输入框
    @BindView(R.id.iv_bg_psw)
    ImageView mPswBgIv;//密码背景
    @BindView(R.id.et_psw)
    EditText mPswEt;//密码输入框
    @BindView(R.id.tv_get_v_code)
    TextView mGetVCodeTv;//获取验证码按钮

    DialogBuilder mDialog;//提示框
    @BindView(R.id.tv_error_phone)
    TextView mErrorPhoneTv;//手机输入错误信息
    @BindView(R.id.tv_error_code)
    TextView mErrorCodeTv;//验证码错误信息
    @BindView(R.id.tv_error_pwd)
    TextView mErrorPwdTv;//密码错误信息

    /**
     * local data
     **/
    private boolean mPostEnable; //是否可以请求
    private boolean mPostPswEnable;//修改密码请求

    private String mPhoneNum;
    private int mCountDownNum;

    private Callback.Cancelable mCheckPhoneCancelable;//检查号码撤销
    private Callback.Cancelable mRecoverPswCancelable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_recover_psw;
    }

    @OnClick({R.id.tv_get_v_code, R.id.btn_complete, R.id.btn_back, R.id.rl_recover_psw_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_get_v_code:
                checkPhone();
                break;

            case R.id.btn_complete:
                completeBtnClick();
                break;

            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_recover_psw_bg:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                break;
        }
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //验证码请求完成
                case MSG_POST_VC_OK:
                    mDialog.dismiss();
                    mPostEnable = true;
                    mCountDownNum = 60;
                    mGetVCodeTv.setClickable(false);
                    mGetVCodeTv.setTextColor(Color.parseColor("#a3a3a3"));
                    mPostHandler.sendEmptyMessage(MSG_GET_VC_TIME);

                    mErrorCodeTv.setText("");
                    mErrorPwdTv.setText("");
                    mErrorPhoneTv.setText("");
                    break;
                //请求失败
                case MSG_POST_ERROR:
                    mErrorCodeTv.setText(msg.obj.toString());
                    mDialog.dismiss();
                    mPostEnable = true;
                    mPostPswEnable = true;
                    break;
                //倒计时
                case MSG_GET_VC_TIME:
                    mCountDownNum--;
                    mGetVCodeTv.setText("(" + mCountDownNum + ")");
                    if (mCountDownNum < 1) {
                        mGetVCodeTv.setClickable(true);
                        mGetVCodeTv.setTextColor(Color.parseColor("#777777"));
                        mGetVCodeTv.setText("获取验证码");
                    } else {
                        mPostHandler.sendEmptyMessageDelayed(MSG_GET_VC_TIME, 1000);
                    }

                    break;
                case MSG_POST_RECOVER_PSW_OK:
                    mPostPswEnable = true;
                    mDialog.dismiss();
                    mErrorCodeTv.setText("");
                    mErrorPwdTv.setText("");
                    mErrorPhoneTv.setText("");

                    
                    preLogin(msg.obj.toString());
                    break;

            }

        }
    };

    @Override
    protected void initData() {
        mPostEnable = true;
        mPostPswEnable = true;
        mCheckNewData = false;
        mDialog = new DialogBuilder();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mPhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocused) {
                TransitionDrawable transition = (TransitionDrawable) mPhoneBgIv.getDrawable();
                if (hasFocused) {
                    transition.startTransition(200);
                } else {
                    transition.reverseTransition(200);
                }
            }
        });
        mVCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocused) {
                TransitionDrawable transition = (TransitionDrawable) mVCodeBgIv.getDrawable();
                if (hasFocused) {
                    transition.startTransition(200);
                } else {
                    transition.reverseTransition(200);
                }
            }
        });
        mPswEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocused) {
                TransitionDrawable transition = (TransitionDrawable) mPswBgIv.getDrawable();
                if (hasFocused) {
                    transition.startTransition(200);
                } else {
                    transition.reverseTransition(200);
                }
            }
        });
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        mDialog.dismiss();

        if (mCheckPhoneCancelable != null && !mCheckPhoneCancelable.isCancelled()) {
            mCheckPhoneCancelable.cancel();
        }

        if (mRecoverPswCancelable != null && !mRecoverPswCancelable.isCancelled()) {
            mRecoverPswCancelable.cancel();
        }

        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_VC_OK);
            mPostHandler.removeMessages(MSG_GET_VC_TIME);
            mPostHandler = null;
        }

        mPhoneNum = null;
        mDialog = null;
    }

    /**
     * 检查手机号码是否符合
     */
    private void checkPhone() {
        mPhoneNum = mPhoneEt.getText().toString().replace(" ", "");
        String lError = StringU.checkPhoneNumInputError(RecoverPswActivity.this, mPhoneNum);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            mErrorPhoneTv.setText(lError);
            return;
        }

        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        //进度条
        mDialog.showProgressDialog(RecoverPswActivity.this, "请稍后...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mCheckPhoneCancelable != null && !mCheckPhoneCancelable.isCancelled()) {
                    mCheckPhoneCancelable.cancel();
                }

            }
        });

        //开始验证手机号并获取验证码
        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildCheckPhoneRP(RecoverPswActivity.this, UrlConfig.URL_CHECK_PHONE_IS_REGISTER, mPhoneNum);
                mCheckPhoneCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {

                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                PhoneNumIsRegisterRE lPhoneEntity = JSON.parseObject(result.result, PhoneNumIsRegisterRE.class);

                                if (lPhoneEntity.isExist != PhoneNumIsRegisterRE.EXIST) {
                                    //号码不存在服务器上时，提示当前的手机号码未注册

                                    Message msg = new Message();
                                    msg.what = MSG_POST_ERROR;
                                    msg.obj = "该手机号码未注册";
                                    mPostHandler.sendMessage(msg);
                                } else {
                                    //已存在服务器上时，表示可以进行修改密码
                                    getVCode();
                                }
                            } else {
                                //服务器返回错误 码
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

    /**
     * 获取短信验证码
     */
    private void getVCode() {

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetVCodeRP(RecoverPswActivity.this, UrlConfig.URL_GET_VC_CODE, mPhoneNum);
                mCheckPhoneCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message lOkMsg = new Message();
                                lOkMsg.what = MSG_POST_VC_OK;
                                lOkMsg.obj = result.result;
                                mPostHandler.sendMessage(lOkMsg);
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

    /**
     * 保存新设置的密码
     */
    private void completeBtnClick() {

        mPhoneNum = mPhoneEt.getText().toString().replace(" ", "");
        String lError = StringU.checkPhoneNumInputError(RecoverPswActivity.this, mPhoneNum);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            mErrorPhoneTv.setText(lError);
            return;
        }

        final String lPsw = mPswEt.getText().toString();
        lError = StringU.checkPasswordInputError(RecoverPswActivity.this, lPsw);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            mErrorPwdTv.setText(lError);
            return;
        }
        final String lVCode = mVCodeEt.getText().toString();
        lError = StringU.checkVCCodeError(RecoverPswActivity.this, lVCode);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            mErrorCodeTv.setText(lError);
            return;
        }
        if (!mPostPswEnable) {
            return;
        }
        mPostPswEnable = false;

        //进度条
        mDialog.showProgressDialog(RecoverPswActivity.this, "请稍后...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                if (mRecoverPswCancelable != null && !mRecoverPswCancelable.isCancelled()) {
                    mRecoverPswCancelable.cancel();
                }
            }
        });

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildResetPwdRP(RecoverPswActivity.this, UrlConfig.URL_RESET_PWD, mPhoneNum, lVCode, lPsw);
                mRecoverPswCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {

                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message message = new Message();
                                message.what = MSG_POST_RECOVER_PSW_OK;
                                message.obj = result.result;
                                mPostHandler.sendMessage(message);
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
                        mPostPswEnable = true;
                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 存储数据并跳转页面
     *
     * @param result
     */
    private void preLogin(final String result) {
        UserInfoRE entity = JSON.parseObject(result, UserInfoRE.class);
        UserDBData.CommonLogin(RecoverPswActivity.this, entity);
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
