package cn.hwh.sports.activity.monitor;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.ui.common.ObservableWebView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by Administrator on 2016/11/18.
 */

public class RestHRChangeInfoActivity extends BaseActivity {
    /*
    * view
    * */
    @BindView(R.id.wv_rest_hr)
    ObservableWebView mRestHrWv;
    @BindView(R.id.pb_wv_loading)
    ProgressBar mWebLoadPb;

    /* data */
    private String mRequestURL ;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rest_hr_change_info;
    }

    @OnClick({R.id.btn_back})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
        }
    }


    @Override
    protected void initData() {
        UserDE lLoginUser = LocalApplication.getInstance().mLoginUser;

        mRequestURL = UrlConfig.getWebHostIp()+"heartRate.html?userid="+
                lLoginUser.userId+"&sessionid="+lLoginUser.sessionId+"&Id="+lLoginUser.userId ;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        WebSettings settings = mRestHrWv.getSettings();
        //启用JavaScrip支持
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        //不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mRestHrWv.setWebViewClient(new WebViewClient(){

        });
        mRestHrWv.setWebChromeClient(new WebChromeClient(){
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
        });
        mRestHrWv.loadUrl(mRequestURL);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void causeGC() {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mRestHrWv.canGoBack()) {
                mRestHrWv.goBack();//返回上一页面
                return true;
            } else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
