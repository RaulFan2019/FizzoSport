package cn.hwh.sports.activity.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import cn.hwh.sports.activity.main.MainActivityV2;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.EditTextPhone;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;

/**
 * Created by Administrator on 2016/11/11.
 */

public class LoginActivity extends BaseActivity {
    /* contains */
    private static final String TAG = "LoginActivity";

    private static final int MSG_LOGIN_ERROR = 0x00;
    private static final int MSG_LOGIN_OK = 0x01;

    /* widget */
    @BindView(R.id.et_phone)
    EditTextPhone mPhoneEt;
    @BindView(R.id.iv_bg_phone)
    ImageView mPhoneBgIv;
    @BindView(R.id.et_psw)
    EditText mPswEt;
    @BindView(R.id.iv_bg_psw)
    ImageView mPswBgIv;
    @BindView(R.id.tv_error_phone)
    TextView mErrorPhoneTv;
    @BindView(R.id.tv_error_pwd)
    TextView mErrorPwdTv;

    DialogBuilder mDialog;


    /**
     * local data
     **/
    private boolean mPostEnable;    //是否可以登录
    private Callback.Cancelable mLoginCancel;//取消请求事件

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            switch (msg.what) {
                //请求成功
                case MSG_LOGIN_OK:
                    mErrorPwdTv.setText("");
                    mErrorPhoneTv.setText("");

                    preLogin(msg.obj.toString());
                    break;
                //请求失败
                case MSG_LOGIN_ERROR:
                    String error = msg.obj.toString();
                    if (error.contains("用户")){
                        mErrorPhoneTv.setText(msg.obj.toString());
                    }else {
                        mErrorPwdTv.setText(msg.obj.toString());
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.btn_login, R.id.btn_register, R.id.tv_forget_pwd, R.id.rl_login})
    public void onClick(View view) {
        switch (view.getId()) {
            //登录
            case R.id.btn_login:
                checkInput();
                break;
            //注册
            case R.id.btn_register:
                launchRegister();
                break;
            //忘记密码
            case R.id.tv_forget_pwd:
                launchForget();
                break;
            case R.id.rl_login:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void initData() {
        mPostEnable = true;
        mCheckNewData = false;
        mDialog = new DialogBuilder();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mPhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focusAble) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) mPhoneBgIv.getDrawable();
                if (focusAble) {
                    transitionDrawable.startTransition(200);
                } else {
                    transitionDrawable.reverseTransition(200);
                }
            }
        });

        mPswEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focusAble) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) mPswBgIv.getDrawable();
                if (focusAble) {
                    transitionDrawable.startTransition(200);
                } else {
                    transitionDrawable.reverseTransition(200);
                }
            }
        });
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {
        if (mLoginCancel != null && !mLoginCancel.isCancelled()) {
            mLoginCancel.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_LOGIN_ERROR);
            mPostHandler.removeMessages(MSG_LOGIN_OK);
            mPostHandler = null;
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 检查输入值并上传至服务器开始登录
     */
    private void checkInput() {
        mErrorPhoneTv.setText("");
        mErrorPwdTv.setText("");
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;

        //手机号码检查
        final String lPhoneNum = mPhoneEt.getText().toString().replace(" ", "");
        String lError = StringU.checkPhoneNumInputError(getApplicationContext(), lPhoneNum);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            mPostEnable = true;
            mErrorPhoneTv.setText(lError);
            return;
        }
        //密码检查
        final String lPsw = mPswEt.getText().toString();
        lError = StringU.checkPasswordInputError(getApplicationContext(), lPsw);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            mPostEnable = true;
            mErrorPwdTv.setText(lError);
            return;
        }
        final String lDevicePushId = "JPushInterface.getRegistrationID(getApplicationContext());";
        final String lDeviceOs = MyBuildConfig.DEVICEOS;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildCommonLoginRequest(LoginActivity.this,
                        UrlConfig.URL_COMMON_LOGIN, lPhoneNum, lPsw, lDevicePushId, lDeviceOs);
                mLoginCancel = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                //  服务器正常返回数据
                                UserSPData.setUserPwd(LoginActivity.this,lPsw);
                                Message msg = new Message();
                                msg.what = MSG_LOGIN_OK;
                                msg.obj = result.result;
                                mPostHandler.sendMessage(msg);
                            } else {
                                // 服务器返回错误码
                                Message msg = new Message();
                                msg.what = MSG_LOGIN_ERROR;
                                msg.obj = result.errormsg;
                                mPostHandler.sendMessage(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        //请求服务器失败
                        if (mPostHandler != null) {
                            Message msg = new Message();
                            msg.what = MSG_LOGIN_ERROR;
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
                        mDialog.dismiss();
                    }
                });
            }
        });

        //可以请求，开始显示dialog
        mDialog.showProgressDialog(LoginActivity.this, "登录中...", true);
        mDialog.setListener(new DialogBuilder.ProgressDialogListener() {
            @Override
            public void onCancel() {
                mLoginCancel.cancel();
                mPostHandler.removeMessages(MSG_LOGIN_OK);
                mPostHandler.removeMessages(MSG_LOGIN_ERROR);
                mPostEnable = true;
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
        UserDBData.CommonLogin(LoginActivity.this, entity);
        Intent i = new Intent(getApplicationContext(), MainActivityV2.class);
        startActivity(i);
        finish();
    }

    /**
     * 启动注册页面
     */
    private void launchRegister() {
        startActivity(RegisterActivity.class);
    }

    /**
     * 启动忘记密码页面
     */
    private void launchForget() {
        startActivity(RecoverPswActivity.class);
    }

}
