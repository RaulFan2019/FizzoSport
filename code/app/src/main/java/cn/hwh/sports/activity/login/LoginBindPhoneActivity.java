package cn.hwh.sports.activity.login;

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
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
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
 */

public class LoginBindPhoneActivity extends BaseActivity {


    /* contains */
    private static final int MSG_GET_VC_ERROR = 0x00;//获取验证码请求错误
    private static final int MSG_GET_VC_OK = 0x01;//获取验证码请求成功
    private static final int MSG_GET_VC_TIMER = 0x02;//获取验证码时间计时
    private static final int MSG_VC_BIND_ERROR = 0x03;//绑定错误
    private static final int MSG_VC_BIND_OK = 0x04;//绑定成功

    /* local view */
    @BindView(R.id.et_phone)
    EditText mPhoneEt;//手机输入框
    @BindView(R.id.ll_phone)
    LinearLayout mPhoneLl;//手机输入布局
    @BindView(R.id.et_verification_code)
    EditText mVerificationCodeEt;//验证码输入框
    @BindView(R.id.ll_verification_code)
    LinearLayout mVerificationCodeLl;//验证码输入布局
    @BindView(R.id.btn_get_verification_code)
    Button mGetVerificationCodeBtn;//获取验证码按钮

    /* local data */
    private int mUserId;//用户ID
    private UserDE mUserDe;//用户数据库类型
    private String mPhoneNum;//手机号码
    private UserInfoRE mLoginResult;

    private int mCountDownNum;
    private DialogBuilder mDialogBuilder;//提示框

    private Callback.Cancelable mCancelable;//检查号码网络事件

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_bind_phone;
    }


    @OnClick({R.id.btn_back, R.id.btn_get_verification_code, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击back
            case R.id.btn_back:
                finish();
                break;
            //点击请求验证码
            case R.id.btn_get_verification_code:
                checkPhone();
                break;
            //点击下一步
            case R.id.btn_next:
                postBindPhone();
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
                    Toasty.error(LoginBindPhoneActivity.this, (String) msg.obj).show();
                    break;
                //发送请求验证码成功
                case MSG_GET_VC_OK:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.success(LoginBindPhoneActivity.this,"发送获取验证码成功").show();
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
                //绑定失败
                case MSG_VC_BIND_ERROR:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.error(LoginBindPhoneActivity.this,(String)msg.obj).show();
                    break;
                //绑定成功
                case MSG_VC_BIND_OK:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.success(LoginBindPhoneActivity.this,"绑定成功").show();
                    UserDBData.CommonLogin(LoginBindPhoneActivity.this,mLoginResult);
                    mLocalHandler.removeCallbacksAndMessages(null);
                    mGetVerificationCodeBtn.setClickable(true);
                    mGetVerificationCodeBtn.setBackgroundResource(R.drawable.selector_shape_fillet_accent);
                    mGetVerificationCodeBtn.setText("获取验证码");
                    startActivity(LoginPerfectUserActivity.class);
                    break;
            }

        }
    };

    @Override
    protected void initData() {
        mUserId = getIntent().getExtras().getInt("userId");
        mUserDe = UserDBData.getUserById(mUserId);
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
        mPhoneNum = mPhoneEt.getText().toString().replace(" ", "");
        String lError = StringU.checkPhoneNumInputError(LoginBindPhoneActivity.this, mPhoneNum);
        if (!lError.equals(AppEnum.DEFAULT_CHECK_ERROR)) {
            Toasty.error(LoginBindPhoneActivity.this, lError).show();
            return;
        }

        //请求获取验证码
//        mDialogBuilder.showProgressDialog(LoginBindPhoneActivity.this, "请稍后...", true);
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
                RequestParams requestParams = RequestParamsBuilder.buildGetVCodeRP(LoginBindPhoneActivity.this, UrlConfig.URL_GET_VC_CODE, mPhoneNum);
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
     * 发送绑定手机号的请求
     */
    private void postBindPhone() {
        final String vCode = mVerificationCodeEt.getText().toString();
        mDialogBuilder.showProgressDialog(LoginBindPhoneActivity.this, "正在请求绑定..", false);
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildBindPhoneRP(LoginBindPhoneActivity.this,
                        UrlConfig.URL_BIND_PHONE, mUserId, mPhoneNum, vCode, mUserDe.sessionId);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mLoginResult = JSON.parseObject(result.result, UserInfoRE.class);
                            mLocalHandler.sendEmptyMessage(MSG_VC_BIND_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_VC_BIND_ERROR;
                            msg.obj = result.errormsg;
                            mLocalHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_VC_BIND_ERROR;
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
