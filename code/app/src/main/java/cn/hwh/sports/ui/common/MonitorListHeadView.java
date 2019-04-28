package cn.hwh.sports.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.adapter.StepDetailListAE;
import cn.hwh.sports.entity.model.MonitorListHeadME;
import cn.hwh.sports.utils.StringU;

/**
 * Created by Raul.Fan on 2016/12/13.
 */

public class MonitorListHeadView extends LinearLayout {

    MonitorChart mChart;
    LinearLayout mTipLl;
    TextView mTipTv;
    View mHeadTipImgV;

    public MonitorListHeadView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_monitor_list_head, this, true);

        mChart = (MonitorChart) findViewById(R.id.monitor_chart);
        mTipLl = (LinearLayout) findViewById(R.id.ll_details_list_tip);
        mTipTv = (TextView) findViewById(R.id.tv_head_tip);
        mHeadTipImgV = findViewById(R.id.v_head_tip_img);
    }

    public MonitorListHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_monitor_list_head, this, true);

        mChart = (MonitorChart) findViewById(R.id.monitor_chart);
        mTipLl = (LinearLayout) findViewById(R.id.ll_details_list_tip);
        mTipTv = (TextView) findViewById(R.id.tv_head_tip);
        mHeadTipImgV = findViewById(R.id.v_head_tip_img);
    }

    /**
     * head item
     *
     * @param monitorListHeadME
     */
    public void bindHeadView(final MonitorListHeadME monitorListHeadME) {
        mChart.setData(monitorListHeadME);
        if (monitorListHeadME.type == MonitorListHeadME.TYPE_STEP){
            mTipTv.setText(R.string.tip_step_detail_list);
            mHeadTipImgV.setBackgroundResource(R.drawable.ic_step_blue);
        }else if (monitorListHeadME.type == MonitorListHeadME.TYPE_LENGTH){
            mTipTv.setText(R.string.tip_distance_detail_list);
            mHeadTipImgV.setBackgroundResource(R.drawable.ic_length_blue);
        }else if (monitorListHeadME.type == MonitorListHeadME.TYPE_SPORT_TIME){
            mTipTv.setText(R.string.tip_sport_time_detail_list);
            mHeadTipImgV.setBackgroundResource(R.drawable.ic_sport_time_blue);
        }else if (monitorListHeadME.type == MonitorListHeadME.TYPE_SPORT_POINT){
            mTipTv.setText(R.string.tip_point_detail_list);
            mHeadTipImgV.setBackgroundResource(R.drawable.ic_sport_point_blue);
        }else if (monitorListHeadME.type == MonitorListHeadME.TYPE_CALORIE){
            mTipTv.setText(R.string.tip_calorie_detail_list);
            mHeadTipImgV.setBackgroundResource(R.drawable.ic_calorie_blue);
        }
    }

    public void setTarget(final float targetValue){
        mChart.setTarget(targetValue);
    }
}
