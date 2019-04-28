package cn.hwh.sports.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.ActivityStackManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.main.MainActivityV2;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.model.WxUserInfo;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.entity.net.LoginWithWxRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;

/**
 * Created by Raul.Fan on 2017/4/13.
 */

public class LoginActivityV2 extends BaseActivity {


    private static final int MSG_GET_AUTH_ERROR = 0x01;//获取授权失败
    private static final int MSG_WX_GET_USER_INFO = 0x02;//获取微信用户信息
    private static final int MSG_WX_CANCEL = 0x03;
    private static final int MSG_LOGIN_ERROR = 0x04;//登录失败
    private static final int MSG_LOGIN_OK = 0x05;//登录成功


    /* local data */
    private WxUserInfo mWxUserInfo;
    private UserInfoRE mLoginResult;

    private DialogBuilder mDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_v2;
    }

    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //授权失败
                case MSG_GET_AUTH_ERROR:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    break;
                //授权取消
                case MSG_WX_CANCEL:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    break;
                //获取微信用户信息
                case MSG_WX_GET_USER_INFO:
                    showWxLoginDialog();
                    break;
                //登录出错
                case MSG_LOGIN_ERROR:
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    Toasty.error(LoginActivityV2.this, (String) msg.obj).show();
                    break;
                case MSG_LOGIN_OK:
                    checkUserIsNeedBindPhone();
                    if (mDialogBuilder.mProgressDialog != null) {
                        mDialogBuilder.mProgressDialog.dismiss();
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.btn_wx_login, R.id.btn_jump_to_phone_login})
    public void onClick(View view) {
        switch (view.getId()) {
            //微信登录
            case R.id.btn_wx_login:
                showGetAuthorizationDialog();
                break;
            //跳转到手机号登录
            case R.id.btn_jump_to_phone_login:
                startActivity(LoginByPhoneActivity.class);
                break;
        }
    }

    UMAuthListener umAuthListener = new UMAuthListener() {
        //授权开始的回调
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            switch (action) {
                //授权成功
                case UMAuthListener.ACTION_AUTHORIZE:
                    Toasty.normal(LoginActivityV2.this, "授权成功！").show();
                    UMShareAPI.get(LoginActivityV2.this).getPlatformInfo(LoginActivityV2.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    break;
                //获取用户资料
                case UMAuthListener.ACTION_GET_PROFILE:
                    mWxUserInfo = new WxUserInfo(data.get("unionid"), data.get("screen_name"), data.get("gender"),
                            data.get("iconurl"), data.get("openid"));
                    mLocalHandler.sendEmptyMessage(MSG_WX_GET_USER_INFO);
                    break;
                //授权删除
                case UMAuthListener.ACTION_DELETE:
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            mLocalHandler.sendEmptyMessage(MSG_GET_AUTH_ERROR);
            Toasty.error(LoginActivityV2.this, "授权失败！" + " msg:" + t.getMessage()).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toasty.error(LoginActivityV2.this, "授权取消！").show();
            mLocalHandler.sendEmptyMessage(MSG_WX_CANCEL);
        }
    };

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void doMyCreate() {
        ActivityStackManager.getAppManager().addTempActivity(this);
    }

    @Override
    protected void causeGC() {
        mLocalHandler.removeCallbacksAndMessages(null);
        ActivityStackManager.getAppManager().finishTempActivity(this);
    }

    /**
     * 显示正在授权
     */
    private void showGetAuthorizationDialog() {
        mDialogBuilder.showProgressDialog(LoginActivityV2.this, "正在申请授权...", false);
        boolean isauth = UMShareAPI.get(this).isAuthorize(this, SHARE_MEDIA.WEIXIN);
        //是否授权
        if (isauth) {
            UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);//获取用户资料
        } else {
            //若未授权
            UMShareAPI.get(this).doOauthVerify(this, SHARE_MEDIA.WEIXIN, umAuthListener);
        }
    }

    /**
     * 显示微信登录对话框
     */
    private void showWxLoginDialog() {
        if (mDialogBuilder.mProgressDialog != null) {
            mDialogBuilder.mProgressDialog.dismiss();
        }
        mDialogBuilder.showProgressDialog(LoginActivityV2.this, "正在登录...", false);
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int gender = 2;
                String pushId = "";
                if (mWxUserInfo.gender.equals("男")) {
                    gender = 1;
                }
                RequestParams params = RequestParamsBuilder.buildLoginWithWxRP(LoginActivityV2.this, UrlConfig.URL_WX_LOGIN,
                        mWxUserInfo.unionid, gender, mWxUserInfo.iconurl, pushId, mWxUserInfo.screen_name);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mLoginResult = JSON.parseObject(result.result, UserInfoRE.class);
                            mLocalHandler.sendEmptyMessage(MSG_LOGIN_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_LOGIN_ERROR;
                            msg.obj = result.errormsg;
                            mLocalHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_LOGIN_ERROR;
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
     * 检查用户是否需要绑定手机号
     */
    private void checkUserIsNeedBindPhone() {
        //保存用户信息
        UserDE userDE = UserDBData.WxLogin(LoginActivityV2.this, mLoginResult);
        //未绑定手机号
        if (mLoginResult.mobile.equals("")) {
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userDE.userId);
            startActivity(LoginBindPhoneActivity.class,bundle);
        } else {
            UserSPData.setHasLogin(LoginActivityV2.this, true);
            UserSPData.setUserId(LoginActivityV2.this, userDE.userId);
            LocalApplication.getInstance().mLoginUser = userDE;
            startActivity(MainActivityV2.class);
        }
    }
}
