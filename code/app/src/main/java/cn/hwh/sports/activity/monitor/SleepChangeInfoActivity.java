package cn.hwh.sports.activity.monitor;

import android.os.Bundle;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.settings.SleepTargetSetActivity;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.ui.common.ObservableWebView;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Administrator on 2016/11/18.
 */

public class SleepChangeInfoActivity extends BaseActivity {

    @BindView(R.id.wv_sleep)
    ObservableWebView mSleepWv;
    @BindView(R.id.pb_wv_loading)
    ProgressBar mWebLoadPb;
    @BindView(R.id.tv_title)
    TextView mTitleTv;
    @BindView(R.id.ll_right_btn)
    LinearLayout mRightBtnLl;
    /* data */
    private String mRequestURL ;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sleep_change_info;
    }

    @OnClick({R.id.btn_back,R.id.ibtn_add_sleep_time,R.id.ibtn_set_sleep_target})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.ibtn_add_sleep_time:
                startActivity(RecordSleepActivity.class);
                break;
            case R.id.ibtn_set_sleep_target:
                startActivity(SleepTargetSetActivity.class);
                break;
        }
    }


    @Override
    protected void initData() {
        UserDE lLoginUser = LocalApplication.getInstance().mLoginUser;

        mRequestURL = UrlConfig.getWebHostIp()+"sleepRecord.html?userid="+
                lLoginUser.userId+"&sessionid="+lLoginUser.sessionId+"&Id="+lLoginUser.userId ;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        WebSettings settings = mSleepWv.getSettings();
        //启用JavaScrip支持
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        //硬件加速
        settings.setAllowFileAccess(true);
        //不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mSleepWv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = mSleepWv.getTitle();
                if (title.contains(".html")){
                    return;
                }
                if (!mSleepWv.getUrl().equals(mRequestURL)){
                    mTitleTv.setText(title);
                    mRightBtnLl.setVisibility(View.INVISIBLE);
                }else {
                    mTitleTv.setText(R.string.title_user_sleep);
                    mRightBtnLl.setVisibility(View.VISIBLE);
                }
            }
        });
        mSleepWv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mWebLoadPb.setProgress(newProgress);
                if (newProgress == 100) {
                    mWebLoadPb.setVisibility(View.GONE);

                    //调用url时调用网页javaScript方法
//                mActivityWv.loadUrl("javascript:login('"+loginToken+"','"+userId+"','"+type+"','"+activityId+"')");
                }
                super.onProgressChanged(view, newProgress);
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.contains(".html")){
                    return;
                }
                if (!mSleepWv.getUrl().equals(mRequestURL)){
                    mTitleTv.setText(title);
                    mRightBtnLl.setVisibility(View.INVISIBLE);
                }else {
                    mTitleTv.setText(R.string.title_user_sleep);
                    mRightBtnLl.setVisibility(View.VISIBLE);
                }
            }
        });
        mSleepWv.loadUrl(mRequestURL);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mSleepWv.getClass().getMethod("onResume").invoke(mSleepWv, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mSleepWv.getClass().getMethod("onPause").invoke(mSleepWv, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void causeGC() {
        mSleepWv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSleepWv.canGoBack()) {
                mSleepWv.goBack();//返回上一页面
                return true;
            } else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
