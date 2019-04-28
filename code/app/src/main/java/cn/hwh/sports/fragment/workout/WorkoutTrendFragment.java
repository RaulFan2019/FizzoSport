package cn.hwh.sports.fragment.workout;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.ui.common.ObservableWebView;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2017/4/26.
 */

public abstract class WorkoutTrendFragment extends BaseFragment {


    /* local view */
    @BindView(R.id.wv_rest_hr)
    ObservableWebView mRestHrWv;
//    @BindView(R.id.pb_wv_loading)
//    ProgressBar mWebLoadPb;

    /* data */
    private String mRequestURL;

    protected int type;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout_trend;
    }

    @Override
    protected void initParams() {
        type = getType();
        UserDE lLoginUser = LocalApplication.getInstance().mLoginUser;


//        mRequestURL = "http://192.168.1.126:8080/?userid=" + lLoginUser.userId +
//                "&sessionid="+ lLoginUser.sessionId + "&id="+ lLoginUser.userId
//                + "&period=" + type +"#";
        mRequestURL = UrlConfig.getWebHostIp() + "index.html?cp=ExerciseTrend&userid=" +
                lLoginUser.userId + "&sessionid=" + lLoginUser.sessionId + "&id=" + lLoginUser.userId
                + "&period=" + type;
//        mRequestURL = "http://www.baidu.com";
        Log.v("WorkoutTrendFragment", "mRequestURL:" + mRequestURL);
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

        mRestHrWv.setWebViewClient(new WebViewClient() {

        });
        mRestHrWv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                if (mWebLoadPb != null) {
//                    mWebLoadPb.setProgress(newProgress);
//                    if (newProgress == 100) {
//                        mWebLoadPb.setVisibility(View.GONE);
//                    }
                    //调用url时调用网页javaScript方法
//                mActivityWv.loadUrl("javascript:login('"+loginToken+"','"+userId+"','"+type+"','"+activityId+"')");
//                }
                super.onProgressChanged(view, newProgress);
            }
        });
        mRestHrWv.loadUrl(mRequestURL);
    }

    @Override
    protected void causeGC() {
//        mRestHrWv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
    }

    @Override
    protected void onVisible() {
        try {
            mRestHrWv.getClass().getMethod("onResume").invoke(mRestHrWv, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onInVisible() {
        try {
            mRestHrWv.getClass().getMethod("onPause").invoke(mRestHrWv, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化布局
     **/
    protected abstract int getType();
}
