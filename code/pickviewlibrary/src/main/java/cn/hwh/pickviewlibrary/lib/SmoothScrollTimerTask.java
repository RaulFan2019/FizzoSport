package cn.hwh.pickviewlibrary.lib;

import java.util.TimerTask;

final class SmoothScrollTimerTask extends TimerTask {

    int realTotalOffset;
    int realOffset;
    int offset;
    final WheelView loopView;

    SmoothScrollTimerTask(WheelView loopview, int offset) {
        this.loopView = loopview;
        this.offset = offset;
        realTotalOffset = Integer.MAX_VALUE;
        realOffset = 0;
    }

    @Override
    public final void run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset;
        }
        realOffset = (int) ((float) realTotalOffset * 0.1F);

        if (realOffset == 0) {
            if (realTotalOffset < 0) {
                realOffset = -1;
            } else {
                realOffset = 1;
            }
        }

        if (Math.abs(realTotalOffset) <= 1) {

            //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的 情况
            if (!loopView.isLoop) {
                float itemHeight = loopView.lineSpacingMultiplier * loopView.maxTextHeight;
                if (loopView.totalScrollY <= (int) ((float) (-loopView.initPosition) * itemHeight)) {
                    loopView.totalScrollY = (int) ((float) (-loopView.initPosition) * itemHeight);
                    loopView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
                } else if (loopView.totalScrollY >= (int) ((float) (loopView.getItemsCount() - 1 - loopView.initPosition) * itemHeight)) {
                    loopView.totalScrollY = (int) ((float) (loopView.getItemsCount() - 1 - loopView.initPosition) * itemHeight);
                    loopView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
                }
            }

            loopView.cancelFuture();
            loopView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);

        } else {
            loopView.totalScrollY = loopView.totalScrollY + realOffset;
            loopView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
            realTotalOffset = realTotalOffset - realOffset;
        }
    }
}