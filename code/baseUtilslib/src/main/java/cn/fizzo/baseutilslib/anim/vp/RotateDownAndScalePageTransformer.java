package cn.fizzo.baseutilslib.anim.vp;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

public class RotateDownAndScalePageTransformer extends BasePageTransformer {
    private static final float DEFAULT_MAX_ROTATE = 0.1f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;

    private static final float DEFAULT_MIN_SCALE = 0.75f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public static final float DEFAULT_CENTER = 0.5f;

    public RotateDownAndScalePageTransformer() {
    }

    public RotateDownAndScalePageTransformer(float maxRotate) {
        this(maxRotate, NonPageTransformer.INSTANCE);
    }

    public RotateDownAndScalePageTransformer(ViewPager.PageTransformer pageTransformer) {
        this(DEFAULT_MAX_ROTATE, pageTransformer);
    }

    public RotateDownAndScalePageTransformer(float maxRotate, ViewPager.PageTransformer pageTransformer) {
        mPageTransformer = pageTransformer;
        mMaxRotate = maxRotate;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void pageTransform(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        view.setPivotY(pageHeight / 2);
        view.setPivotX(pageWidth / 2);
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.  
            view.setRotation(mMaxRotate * -1);
            view.setPivotX(view.getWidth());
            view.setPivotY(view.getHeight());

            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setPivotX(pageWidth);

        } else if (position <= 1) { // [-1,1]
            //[0，-1]
            if (position < 0){
                view.setPivotX(view.getWidth() * (DEFAULT_CENTER + DEFAULT_CENTER * (-position)));
                view.setPivotY(view.getHeight());
                view.setRotation((float) (mMaxRotate * position * 0.5));

                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
                //[1,0]
            } else {
                view.setPivotX(view.getWidth() * DEFAULT_CENTER * (1 - position));
                view.setPivotY(view.getHeight());
                view.setRotation((float) (mMaxRotate * position * 0.5));

                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
            }
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.  
            view.setRotation((float) (mMaxRotate * 0.5));
            view.setPivotX(view.getWidth() * 0);
            view.setPivotY(view.getHeight());

            view.setPivotX(0);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
        }
    }
}  