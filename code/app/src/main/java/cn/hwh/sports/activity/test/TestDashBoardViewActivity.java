package cn.hwh.sports.activity.test;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;

/**
 * Created by Raul.Fan on 2017/4/17.
 */

public class TestDashBoardViewActivity extends BaseActivity {


    @BindView(R.id.iv_tester)
    ImageView ivTester;
    @BindView(R.id.iv_needle)
    ImageView ivNeedle;
    @BindView(R.id.iv_heart)
    ImageView ivHeart;
    @BindView(R.id.aa)
    RelativeLayout aa;

    private long begin = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_dash_board;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {
        startAnimation(50);
    }

    @Override
    protected void causeGC() {

    }

    protected void startAnimation(double d) {
        AnimationSet animationSet = new AnimationSet(true);
        /**
         * 前两个参数定义旋转的起始和结束的度数，后两个参数定义圆心的位置
         */
        // Random random = new Random();
        int end = getDuShu(d);

        Log.i("", "********************begin:" + begin + "***end:" + end);
        RotateAnimation rotateAnimation = new RotateAnimation(begin, end, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation.setDuration(1000);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);
        ivNeedle.startAnimation(animationSet);
        begin = end;
    }

    public int getDuShu(double number) {
        double a = number * 180 / 50;

//        if (number >= 0 && number <= 512) {
//            a = number / 128 * 15;
//        } else if (number > 521 && number <= 1024) {
//            a = number / 256 * 15 + 30;
//        } else if (number > 1024 && number <= 10 * 1024) {
//            a = number / 512 * 5 + 80;
//        } else {
//            a = 180;
//        }
        return (int) a;
    }

}
