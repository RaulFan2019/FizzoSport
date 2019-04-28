package cn.hwh.sports.fragment.workout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.HrZoneU;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class WorkoutIndoorHrFragment extends BaseFragment {


    /* local view */
    @BindView(R.id.chart_small)
    PieChart chartSmall;
    @BindView(R.id.chart_big)
    PieChart chartBig;
    @BindView(R.id.tv_curr_pie_percent)
    TextView tvCurrPiePercent;
    @BindView(R.id.tv_curr_pie_zone)
    TextView tvCurrPieZone;
    @BindView(R.id.tv_curr_pie_time)
    TextView tvCurrPieTime;
    @BindView(R.id.tv_graphic_range_0)
    TextView tvGraphicRange0;
    @BindView(R.id.tv_graphic_range_1)
    TextView tvGraphicRange1;
    @BindView(R.id.tv_graphic_range_2)
    TextView tvGraphicRange2;
    @BindView(R.id.tv_graphic_range_3)
    TextView tvGraphicRange3;
    @BindView(R.id.tv_graphic_range_4)
    TextView tvGraphicRange4;
    @BindView(R.id.tv_graphic_range_5)
    TextView tvGraphicRange5;
    @BindView(R.id.tv_zone_time_0)
    TextView tvZoneTime0;
    @BindView(R.id.tv_zone_time_1)
    TextView tvZoneTime1;
    @BindView(R.id.tv_zone_time_2)
    TextView tvZoneTime2;
    @BindView(R.id.tv_zone_time_3)
    TextView tvZoneTime3;
    @BindView(R.id.tv_zone_time_4)
    TextView tvZoneTime4;
    @BindView(R.id.tv_zone_time_5)
    TextView tvZoneTime5;
    @BindView(R.id.tv_consume)
    TextView tvConsume;
    @BindView(R.id.tv_effective_time)
    TextView tvEffectiveTime;
    @BindView(R.id.tv_avg_power)
    TextView tvAvgPower;

    /* local data */
    private String mWorkoutStartTime;
    private WorkoutDE mWorkoutDe;
    private UserDE mUserDe;

    private List<TimeSplitDE> mTimeSplits = new ArrayList<>();
    int zone0 = 0, zone1 = 0, zone2 = 0, zone3 = 0, zone4 = 0, zone5 = 0;
    int maxZone = 0;
    int maxZoneValue = zone0;

    private Typeface typeFace;//字体

    public static WorkoutIndoorHrFragment newInstance() {
        WorkoutIndoorHrFragment fragment = new WorkoutIndoorHrFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout_hr;
    }

    @Override
    protected void initParams() {
        mUserDe = LocalApplication.getInstance().getLoginUser(getActivity());
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");

        tvConsume.setTypeface(typeFace);
        tvEffectiveTime.setTypeface(typeFace);
        tvAvgPower.setTypeface(typeFace);
        tvCurrPiePercent.setTypeface(typeFace);


        //设置心率范围文本
        tvGraphicRange0.setText((int) (mUserDe.maxHr * 0.2) + "~" + (int) (mUserDe.maxHr * 0.5 + 1));
        tvGraphicRange1.setText((int) (mUserDe.maxHr * 0.5) + "~" + (int) (mUserDe.maxHr * 0.6 + 1));
        tvGraphicRange2.setText((int) (mUserDe.maxHr * 0.6) + "~" + (int) (mUserDe.maxHr * 0.7 + 1));
        tvGraphicRange3.setText((int) (mUserDe.maxHr * 0.7) + "~" + (int) (mUserDe.maxHr * 0.8 + 1));
        tvGraphicRange4.setText((int) (mUserDe.maxHr * 0.8) + "~" + (int) (mUserDe.maxHr * 0.9 + 1));
        tvGraphicRange5.setText((int) (mUserDe.maxHr * 0.9) + "~" + (int) (mUserDe.maxHr));

        initPieChart();
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        if (mWorkoutDe != null){
            updateZoneView();
        }
    }

    @Override
    protected void onInVisible() {

    }

    /**
     * activity 调用更新
     */
    public void updateViewByActivity(final String workoutStartTime) {
        mWorkoutStartTime = workoutStartTime;
        updateWorkoutData();
        updateZoneView();
    }

    /**
     * 更新数据
     */
    private void updateWorkoutData() {
        mWorkoutDe = WorkoutDBData.getWorkoutByStartTime(mUserDe.userId, mWorkoutStartTime);
        mTimeSplits = TimeSplitDBData.getSplitsByWorkout(mWorkoutDe);
        for (TimeSplitDE split : mTimeSplits) {
            int zone = EffortPointU.getZone(split.avgHr * 100 / mUserDe.maxHr);
            switch (zone) {
                case 0:
                    zone0++;
                    break;
                case 1:
                    zone1++;
                    break;
                case 2:
                    zone2++;
                    break;
                case 3:
                    zone3++;
                    break;
                case 4:
                    zone4++;
                    break;
                case 5:
                    zone5++;
                    break;
            }
        }
        if (zone1 > maxZoneValue) {
            maxZone = 1;
            maxZoneValue = zone1;
        }
        if (zone2 > maxZoneValue) {
            maxZone = 2;
            maxZoneValue = zone2;
        }
        if (zone3 > maxZoneValue) {
            maxZone = 3;
            maxZoneValue = zone3;
        }
        if (zone4 > maxZoneValue) {
            maxZone = 4;
            maxZoneValue = zone4;
        }
        if (zone5 > maxZoneValue) {
            maxZone = 5;
            maxZoneValue = zone5;
        }
    }

    /**
     * 更新心率区间相关的页面
     */
    private void updateZoneView() {
        setPieChartSelectData(maxZone, maxZoneValue);
        //汇总数据.
        tvConsume.setText((int)mWorkoutDe.calorie+"");
        tvEffectiveTime.setText((zone2 + zone3 + zone4 + zone5) + "");
        tvAvgPower.setText(mWorkoutDe.avgHr * 100 / mUserDe.maxHr + "");
        tvZoneTime0.setText(zone0 + "分钟");
        tvZoneTime1.setText(zone1 + "分钟");
        tvZoneTime2.setText(zone2 + "分钟");
        tvZoneTime3.setText(zone3 + "分钟");
        tvZoneTime4.setText(zone4 + "分钟");
        tvZoneTime5.setText(zone5 + "分钟");
    }


    /**
     * 初始化饼状图
     */
    private void initPieChart() {
        chartBig.setUsePercentValues(true);
        chartBig.getDescription().setEnabled(false);
        chartBig.setDrawHoleEnabled(true);
        chartBig.setHoleColor(Color.TRANSPARENT);

        chartBig.setTransparentCircleColor(Color.TRANSPARENT);
        chartBig.setTransparentCircleAlpha(110);

        chartBig.setHoleRadius(74f);
        chartBig.setTransparentCircleRadius(74f);

        chartBig.setDrawCenterText(true);

        chartBig.setRotationAngle(0);
        // enable rotation of the chart by touch
        chartBig.setRotationEnabled(true);
        chartBig.setHighlightPerTapEnabled(true);

        chartBig.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        chartBig.setDrawEntryLabels(false);

        chartBig.getLegend().setEnabled(false);//设置图例是否显示
        chartBig.getDescription().setEnabled(false);
        chartBig.setRotationEnabled(false);

        chartBig.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                setPieChartSelectData((int) h.getX(), (int) e.getY());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        chartSmall.setUsePercentValues(true);
        chartSmall.getDescription().setEnabled(false);
        chartSmall.setDrawHoleEnabled(true);
        chartSmall.setHoleColor(Color.TRANSPARENT);

        chartSmall.setTransparentCircleColor(Color.TRANSPARENT);
        chartSmall.setTransparentCircleAlpha(110);

        chartSmall.setHoleRadius(86f);
        chartSmall.setTransparentCircleRadius(86f);

        chartSmall.setDrawCenterText(true);

        chartSmall.setRotationAngle(0);
        // enable rotation of the chart by touch
        chartSmall.setRotationEnabled(true);
        chartSmall.setHighlightPerTapEnabled(false);

        chartSmall.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        chartSmall.setDrawEntryLabels(false);

        chartSmall.getLegend().setEnabled(false);//设置图例是否显示
        chartSmall.getDescription().setEnabled(false);
        chartSmall.setRotationEnabled(false);
    }


    /**
     * 根据高亮区域显示pie chart
     *
     * @param zone
     */
    private void setPieChartSelectData(int zone, int zoneValue) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        ArrayList<Integer> bigColors = new ArrayList<Integer>();
        ArrayList<Integer> smallColors = new ArrayList<Integer>();

        entries.add(new PieEntry((float) zone0, ""));
        entries.add(new PieEntry((float) zone1, ""));
        entries.add(new PieEntry((float) zone2, ""));
        entries.add(new PieEntry((float) zone3, ""));
        entries.add(new PieEntry((float) zone4, ""));
        entries.add(new PieEntry((float) zone5, ""));

        smallColors.add(ColorU.getColorByZone(0));
        smallColors.add(ColorU.getColorByZone(1));
        smallColors.add(ColorU.getColorByZone(2));
        smallColors.add(ColorU.getColorByZone(3));
        smallColors.add(ColorU.getColorByZone(4));
        smallColors.add(ColorU.getColorByZone(5));

        for (int i = 0; i < 6; i++) {
            if (i == zone) {
                bigColors.add(ColorU.getColorByZone(i));
            } else {
                bigColors.add(Color.TRANSPARENT);
            }
        }

        PieDataSet smallDataSet = new PieDataSet(entries, "");
        smallDataSet.setSliceSpace(0f);
        smallDataSet.setSelectionShift(5f);
        smallDataSet.setColors(smallColors);
        //dataSet.setSelectionShift(0f);

        PieData smallData = new PieData(smallDataSet);
        smallData.setValueFormatter(null);

        chartSmall.setData(smallData);

        // undo all highlights
        chartSmall.highlightValues(null);

        for (IDataSet<?> set : chartSmall.getData().getDataSets()) {
            set.setDrawValues(false);
        }
        chartSmall.invalidate();

        PieDataSet bigDataSet = new PieDataSet(entries, "");
        bigDataSet.setSliceSpace(0f);
        bigDataSet.setSelectionShift(5f);
        bigDataSet.setColors(bigColors);
        //dataSet.setSelectionShift(0f);

        PieData bigData = new PieData(bigDataSet);
        bigData.setValueFormatter(null);

        chartBig.setData(bigData);

        // undo all highlights
        chartBig.highlightValues(null);

        for (IDataSet<?> set : chartBig.getData().getDataSets()) {
            set.setDrawValues(false);
        }
        chartBig.invalidate();

        if (mTimeSplits != null && mTimeSplits.size() > 0){
            tvCurrPiePercent.setText((int) zoneValue * 100 / mTimeSplits.size() +"");
        }else {
            tvCurrPiePercent.setText("0");
        }
        tvCurrPieZone.setText(HrZoneU.getDescribeByHrZone(zone));
        tvCurrPieTime.setText(zoneValue + "分钟");
    }

}
