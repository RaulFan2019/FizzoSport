package cn.hwh.sports.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import cn.hwh.sports.R;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2016/12/14.
 */

public class MonitorColumnView extends LinearLayout {

    private static final String TAG = "MonitorColumnView";

    LinearLayout mColumnValueLl;
    View mDoneV;
    View mShadowLl;


    public MonitorColumnView(Context context) {
        super(context);
    }

    public MonitorColumnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_monitor_column, this, true);

        mColumnValueLl = (LinearLayout) findViewById(R.id.ll_monitor_column_value);
        mDoneV = findViewById(R.id.v_monitor_column_done);
        mShadowLl = findViewById(R.id.v_monitor_column_shadow);
    }


    /**
     * 设置
     * @param value
     * @param targetValue
     */
    public void setValue(final float value, final float targetValue) {
//        Log.v(TAG,"value:" + value + ",targetValue:" + targetValue);
        if (value <= 0) {
            mDoneV.setVisibility(View.GONE);
            mShadowLl.setVisibility(View.GONE);
            mColumnValueLl.setVisibility(View.GONE);
        } else {
            mShadowLl.setVisibility(View.VISIBLE);
            if (value > targetValue) {
                mDoneV.setVisibility(View.VISIBLE);
            }else {
                mDoneV.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置最后一个高亮
     */
    public void setLastView(){
        mShadowLl.setBackgroundColor(Color.parseColor("#664239"));
        mColumnValueLl.setBackgroundResource(R.drawable.bg_monitor_column_selected);
        mDoneV.setBackgroundResource(R.drawable.ic_monitor_done);
    }
}
