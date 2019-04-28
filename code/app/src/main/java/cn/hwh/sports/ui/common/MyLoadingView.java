package cn.hwh.sports.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.hwh.sports.R;

/**
 * Created by Raul.Fan on 2016/12/13.
 * 全局加载数据控件
 */
public class MyLoadingView extends RelativeLayout {

    /* view */
    LinearLayout mLoadingLl;
    View mLoadingV;
    View mLoadingAnimLineV;

    LinearLayout mErrorLl;
    TextView mErrorTv;

    /* anim */
    TranslateAnimation mLoadingAnim;
    ScaleAnimation mScaleAnim;



    public MyLoadingView(Context context) {
        super(context);
    }

    public MyLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_loading_data, this, true);

        mLoadingLl = (LinearLayout) findViewById(R.id.ll_loading);
        mLoadingV = findViewById(R.id.v_loading_anim);
        mLoadingAnimLineV = findViewById(R.id.v_loading_anim_line);

        mErrorLl = (LinearLayout) findViewById(R.id.ll_loading_error);
        mErrorTv = (TextView) findViewById(R.id.tv_load_error);

        mLoadingAnim = new TranslateAnimation(0, 0,0f, -85f);
        mLoadingAnim.setDuration(1000);
        mLoadingAnim.setRepeatCount(Animation.INFINITE);
        mLoadingAnim.setRepeatMode(Animation.REVERSE);

        mScaleAnim = new ScaleAnimation(1.0f,0.7f,1.0f,0.7f,Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnim.setDuration(1000);
        mScaleAnim.setRepeatCount(Animation.INFINITE);
        mScaleAnim.setRepeatMode(Animation.REVERSE);

        Loading();
    }

    /**
     * 加载
     */
    public void Loading() {
        mErrorLl.setVisibility(View.GONE);
        mLoadingLl.setVisibility(View.VISIBLE);

        mLoadingV.setAnimation(mLoadingAnim);
        mLoadingAnim.start();
        mLoadingAnimLineV.setAnimation(mScaleAnim);
        mScaleAnim.start();
    }

    /**
     * 加载错误
     *
     * @param error
     */
    public void LoadError(final String error) {
        mLoadingAnim.cancel();
        mLoadingV.clearAnimation();
        mScaleAnim.cancel();
        mLoadingAnimLineV.clearAnimation();
        mLoadingLl.setVisibility(View.GONE);
        mErrorLl.setVisibility(View.VISIBLE);
        mErrorTv.setText(error);
    }

    /**
     * 加载结束
     */
    public void loadFinish() {
        mLoadingAnim.cancel();
        mLoadingV.clearAnimation();
        mScaleAnim.cancel();
        mLoadingAnimLineV.clearAnimation();
        this.setVisibility(View.GONE);
    }
}
