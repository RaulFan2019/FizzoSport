package cn.hwh.sports.fragment.workout;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.LocationDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.ui.chart.MintTimeFormatter;
import cn.hwh.sports.ui.chart.TimerFormatter;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class WorkoutRunningOutdoorChartFragment extends BaseFragment {

    private static final String TAG = "WorkoutRunningOutdoorChartFragment";

    /* local view */
    @BindView(R.id.chart_hr)
    LineChart chartHr;
    @BindView(R.id.chart_cadence)
    LineChart chartCadence;
    @BindView(R.id.chart_altitude)
    LineChart chartAltitude;

    /* hr */
    @BindView(R.id.tv_hr_avg)
    TextView tvHrAvg;
    @BindView(R.id.tv_hr_max)
    TextView tvHrMax;

    /* Cadence */
    @BindView(R.id.tv_cadence_avg)
    TextView tvCadenceAvg;
    @BindView(R.id.tv_cadence_max)
    TextView tvCadenceMax;

    /* Altitude */
    @BindView(R.id.tv_altitude)
    TextView tvAltitude;

    /* power */
    @BindView(R.id.tv_power_avg)
    TextView tvPowerAvg;
    @BindView(R.id.tv_power_max)
    TextView tvPowerMax;
    @BindView(R.id.chart_power)
    BarChart chartPower;

    /* local data */
    private String mWorkoutStartTime;
    private WorkoutDE mWorkoutDe;
    private UserDE mUserDe;
    private List<HrDE> mHrDeList;
    private List<LocationDE> mLocList;
    private float mAltitudeUp;
    private int mCadenceMax;
    private int mHrAvg;

    private BarDataSet mBarSet;
    private List<TimeSplitDE> mTimeSplits = new ArrayList<>();
    private ArrayList<BarEntry> mBarData = new ArrayList<BarEntry>();

    private float mChartMarginTop, mChartMarginBottom, mChartMarginLeft, mChartMarginRight;

    public static WorkoutRunningOutdoorChartFragment newInstance() {
        WorkoutRunningOutdoorChartFragment fragment = new WorkoutRunningOutdoorChartFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout_running_outdoor_chart;
    }

    @Override
    protected void initParams() {
//        Log.v(TAG, "mUserDe == null:" + (mUserDe == null));
        mChartMarginTop = DeviceU.dpToPixel(10);
        mChartMarginBottom = DeviceU.dpToPixel(15);
        mChartMarginLeft = DeviceU.dpToPixel(25);
        mChartMarginRight = DeviceU.dpToPixel(0);
        initHrChart();
        initAltitudeChart();
        initCadenceChart();
        initPowerChart();
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        if (mWorkoutDe != null) {
            updateViews();
        }
    }

    @Override
    protected void onInVisible() {

    }

    /**
     * activity 调用更新
     */
    public void updateViewByActivity(final String workoutStartTime) {
//        Log.v(TAG,"updateViewByActivity");
        mWorkoutStartTime = workoutStartTime;
        updateWorkoutData();
        updateViews();
    }

    /**
     * 更新记录相关数据
     */
    private void updateWorkoutData() {
        mUserDe = LocalApplication.getInstance().getLoginUser(getActivity());
        mWorkoutDe = WorkoutDBData.getWorkoutByStartTime(mUserDe.userId, mWorkoutStartTime);
        mHrDeList = HrDBData.getHrListInWorkout(mWorkoutStartTime);
        mLocList = LocationDBData.getAfterLocFromWorkout(mWorkoutStartTime, -1);
        mTimeSplits = TimeSplitDBData.getSplitsByWorkout(mWorkoutDe);
    }

    private void updateViews() {
        setChartData();
        setHrView();
        setPowerView();
        setCadenceView();
        setAltitudeView();
    }

    /**
     * 初始化chart
     */
    private void initHrChart() {
        chartHr.getLegend().setEnabled(false);//设置图例是否显示
        // no description text
        chartHr.getDescription().setEnabled(false);
        // enable touch gestures
        chartHr.setTouchEnabled(true);

        // enable scaling and dragging
        chartHr.setDragEnabled(true);
        chartHr.setScaleEnabled(false);
        chartHr.setDrawGridBackground(false);
        chartHr.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chartHr.setBackgroundColor(Color.TRANSPARENT);
        chartHr.setViewPortOffsets(mChartMarginLeft, mChartMarginTop, mChartMarginRight, mChartMarginBottom);

        XAxis xAxis = chartHr.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new MintTimeFormatter());
        xAxis.setLabelCount(5);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chartHr.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(5);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawZeroLine(true);

        chartHr.getAxisRight().setDrawZeroLine(false);
        chartHr.getAxisRight().setDrawGridLines(false);
        chartHr.getAxisRight().setDrawAxisLine(false);
    }

    /**
     * 初始化chart
     */
    private void initCadenceChart() {
        chartCadence.getLegend().setEnabled(false);//设置图例是否显示
        // no description text
        chartCadence.getDescription().setEnabled(false);
        // enable touch gestures
        chartCadence.setTouchEnabled(true);

        // enable scaling and dragging
        chartCadence.setDragEnabled(true);
        chartCadence.setScaleEnabled(false);
        chartCadence.setDrawGridBackground(false);
        chartCadence.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chartCadence.setBackgroundColor(Color.TRANSPARENT);
        chartCadence.setViewPortOffsets(mChartMarginLeft, mChartMarginTop, mChartMarginRight, mChartMarginBottom);

        XAxis xAxis = chartCadence.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new MintTimeFormatter());
        xAxis.setLabelCount(5);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chartCadence.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(250f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(5);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawZeroLine(true);

        chartCadence.getAxisRight().setDrawZeroLine(false);
        chartCadence.getAxisRight().setDrawGridLines(false);
        chartCadence.getAxisRight().setDrawAxisLine(false);
    }


    /**
     * 初始化chart
     */
    private void initAltitudeChart() {
        chartAltitude.getLegend().setEnabled(false);//设置图例是否显示
        // no description text
        chartAltitude.getDescription().setEnabled(false);
        // enable touch gestures
        chartAltitude.setTouchEnabled(true);

        // enable scaling and dragging
        chartAltitude.setDragEnabled(true);
        chartAltitude.setScaleEnabled(false);
        chartAltitude.setDrawGridBackground(false);
        chartAltitude.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chartAltitude.setBackgroundColor(Color.TRANSPARENT);
        chartAltitude.setViewPortOffsets(mChartMarginLeft, mChartMarginTop, mChartMarginRight, mChartMarginBottom);

        XAxis xAxis = chartAltitude.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new MintTimeFormatter());
        xAxis.setLabelCount(5);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chartAltitude.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(5);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawZeroLine(true);

        chartAltitude.getAxisRight().setDrawZeroLine(false);
        chartAltitude.getAxisRight().setDrawGridLines(false);
        chartAltitude.getAxisRight().setDrawAxisLine(false);
    }

    /**
     * 初始化bar chart
     */
    private void initPowerChart() {
        chartPower.setDrawBarShadow(false);
        chartPower.getLegend().setEnabled(false);//设置图例是否显示
        // no description text
        chartPower.getDescription().setEnabled(false);
        // enable touch gestures
        chartPower.setTouchEnabled(true);

        // enable scaling and dragging
        chartPower.setPinchZoom(false);
        chartPower.setFitBars(true);
        chartPower.setDragEnabled(true);
        chartPower.setScaleEnabled(false);
        chartPower.setDrawGridBackground(false);
        chartPower.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chartPower.setBackgroundColor(Color.TRANSPARENT);
        chartPower.setViewPortOffsets(mChartMarginLeft, mChartMarginTop, mChartMarginRight, mChartMarginBottom);

        XAxis xAxis = chartPower.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setValueFormatter(new TimerFormatter(1));
        xAxis.setLabelCount(5,false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chartPower.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(5);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawZeroLine(true);

        chartPower.getAxisRight().setDrawZeroLine(false);
        chartPower.getAxisRight().setDrawGridLines(false);
        chartPower.getAxisRight().setDrawAxisLine(false);
    }


    /**
     * 设置数据
     */
    private void setChartData() {
        ArrayList<Entry> hrValues = new ArrayList<Entry>();
        ArrayList<Entry> stepValues = new ArrayList<Entry>();
        ArrayList<Entry> locValues = new ArrayList<Entry>();

        XAxis xAxis = chartHr.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum((float) (mWorkoutDe.duration));
        if (mWorkoutDe.duration < 300){
            xAxis.setLabelCount(2);
        }

        xAxis = chartCadence.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum((float) (mWorkoutDe.duration));
        if (mWorkoutDe.duration < 300){
            xAxis.setLabelCount(2);
        }

        xAxis = chartAltitude.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum((float) (mWorkoutDe.duration));
        if (mWorkoutDe.duration < 300){
            xAxis.setLabelCount(2);
        }

        xAxis = chartPower.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum((float) (mWorkoutDe.duration/60));
        if (mWorkoutDe.duration < 300) {
            xAxis.setLabelCount(2);
        }

        int interval = mHrDeList.size() / 100 + 1;
        int index = 0;
        int total = 0;
        if (mHrDeList != null && mHrDeList.size() > 0) {
            mCadenceMax = mHrDeList.get(0).strideFreQuency;

            for (HrDE hr : mHrDeList) {
                if (index % interval == 0) {
                    hrValues.add(new Entry(hr.timeOffSet, hr.heartbeat));
                    stepValues.add(new Entry(hr.timeOffSet, hr.strideFreQuency));
                }
                if (hr.strideFreQuency > mCadenceMax) {
                    mCadenceMax = hr.strideFreQuency;
                }
                index++;
                total += hr.heartbeat;
            }
            mHrAvg = total/mHrDeList.size();
        }

        interval = mLocList.size() / 100 + 1;
        index = 0;
        String lapStartTime = "";
        mAltitudeUp = 0;
        if (mLocList.size() > 0) {
            lapStartTime = mLocList.get(0).lapStartTime;

            long offsetTime = 0;
            double lastAltitude = mLocList.get(0).height;
            for (LocationDE loc : mLocList) {
                if (index % interval == 0) {
                    if (!lapStartTime.equals(loc.lapStartTime)) {
                        LapDE lastLap = LapDBData.getLapFromWorkout(mWorkoutStartTime, lapStartTime, mUserDe.userId);
                        offsetTime += lastLap.duration;
                        lapStartTime = loc.lapStartTime;
                    }
                    locValues.add(new Entry(loc.timeOffSet + offsetTime, (float) loc.height));
                }
                index++;
                if (loc.height > lastAltitude) {
                    mAltitudeUp += (loc.height - lastAltitude);
                }
                lastAltitude = loc.height;
                if (mWorkoutDe.maxHeight < loc.height) {
                    mWorkoutDe.maxHeight = loc.height;
                }
            }
        }
        YAxis leftAxis = chartAltitude.getAxisLeft();
        leftAxis.setAxisMaximum((float) ((float) mWorkoutDe.maxHeight * 1.1));

        LineDataSet hrSet = new LineDataSet(hrValues, "DataSet hr");
        hrSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        hrSet.setColor(Color.parseColor("#ffb300"));
        hrSet.setLineWidth(1.5f);
        hrSet.setDrawCircles(false);
        hrSet.setDrawValues(false);
        hrSet.setHighLightColor(Color.TRANSPARENT);
        hrSet.setDrawCircleHole(false);
        hrSet.setDrawFilled(true);

        LineDataSet stepSet = new LineDataSet(stepValues, "DataSet step");
        stepSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        stepSet.setColor(Color.parseColor("#18cc14"));
        stepSet.setLineWidth(1.5f);
        stepSet.setDrawCircles(false);
        stepSet.setDrawValues(false);
        stepSet.setHighLightColor(Color.TRANSPARENT);
        stepSet.setDrawCircleHole(false);
        stepSet.setDrawFilled(true);

        LineDataSet LocSet = new LineDataSet(locValues, "LocSet step");
        LocSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        LocSet.setColor(Color.parseColor("#2196f3"));
        LocSet.setLineWidth(1.5f);
        LocSet.setDrawCircles(false);
        LocSet.setDrawValues(false);
        LocSet.setHighLightColor(Color.TRANSPARENT);
        LocSet.setDrawCircleHole(false);
        LocSet.setDrawFilled(true);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable hrDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.chart_fade_contrast);
            hrSet.setFillDrawable(hrDrawable);
            Drawable stepDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.chart_fade_accent);
            stepSet.setFillDrawable(stepDrawable);
            Drawable locDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.chart_fade_loc);
            LocSet.setFillDrawable(locDrawable);
        } else {
            hrSet.setFillColor(Color.parseColor("#ffb300"));
            stepSet.setFillColor(Color.parseColor("#18cc14"));
            LocSet.setFillColor(Color.parseColor("#2196f3"));
        }

        // create a data object with the datasets
//        LineData data = new LineData(hrSet, stepSet);
        LineData data = new LineData(hrSet);
        LineData stepData = new LineData(stepSet);
        LineData locData = new LineData(LocSet);

        chartHr.setData(data);
        chartHr.getData().setHighlightEnabled(true);

        chartCadence.setData(stepData);
        chartCadence.getData().setHighlightEnabled(true);

        chartAltitude.setData(locData);
        chartAltitude.getData().setHighlightEnabled(true);
        // get the legend (only possible after setting data)
        Legend l = chartHr.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        l = chartCadence.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        l = chartAltitude.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        setPowerChartData();

        chartHr.invalidate();
        chartHr.animateX(1000);

        chartCadence.invalidate();
        chartCadence.animateX(1000);

        chartAltitude.invalidate();
        chartAltitude.animateX(1000);

        chartPower.invalidate();
        chartPower.animateX(1000);
    }

    /**
     * 心率文本
     */
    private void setHrView() {
        tvHrAvg.setText("平均心率 " + mHrAvg);
        tvHrMax.setText("最高心率 " + mWorkoutDe.maxHr);
    }

    /**
     * 心率运动强度信息
     */
    private void setPowerView(){
        tvPowerAvg.setText("平均强度 " + mHrAvg * 100 / mUserDe.maxHr);
        tvPowerMax.setText("最大强度 " + mWorkoutDe.maxHr * 100 / mUserDe.maxHr);
    }

    private void setCadenceView() {
        tvCadenceAvg.setText("平均步频 " + mWorkoutDe.endStep * 60 / mWorkoutDe.duration);
        tvCadenceMax.setText("最高步频 " + mCadenceMax);
    }


    private void setAltitudeView() {
        tvAltitude.setText("累计爬升 " + (int) (mAltitudeUp * 100) / 100.0f);
    }

    private void setPowerChartData() {
//        Log.v(TAG,"mTimeSplits.size:" + mTimeSplits.size());
        List<Integer> colors = new ArrayList<>();
        for (int i = 0, size = mTimeSplits.size(); i < size; i++) {
            mBarData.add(new BarEntry(i, mTimeSplits.get(i).avgHr * 100 / mUserDe.maxHr));
            colors.add(ColorU.getColorByHeartbeat(mTimeSplits.get(i).avgHr * 100 / mUserDe.maxHr));
        }
        if (chartPower.getData() != null &&
                chartPower.getData().getDataSetCount() > 0) {
            mBarSet = (BarDataSet) chartPower.getData().getDataSetByIndex(0);
            mBarSet.setValues(mBarData);
            mBarSet.setColors(colors);
            chartPower.getData().notifyDataChanged();
            chartPower.notifyDataSetChanged();
        } else {
            mBarSet = new BarDataSet(mBarData, "free sport");
            mBarSet.setColors(colors);

            mBarSet.setDrawValues(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(mBarSet);


            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setBarWidth(0.9f);

            chartPower.setData(data);
        }
    }
}
