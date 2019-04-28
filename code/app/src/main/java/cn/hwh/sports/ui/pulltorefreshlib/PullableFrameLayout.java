package cn.hwh.sports.ui.pulltorefreshlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by LY on 2016/5/5.
 */
public class PullableFrameLayout extends FrameLayout implements Pullable {

    private RecyclerView recyclerView;

    public PullableFrameLayout(Context context) {
        super(context);
    }

    public PullableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getScrollY() >= getHeight() - getMeasuredHeight())
            return true;
        else
            return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
