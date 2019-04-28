package cn.hwh.sports.ui.pulltorefreshlib;

import android.content.Context;
import android.util.AttributeSet;

import cn.hwh.sports.ui.common.PinnedSectionListView;

/**
 * Created by Raul.Fan on 2016/12/13.
 */

public class PullablePinnedSectionListView extends PinnedSectionListView implements Pullable {


    public PullablePinnedSectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullablePinnedSectionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean canPullDown() {
        //防止在加载数据时上拉崩溃
        if (getChildAt(0) == null) {
            return false;
        }

        if (getCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else if (getFirstVisiblePosition() == 0
                && getChildAt(0).getTop() >= 0) {
            // 滑到ListView的顶部了
            return true;
        } else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }
}
