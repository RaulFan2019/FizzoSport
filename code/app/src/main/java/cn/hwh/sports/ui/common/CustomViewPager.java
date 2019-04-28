package cn.hwh.sports.ui.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rey.material.widget.Slider;
import com.rey.material.widget.Switch;

/**
 * Viewpager 适配器
 * Created by Rey on 3/18/2015.
 */
public class CustomViewPager extends ViewPager {


    private static final String TAG = "CustomViewPager";

    private boolean noScroll = false;
    private View mView;

    public void setView(View View) {
        this.mView = View;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return super.onTouchEvent(arg0);
    }

    //
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll && mView != null) {
            mView.onTouchEvent(arg0);
            return false;

        }
        return super.onInterceptTouchEvent(arg0);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (noScroll && mView != null) {
            mView.dispatchTouchEvent(ev);
            return false;

        }
        return super.dispatchTouchEvent(ev);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//        if (v.getClass().getName().equals("com.app.pao.ui.widget.LinearLayoutNoScroll")) {
//            return true;
//        }
        if (v.getClass().getName().equals("com.amap.api.maps.MapView")) {
            return true;
        }
//        //if(v instanceof MapView){
//        //    return true;
//        //}
        return super.canScroll(v, checkV, dx, x, y);
    }

    protected boolean customCanScroll(View v) {
        if (v instanceof Slider || v instanceof Switch)
            return true;
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        int height = 0;
//        for(int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if(h > height) height = h;
//        }
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
