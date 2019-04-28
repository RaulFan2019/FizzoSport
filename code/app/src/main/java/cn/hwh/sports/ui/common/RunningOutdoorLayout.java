package cn.hwh.sports.ui.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by machenike on 2017/6/1 0001.
 */

public class RunningOutdoorLayout extends LinearLayout {

    public boolean mMapState = false;

    public RunningOutdoorLayout(Context context) {
        super(context);
    }

    public RunningOutdoorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RunningOutdoorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mMapState;
    }
}
