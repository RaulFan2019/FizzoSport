package cn.hwh.sports.config;

import android.graphics.Color;

import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Raul on 2016/2/5.
 */
public class MapEnum {


    //图层关系
    public static final float ZINDEX_POLYLINE = 3.0f;//轨迹
    public static final float ZINDEX_MASK = 2.0f;//蒙版
    public static final float ZINDEX_SPLITE_IMAGE = 4.0f;//分段背景
    public static final float ZINDEX_START_END = 4.0f;//开始结束标记
    public static final float ZINDEX_SPLITE_TEXT = 5.0f;//分段文字
    public static final float ZINDEX_SOCIAL = 6.0f;//评论层文字
    public static final float ZINDEX_SOCIAL_BIG = 7.0f;//评论层文字
    //大小
    public static final float WIDTH_POLYLINE = DeviceU.dpToPixel(5);//轨迹宽度


    //颜色
    public static final int COLOR_POLYLINE = Color.parseColor("#ffc445");
    public static final int COLOR_RUNNING_POLYLINE = Color.parseColor("#cfcfcf");
    public static final int COLOR_PAUSE_LINE = Color.parseColor("#00ffc445");
    public static final int COLOR_MASK = Color.argb(150, 1, 1, 1);
    public static final int COLOR_MASK_SECRET = Color.argb(255, 255, 255, 255);

    //字体
    public static final int FONT_SIZE_SPLITE = (int)DeviceU.dpToPixel(10);
    public static final int STROKE_WIDTH_SPLITE = (int)DeviceU.dpToPixel(2);
}
