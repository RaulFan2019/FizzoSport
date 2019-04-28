package cn.hwh.sports.activity.test;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;

/**
 * Created by machenike on 2017/5/26 0026.
 */

public class TestRunningBtnsAnimActivity extends BaseActivity {


    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.fl_finish_btn_layout)
    FrameLayout flFinishBtnLayout;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.fl_continue_btn_layout)
    FrameLayout flContinueBtnLayout;
    @BindView(R.id.btn_pause)
    Button btnPause;
    @BindView(R.id.paused_btn_layout)
    FrameLayout pausedBtnLayout;
    @BindView(R.id.include_running_btns)
    View mRunningPage;// 正在跑的界面
    @BindView(R.id.include_pause_btns)
    View mPausedPage;// 暂停界面

    private int fadeAnimationTime;
    private int mWidth;// 宽度

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_running_btns_anim;
    }

    @OnClick({R.id.btn_finish, R.id.btn_continue, R.id.btn_pause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_finish:
                break;
            case R.id.btn_continue:
                continueAnimation();
                break;
            case R.id.btn_pause:
                pauseAnimation();
                break;
        }
    }

    @Override
    protected void initData() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    /**
     * 暂停动画
     */
    private void pauseAnimation() {
        fadeAnimationTime = 0;
        AnimatorSet set = new AnimatorSet();
        AnimatorSet set1 = new AnimatorSet();
        fadeAnimation(mRunningPage, 1, 0, 300, 0);
        fadeAnimation(mPausedPage, 0, 1, 400, 100);
        set.playTogether(ObjectAnimator.ofFloat(flFinishBtnLayout, "translationX", 0, mWidth / 4),
                ObjectAnimator.ofFloat(flFinishBtnLayout, "alpha", 0, 1));
//                ObjectAnimator.ofFloat(flFinishBtnLayout, "scaleY", 1.5f, 1f),
//                ObjectAnimator.ofFloat(flFinishBtnLayout, "scaleX", 1.5f, 1f));
        set1.playTogether(ObjectAnimator.ofFloat(flContinueBtnLayout, "translationX", 0, -mWidth / 4),
                ObjectAnimator.ofFloat(flContinueBtnLayout, "alpha", 0, 1));
//                ObjectAnimator.ofFloat(flContinueBtnLayout, "scaleY", 1.5f, 1f),
//                ObjectAnimator.ofFloat(flContinueBtnLayout, "scaleX", 1.5f, 1f));
        set.setStartDelay(0);
        set1.setStartDelay(0);
        set.setDuration(500).start();
        set1.setDuration(500).start();
        mPausedPage.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mRunningPage.setVisibility(View.GONE);
            }
        }, 300);
    }

    /**
     * 继续动画
     */
    private void continueAnimation() {
        fadeAnimationTime = 0;
        AnimatorSet set = new AnimatorSet();
        AnimatorSet set1 = new AnimatorSet();
        fadeAnimation(mRunningPage, 0, 1, 800, 200);
        fadeAnimation(mPausedPage, 1, 0, 500, 0);
        set.playTogether(ObjectAnimator.ofFloat(flFinishBtnLayout, "translationX", mWidth / 4, 0),
                ObjectAnimator.ofFloat(flFinishBtnLayout, "alpha", 1, 0));
//                ObjectAnimator.ofFloat(flFinishBtnLayout, "scaleY", 1f, 1.5f),
//                ObjectAnimator.ofFloat(flFinishBtnLayout, "scaleX", 1f, 1.5f));
        set1.playTogether(ObjectAnimator.ofFloat(flContinueBtnLayout, "translationX", -mWidth / 4, 0),
                ObjectAnimator.ofFloat(flContinueBtnLayout, "alpha", 1, 0));
//                ObjectAnimator.ofFloat(flContinueBtnLayout, "scaleY", 1f, 1.5f),
//                ObjectAnimator.ofFloat(flContinueBtnLayout, "scaleX", 1f, 1.5f));
        set.setStartDelay(0);
        set1.setStartDelay(0);
        set.setDuration(500).start();
        set1.setDuration(500).start();
        mRunningPage.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mPausedPage.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 动画
     *
     * @param view
     * @param from
     * @param to
     * @param durationMillis
     * @param delayMillis
     */
    private void fadeAnimation(final View view, final float from, final float to, final long durationMillis,
                               final long delayMillis) {
        AlphaAnimation animation = new AlphaAnimation(from, to);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                fadeAnimationTime++;
                if (fadeAnimationTime == 2) {

                }
            }
        });
        view.startAnimation(animation);
    }

}
