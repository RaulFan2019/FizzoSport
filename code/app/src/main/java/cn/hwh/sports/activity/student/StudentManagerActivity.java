package cn.hwh.sports.activity.student;

import android.os.Build;
import android.os.Bundle;
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
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.ui.common.ObservableWebView;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Administrator on 2016/11/30.
 */

public class StudentManagerActivity extends BaseActivity {
    private static final String TAG = "StudentReportActivity";


    @BindView(R.id.wv_student_report)
    ObservableWebView mStudentReportWv;
    @BindView(R.id.pb_wv_loading)
    ProgressBar mWebLoadPb;
    @BindView(R.id.tv_student_report_title)
    TextView mTitleTv;
    @BindView(R.id.tv_report_date_title)
    TextView mTitleDateTv;

    /* data */
    private String mRequestURL;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_manager;
    }

    @OnClick({R.id.btn_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (mStudentReportWv.canGoBack()) {
                    mStudentReportWv.goBack();//返回上一页面
                } else {
                    finish();//退出
                }
                break;
        }
    }

    @Override
    protected void initData() {
        UserDE lLoginUser = LocalApplication.getInstance().mLoginUser;

        mRequestURL = UrlConfig.getWebHostIp() + "trainee.html?userid=" +
                lLoginUser.userId + "&sessionid=" + lLoginUser.sessionId + "&Id=" + lLoginUser.userId;
//        + "&month=" + TimeU.formatDateToStr(new Date(),TimeU.FORMAT_TYPE_6);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        WebSettings settings = mStudentReportWv.getSettings();
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

        mStudentReportWv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = mStudentReportWv.getTitle();
                if (title.contains(".html")){
                    return;
                }
                if (title.contains("#")) {
                    String[] lTitle = title.split("#", 2);
                    mTitleTv.setText(lTitle[0]);
                    mTitleDateTv.setVisibility(View.VISIBLE);
                    mTitleDateTv.setText(lTitle[1]);
                } else {
                    mTitleDateTv.setVisibility(View.GONE);
                    mTitleTv.setText(title);
                }
            }
        });
        mStudentReportWv.setWebChromeClient(new WebChromeClient() {
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
                if (title.contains("#")) {
                    String[] lTitle = title.split("#", 2);
                    mTitleTv.setText(lTitle[0]);
                    mTitleDateTv.setVisibility(View.VISIBLE);
                    mTitleDateTv.setText(lTitle[1]);
                } else {
                    mTitleDateTv.setVisibility(View.GONE);
                    mTitleTv.setText(title);
                }
            }
        });
        mStudentReportWv.loadUrl(mRequestURL);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mStudentReportWv.getClass().getMethod("onResume").invoke(mStudentReportWv, (Object[]) null);
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
            mStudentReportWv.getClass().getMethod("onPause").invoke(mStudentReportWv, (Object[]) null);
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
        mStudentReportWv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mStudentReportWv.canGoBack()) {
                mStudentReportWv.goBack();//返回上一页面
                return true;
            } else {
                finish();//退出
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
