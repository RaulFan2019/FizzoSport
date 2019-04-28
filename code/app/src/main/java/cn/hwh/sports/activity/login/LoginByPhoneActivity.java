package cn.hwh.sports.activity.login;

import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.main.MainActivityV2;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.StringU;

/**
 * Created by Raul.Fan on 2017/4/13.
 * 通过手机验证码登录
 */
public class LoginByPhoneActivity extends BaseActivity {


    /* contains */
    private static final int MSG_GET_VC_ERROR = 0x00;//获取验证码请求错误
    private static final int MSG_GET_VC_OK = 0x01;//获取验证码请求成功
    private static final int MSG_GET_VC_TIMER = 0x02;//获取验证码时间计时
    private static final int MSG_VC_LOGIN_ERROR = 0x03;//登录错误
    private static final int MSG_VC_LOGIN_OK = 0x04;//登录成功


    /* local view */
    @BindView(R.id.btn_get_verification_code)
    Button mGetVerificationCodeBtn;//获取验证码按钮
    @BindView(R.id.et_phone)
    EditText mPhoneEt;//手机号输入框
    @BindView(R.id.ll_phone)
    LinearLayout mPhoneLl;//手机号输入布局
    @BindView(R.id.et_verification_code)
    EditText mVerificationCodeEt;//验证码输入框
    @BindView(R.id.ll_verification_code)
    LinearLayout mVerificationCodeLl;//验证码输入布局


    /* local data */
    private int mCountDownNum;
    private DialogBuilder mDialogBuilder;//提示框

    private Callback.Cancelable mCancelable;//检查号码网络事件
    private String mPhoneNum;//手机号
    private UserInfoRE mLoginResult;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_by_phone;
    }


    @OnClick({R.id.btn_back, R.id.btn_get_verification_code, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.btn_back:
                finish();
                break;
            //申请获取验证码
            case R.id.btn_get_verification_code:
                checkPhone();
                break;
            //点击登录
            case R.id.btn_login:
                postVcLogin();
                break;
        }
    }

    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取验证码失败
                case MSG_GET_VC_ERROR:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.error(LoginByPhoneActivity.this, (String) msg.obj).show();
                    break;
                //发送请求验证码成功
                case MSG_GET_VC_OK:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.success(LoginByPhoneActivity.this,"发送获取验证码成功").show();
                    mCountDownNum = 60;
                    mGetVerificationCodeBtn.setClickable(false);
                    mGetVerificationCodeBtn.setBackgroundResource(R.drawable.bg_shape_fillet_gray);
                    mLocalHandler.sendEmptyMessageDelayed(MSG_GET_VC_TIMER, 1000);
                    break;
                //获取验证码倒计时消息
                case MSG_GET_VC_TIMER:
                    mCountDownNum--;
                    mGetVerificationCodeBtn.setText(mCountDownNum + "s");
                    if (mCountDownNum < 1) {
                        mGetVerificationCodeBtn.setClickable(true);
                        mGetVerificationCodeBtn.setBackgroundResource(R.drawable.selector_shape_fillet_accent);
                        mGetVerificationCodeBtn.setText("获取验证码");
                    } else {
                        mLocalHandler.sendEmptyMessageDelayed(MSG_GET_VC_TIMER, 1000);
                    }
                    break;
                case MSG_VC_LOGIN_ERROR:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.error(LoginByPhoneActivity.this,(String)msg.obj).show();
                    break;
                case MSG_VC_LOGIN_OK:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    UserDBData.CommonLogin(LoginByPhoneActivity.this,mLoginResult);
                    ActivityStackManager.getAppManager().finishAllTempActivity();
                    startActivity(MainActivityV2.class);
                    break;
            }

        }
    };

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mDialogBuilder = new DialogBuilder();
        mPhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focusAble) {
                if (focusAble) {
                    mPhoneLl.setBackgroundResource(R.drawable.bg_widget_input_focus);
                } else {
                    mPhoneLl.setBackgroundResource(R.drawable.bg_widget_input_normal);
                }
            }
        });

        mVerificationCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focusAble) {
                if (focusAble) {
                    mVerificationCodeLl.setBackgroundResource(R.drawable.bg_widget_input_focus);
                } else {
                    mVerificationCodeLl.setBackgroundResource(R.drawable.bg_widget_input_normal);
                }
            }
        });
    }

    @Override
    protected void doMyCreate() {
        ActivityStackManager.getAppManager().addTempActivity(this);
    }

    @Override
    protected void causeGC() {
        mLocalHandler.removeCallbacksAndMessages(null);
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        ActivityStackManager.getAppManager().finishTempActivity(this);
    }

    /**
     * 检查手机号码是否符合
     */
    private void checkPhone() {
        mPhoneNum = mPhoneEt.getText().toString();
        String lError = StringU.checkPhoneNumInputError(LoginByPhoneActivity.this, mPhoneNum);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            Toasty.error(LoginByPhoneActivity.this, lError).show();
            return;
        }

        //请求获取验证码
//        mDialogBuilder.showProgressDialog(LoginByPhoneActivity.this, "请稍后...", true);
//        mDialogBuilder.setListener(new DialogBuilder.ProgressDialogListener() {
//            @Override
//            public void onCancel() {
//                if (mCancelable != null && !mCancelable.isCancelled()) {
//                    mCancelable.cancel();
//                }
//
//            }
//        });

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetVCodeRP(LoginByPhoneActivity.this, UrlConfig.URL_GET_VC_CODE, mPhoneNum);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            Message lOkMsg = new Message();
                            lOkMsg.what = MSG_GET_VC_OK;
                            lOkMsg.obj = result.result;
                            mLocalHandler.sendMessage(lOkMsg);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_GET_VC_ERROR;
                            msg.obj = result.errormsg;
                            mLocalHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_GET_VC_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mLocalHandler.sendMessage(msg);
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
     * 获取验证码登录
     */
    private void postVcLogin() {
        final String vcCode = mVerificationCodeEt.getText().toString();
        final String pushId = "";
        //请求获取验证码
//        mDialogBuilder.showProgressDialog(LoginByPhoneActivity.this, "正在登录...", true);
//        mDialogBuilder.setListener(new DialogBuilder.ProgressDialogListener() {
//            @Override
//            public void onCancel() {
//                if (mCancelable != null && !mCancelable.isCancelled()) {
//                    mCancelable.cancel();
//                }
//            }
//        });
        mPhoneNum = mPhoneEt.getText().toString();
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildLoginByVcCodeRP(LoginByPhoneActivity.this,
                        UrlConfig.URL_LOGIN_BY_VC_CODE, mPhoneNum, vcCode, pushId);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mLoginResult = JSON.parseObject(result.result, UserInfoRE.class);
                            mLocalHandler.sendEmptyMessage(MSG_VC_LOGIN_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_VC_LOGIN_ERROR;
                            msg.obj = result.errormsg;
                            mLocalHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_VC_LOGIN_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mLocalHandler.sendMessage(msg);
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
