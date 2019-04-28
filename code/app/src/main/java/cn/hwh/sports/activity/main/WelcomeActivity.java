package cn.hwh.sports.activity.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.login.LoginActivity;
import cn.hwh.sports.activity.login.LoginActivityV2;
import cn.hwh.sports.activity.login.LoginPerfectUserActivity;
import cn.hwh.sports.activity.settings.UserInfoSettingActivity;
import cn.hwh.sports.activity.test.TestEntranceActivity;
import cn.hwh.sports.config.MyBuildConfig;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.service.CheckAppVersionIntentService;
import cn.hwh.sports.service.SendCrashLogService;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/11/10.
 */

public class WelcomeActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "WelcomeActivity";

    @BindView(R.id.v_anim)
    View mAnimV;

    @Override
    protected void initData() {
        mCheckNewData = false;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        animLaunch();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_welcome;
    }

    @Override
    protected void doMyCreate() {
        if (!isTaskRoot()) {
            finish();
            return;
        }
        //获取个人信息
        if (UserSPData.hasLogin(LocalApplication.applicationContext) &&
                LocalApplication.getInstance().getLoginUser(WelcomeActivity.this) != null) {
            postGetUserInfo();
        }
        //获取崩溃信息
        Intent crashI = new Intent(WelcomeActivity.this, SendCrashLogService.class);
        startService(crashI);

        //启动检查APP版本
        Intent checkUpdateIntent = new Intent(this, CheckAppVersionIntentService.class);
        startService(checkUpdateIntent);
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 页面启动动画
     */
    private void animLaunch() {
        AlphaAnimation lAnim = new AlphaAnimation(0.1f, 1.0f);
        lAnim.setDuration(2000);

        if (mAnimV == null || lAnim == null) {
            launchNext();
            return;
        }
        mAnimV.startAnimation(lAnim);
        lAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                launchNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    /**
     * 根据不同的情况启动页面
     */
    private void launchNext() {
        if (MyBuildConfig.TEST) {
            startActivity(TestEntranceActivity.class);
            return;
        }
        //判断是否已经登录过
        if (UserSPData.hasLogin(LocalApplication.applicationContext)) {
            //检查数据库是否有数据
            UserDE user = UserDBData.getUserById(UserSPData.getUserId(LocalApplication.applicationContext));
//            Log.v(TAG,"user == null:" + (user == null));
            //用户数据不存在
            if (user == null) {
                startActivity(LoginActivityV2.class);
            } else {
                startActivity(MainActivityV2.class);
            }
        } else {
            //未登录，进入登录页面
            startActivity(LoginActivityV2.class);
        }
    }

    /**
     * 获取个人信息
     */
    private void postGetUserInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                //开始请求服务器
                RequestParams requestParams = RequestParamsBuilder.buildGetUserInfoRP(WelcomeActivity.this,
                        UrlConfig.URL_GET_USER_INFO, UserSPData.getUserId(LocalApplication.applicationContext));
                x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            UserInfoRE user = JSON.parseObject(result.result, UserInfoRE.class);
                            UserDE userDE = LocalApplication.getInstance().getLoginUser(WelcomeActivity.this);
                            user.sessionid = userDE.sessionId;
                            UserDBData.CommonLogin(WelcomeActivity.this, user);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

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
