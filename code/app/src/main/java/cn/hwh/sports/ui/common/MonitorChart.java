package cn.hwh.sports.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.entity.model.MonitorListHeadME;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2016/12/14.
 */

public class MonitorChart extends FrameLayout {

    private static final String TAG = "MonitorChart";

    /* week tv */
    TextView mWeekTv0;
    TextView mWeekTv1;
    TextView mWeekTv2;
    TextView mWeekTv3;
    TextView mWeekTv4;
    TextView mWeekTv5;
    TextView mWeekTv6;

    List<TextView> mWeekTvs = new ArrayList<>();
    /* column view */
    MonitorColumnView mColumn0;
    MonitorColumnView mColumn1;
    MonitorColumnView mColumn2;
    MonitorColumnView mColumn3;
    MonitorColumnView mColumn4;
    MonitorColumnView mColumn5;
    MonitorColumnView mColumn6;

    List<MonitorColumnView> mColumns = new ArrayList<>();

    /* line view */
    TextView mMaxTv;
    TextView mMiddleTv;
    TextView mTargetTv;
    LinearLayout mTargetLl;

    /* data */
    int maxValue;
    int middleValue;
    float targetValue;

    public MonitorChart(Context context) {
        super(context);
    }

    public MonitorChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_monitor_chart, this, true);

        /* week tv view */
        mWeekTv0 = (TextView) findViewById(R.id.tv_week_0);
        mWeekTv1 = (TextView) findViewById(R.id.tv_week_1);
        mWeekTv2 = (TextView) findViewById(R.id.tv_week_2);
        mWeekTv3 = (TextView) findViewById(R.id.tv_week_3);
        mWeekTv4 = (TextView) findViewById(R.id.tv_week_4);
        mWeekTv5 = (TextView) findViewById(R.id.tv_week_5);
        mWeekTv6 = (TextView) findViewById(R.id.tv_week_6);

        mWeekTvs.add(mWeekTv0);
        mWeekTvs.add(mWeekTv1);
        mWeekTvs.add(mWeekTv2);
        mWeekTvs.add(mWeekTv3);
        mWeekTvs.add(mWeekTv4);
        mWeekTvs.add(mWeekTv5);
        mWeekTvs.add(mWeekTv6);

        mColumn0 = (MonitorColumnView) findViewById(R.id.cv_0);
        mColumn1 = (MonitorColumnView) findViewById(R.id.cv_1);
        mColumn2 = (MonitorColumnView) findViewById(R.id.cv_2);
        mColumn3 = (MonitorColumnView) findViewById(R.id.cv_3);
        mColumn4 = (MonitorColumnView) findViewById(R.id.cv_4);
        mColumn5 = (MonitorColumnView) findViewById(R.id.cv_5);
        mColumn6 = (MonitorColumnView) findViewById(R.id.cv_6);

        mColumns.add(mColumn0);
        mColumns.add(mColumn1);
        mColumns.add(mColumn2);
        mColumns.add(mColumn3);
        mColumns.add(mColumn4);
        mColumns.add(mColumn5);
        mColumns.add(mColumn6);

        /* line view */
        mMaxTv = (TextView) findViewById(R.id.tv_max_line);
        mMiddleTv = (TextView) findViewById(R.id.tv_middle_line);
        mTargetTv = (TextView) findViewById(R.id.tv_target_line);
        mTargetLl = (LinearLayout) findViewById(R.id.ll_target);
    }


    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(final MonitorListHeadME data) {
        //如果一周最大数据， 大于一周目标数据
//        Log.v(TAG,"data.maxValue:" + data.maxValue);
//        Log.v(TAG,"data.targetValue:" + data.targetValue);
        if (data.maxValue > data.targetValue) {
            middleValue = (int) ((data.maxValue * 1.1) + 0.5) / 2;

        } else {
            middleValue = (int) ((data.targetValue * 1.1) + 0.5) / 2;
        }
        if (middleValue > 1000) {
            middleValue += 1000;
        }
        maxValue = middleValue * 2;

        //line view
        if (maxValue > 2000) {
            mMaxTv.setText(maxValue / 1000 + "k");
        } else {
            mMaxTv.setText(maxValue + "");
        }
        if (middleValue > 2000) {
            mMiddleTv.setText(middleValue / 1000 + "k");
        } else {
            mMiddleTv.setText(middleValue + "");
        }
        if (data.targetValue > 2000) {
            mTargetTv.setText(data.targetValue / 1000 + "k");
        } else {
            mTargetTv.setText(data.targetValue + "");
        }
        RelativeLayout.LayoutParams TargetLayoutParams = (RelativeLayout.LayoutParams) mTargetLl.getLayoutParams();
        TargetLayoutParams.setMargins((int) DeviceU.dpToPixel(8), (int) DeviceU.dpToPixel(176 - (176 * data.targetValue / maxValue)), (int) DeviceU.dpToPixel(8), 0);
        mTargetLl.setLayoutParams(TargetLayoutParams);
        //设置星期文本和柱状图
        for (int i = 0; i < 7; i++) {
            mWeekTvs.get(i).setText(data.dayList.get(i).weekDay);
            mColumns.get(i).setValue(data.dayList.get(i).value, data.targetValue);
            LinearLayout.LayoutParams columnLayoutParams = (LinearLayout.LayoutParams) mColumns.get(i).getLayoutParams();
            columnLayoutParams.height = (int) DeviceU.dpToPixel(176 * data.dayList.get(i).value / maxValue + 18.7f);
        }
        mColumns.get(6).setLastView();
    }


    public void setTarget(final float target){
        RelativeLayout.LayoutParams TargetLayoutParams = (RelativeLayout.LayoutParams) mTargetLl.getLayoutParams();
        TargetLayoutParams.setMargins((int) DeviceU.dpToPixel(8), (int) DeviceU.dpToPixel(176 - (176 * target / maxValue)), (int) DeviceU.dpToPixel(8), 0);
        mTargetLl.setLayoutParams(TargetLayoutParams);

        if (target > 2000) {
            mTargetTv.setText(target / 1000 + "k");
        } else {
            mTargetTv.setText(target + "");
        }
    }
}
