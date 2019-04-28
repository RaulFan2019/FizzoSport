package cn.hwh.sports.activity.monitor;

import android.os.Bundle;

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

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.ui.common.ObservableWebView;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Administrator on 2016/11/18.
 */

public class HealthSignChangeInfoActivity extends BaseActivity {
    @BindView(R.id.wv_health_sign)
    ObservableWebView mHeightSignWv;
    @BindView(R.id.v_state_bar)
    View mStateBarV;
    @BindView(R.id.pb_wv_loading)
    ProgressBar mWebLoadPb;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_health_sign_change_info;
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

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        WebSettings settings = mHeightSignWv.getSettings();
        //启用JavaScrip支持
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        //不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);


        mHeightSignWv.setWebChromeClient(new WebChromeClient(){
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
        mHeightSignWv.loadUrl("http://www.baidu.com");
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置色块区域高度
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mStateBarV.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = DeviceU.getStatusBarHeight(this);
            mStateBarV.setLayoutParams(layoutParams);
        }else {
            mStateBarV.setVisibility(View.GONE);
        }
    }

    @Override
    protected void causeGC() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mHeightSignWv.canGoBack()) {
                mHeightSignWv.goBack();//返回上一页面
                return true;
            } else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
