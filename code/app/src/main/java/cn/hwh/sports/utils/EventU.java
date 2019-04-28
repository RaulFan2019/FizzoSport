package cn.hwh.sports.utils;


import org.greenrobot.eventbus.EventBus;

import cn.hwh.sports.LocalApplication;

/**
 * Created by Administrator on 2016/7/4.
 * Event事件工具
 */
public class EventU {

    /**
     * 发送前台事件
     */
    public static void sendForegroundEvent(final Object event) {
        if (LocalApplication.getInstance().isActive) {
            EventBus.getDefault().post(event);
        }
    }


    /**
     * 发送事件
     */
    public static void sendEvent(final Object event) {
        EventBus.getDefault().post(event);
    }
}
