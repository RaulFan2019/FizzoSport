package cn.hwh.sports.activity.run;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.workout.WorkoutIndoorActivity;
import cn.hwh.sports.activity.workout.WorkoutListActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.EffortPointEE;
import cn.hwh.sports.entity.event.MaxHrEE;
import cn.hwh.sports.entity.event.RunningTimeEE;
import cn.hwh.sports.service.FreeEffortService;
import cn.hwh.sports.ui.chart.HeartbeatFormatter;
import cn.hwh.sports.ui.chart.HeartbeatPowerFormatter;
import cn.hwh.sports.ui.chart.TimerFormatter;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/11/27.
 */

public class FreeEffortActivity extends BaseActivity implements OnChartValueSelectedListener {

    /* contains */
    private static final String TAG = "FreeEffortActivity";

    /* view */
    @BindView(R.id.tv_effort_power)
    TextView mEffortPowerTv;
    @BindView(R.id.tv_curr_hr)
    TextView mCurrHrTv;
    @BindView(R.id.tv_max_hr)
    TextView mMaxHrTv;
    @BindView(R.id.v_ble_status)
    View mBleStatusV;
    @BindView(R.id.tv_ble_status)
    TextView mBleStatusTv;
    @BindView(R.id.v_play_voice)
    View mPlayVoiceV;
    @BindView(R.id.tv_play_voice)
    TextView mPlayVoiceTv;
    @BindView(R.id.chart)
    BarChart mChart;
    @BindView(R.id.ll_root)
    LinearLayout mRootLl;
    @BindView(R.id.tv_time)
    TextView mDurationTv;//时间文本
    @BindView(R.id.btn_finish)
    Button mFinishBtn;

    /* local data */
    private UserDE mUserDE;
    private WorkoutDE mWorkoutDE;
    private List<TimeSplitDE> mTimeSplits = new ArrayList<>();

    ArrayList<BarEntry> mBarData = new ArrayList<BarEntry>();
    List<Integer> colors = new ArrayList<>();
    BarDataSet mBarSet;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_free_effort;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
//        Log.v(TAG,"onBleEventBus msg :" + event.msg);
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT
                && event.hr != 0) {
            int percent = event.hr * 100 / mUserDE.maxHr;
            mCurrHrTv.setText(event.hr + "");
            mEffortPowerTv.setText(percent + "");
            mRootLl.setBackgroundResource(ColorU.getBgByHeartbeat(percent));
        } else if (event.msg == BleManager.MSG_DISCONNECT
                || event.msg == BleManager.MSG_CONNECT_FAIL) {
            mBleStatusTv.setText("设备已断开");
            mBleStatusV.setBackgroundResource(R.drawable.ic_ble_disconnect);
            mCurrHrTv.setText("- -");
            mEffortPowerTv.setText("- -");
        } else if (event.msg == BleManager.MSG_CONNECTED) {
            mBleStatusTv.setText("设备已连接");
            mBleStatusV.setBackgroundResource(R.drawable.ic_ble_connected);
        }
    }

    /**
     * 时间发生改变
     *
     * @param eventEffortTime
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEventBus(RunningTimeEE eventEffortTime) {
        mDurationTv.setText("时长:" + eventEffortTime.time / 60 + "分" + eventEffortTime.time % 60 + "秒");
//        mDurationTv.setText(TimeUtils.formatSecondsToShortHourTime(eventEffortTime.time));
    }

    /**
     * 获取最大心率
     *
     * @param eventMaxHr
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMaxHr(MaxHrEE eventMaxHr) {
        mMaxHrTv.setText(eventMaxHr.maxHr + "");
    }


    /**
     * 过了整分钟
     *
     * @param effortPointEE
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEffortPointChange(EffortPointEE effortPointEE) {
        mTimeSplits = TimeSplitDBData.getSplitsByWorkout(mWorkoutDE);
        setData();
        mChart.invalidate();
    }


    @OnClick({R.id.v_play_voice, R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击声音按钮
            case R.id.v_play_voice:
                break;
            //点击结束按钮
            case R.id.btn_finish:
                finishWorkout();
                break;
        }
    }

    @Override
    protected void initData() {
        mUserDE = LocalApplication.getInstance().getLoginUser(FreeEffortActivity.this);

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initVoiceView();
        initChart();
    }

    @Override
    protected void doMyCreate() {
        Intent intent = new Intent(getApplicationContext(), FreeEffortService.class);
        startService(intent);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        mTimeSplits = TimeSplitDBData.getSplitsByWorkout(mWorkoutDE);
        mMaxHrTv.setText(mWorkoutDE.maxHr + "");
        if (mWorkoutDE.userId != mWorkoutDE.ownerId) {
            mFinishBtn.setText("结束私教");
        }
        if (BleManager.getBleManager().mBleConnectE != null) {
            if (BleManager.getBleManager().mBleConnectE.mIsConnected) {
                mBleStatusTv.setText("设备已连接");
                mBleStatusV.setBackgroundResource(R.drawable.ic_ble_connected);
            } else {
                mBleStatusTv.setText("设备已断开");
                mBleStatusV.setBackgroundResource(R.drawable.ic_ble_disconnect);
                mCurrHrTv.setText("- -");
                mEffortPowerTv.setText("- -");
            }
        }
        setData();
        mChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 结束记录
     */
    private void finishWorkout() {
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);

        Intent intent = new Intent(this, FreeEffortService.class);
        intent.putExtra("CMD", FreeEffortService.CMD_FINISH);
        startService(intent);
        if (mWorkoutDE.duration > 60) {
            //若是自己的锻炼记录，跳转到记录列表
            if (mWorkoutDE.ownerId == mUserDE.userId){
                startActivity(WorkoutListActivity.class);
            }
            Bundle bundle = new Bundle();
            bundle.putInt("workoutId", 0);
            bundle.putString("workoutStartTime", mWorkoutDE.startTime);
            bundle.putInt("resource", SportEnum.resource.APP);
            bundle.putInt("type", mWorkoutDE.type);
            startActivity(WorkoutIndoorActivity.class, bundle);
            finish();
        } else {
            T.showShort(FreeEffortActivity.this, "运动不足1分钟，不作记录");
            finish();
        }
        if (mWorkoutDE.userId != mWorkoutDE.ownerId) {
            BleManager.getBleManager().mBleConnectE.disConnect();
            String mac = LocalApplication.getInstance().getLoginUser(FreeEffortActivity.this).bleMac;
            if (!mac.equals("")) {
                BleManager.getBleManager().addNewConnect(mac);
            }
        } else {
            if (BleManager.getBleManager().mBleConnectE != null &&
                    BleManager.getBleManager().mBleConnectE.mIsConnected){
                BleManager.getBleManager().mBleConnectE.reStartSync();
            }
        }
    }

    private void initVoiceView() {
        if (SportSpData.getTtsEnable(FreeEffortActivity.this)) {
            mPlayVoiceV.setBackgroundResource(R.drawable.ic_play_voice_open);
            mPlayVoiceTv.setText(R.string.play_open);
        } else {
            mPlayVoiceV.setBackgroundResource(R.drawable.ic_play_voice_close);
            mPlayVoiceTv.setText(R.string.play_close);
        }
    }

    private void initChart() {
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setScaleEnabled(false);//设置是否缩放
        mChart.getLegend().setEnabled(false);//设置图例是否显示

        mChart.getDescription().setEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setFitBars(true);
        mChart.getAxisLeft().setDrawAxisLine(false);//显示数值轴
        mChart.getAxisLeft().setDrawGridLines(false);//显示边框
        mChart.getAxisLeft().setDrawZeroLine(false);//显示
        mChart.getAxisRight().setDrawZeroLine(true);
        mChart.getAxisRight().setDrawGridLines(true);
        mChart.getAxisRight().setDrawAxisLine(false);

        TimerFormatter timerFormatter = new TimerFormatter(0);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(timerFormatter);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(5);
        xAxis.setTextSize(9.5f);
        xAxis.setTextColor(Color.parseColor("#333333"));
//        xAxis.setAxisMinimum(-0.5f);

        HeartbeatPowerFormatter custom = new HeartbeatPowerFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(Color.parseColor("#333333"));
        leftAxis.setTextSize(9.5f);


        HeartbeatFormatter nullCustom = new HeartbeatFormatter(mUserDE.maxHr);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setValueFormatter(nullCustom);
//        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(100f);
        rightAxis.setTextSize(9.5f);
        rightAxis.setZeroLineColor(Color.parseColor("#333333"));
        rightAxis.setZeroLineWidth(0.5f);
    }


    private void setData() {
        List<Integer> colors = new ArrayList<>();

//        for (int i = 0; i < mTimeSplits.size(); i++) {
//            int avgHr = mTimeSplits.get(i).avgHr;
//            mBarData.add(new BarEntry(i, avgHr * 100 / mUserDE.maxHr));
//            colors.add(ColorU.getColorByHeartbeat(avgHr * 100 / mUserDE.maxHr));
//        }
//        if (mTimeSplits.size() < 30){
//            for (int i = mTimeSplits.size() -1 ; i < 31 ; i++){
//                mBarData.add(new BarEntry(i, 0 * 100 / mUserDE.maxHr));
//                colors.add(ColorU.getColorByHeartbeat(0 * 100 / mUserDE.maxHr));
//            }
//        }
        int startIndex = 0;
        if (mTimeSplits.size() > 30) {
            startIndex = mTimeSplits.size() - 30;
        }
        for (int index = startIndex, i = 0; i < 31; i++, index++) {
            int avgHr = 0;
            if (mTimeSplits.size() > index) {
                avgHr = mTimeSplits.get(index).avgHr;
            }
//            Log.v(TAG,"avgHr:" + avgHr);
            mBarData.add(new BarEntry(i, avgHr * 100 / mUserDE.maxHr));
            colors.add(ColorU.getColorByHeartbeat(avgHr * 100 / mUserDE.maxHr));
        }

//        if (mTimeSplits.size() > 30 ){
//            mChart.getXAxis().setAxisMinimum((float) (mTimeSplits.size() - 31.5));
//        }else {
//            mChart.getXAxis().setAxisMinimum((float) (- 0.5));
//        }

        TimerFormatter timerFormatter = new TimerFormatter((int) (startIndex - 0.5));
        mChart.getXAxis().setValueFormatter(timerFormatter);

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            mBarSet = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            mBarSet.setValues(mBarData);
            mBarSet.setColors(colors);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            mBarSet = new BarDataSet(mBarData, "free sport");
            mBarSet.setColors(colors);

            mBarSet.setDrawValues(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(mBarSet);


            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }

    /**
     * 初始化Chart
     */
//    private void initChart() {
//        mChart.setDrawBarShadow(false);
//        mChart.setDrawValueAboveBar(true);
//        mChart.setScaleEnabled(false);//设置是否缩放
//        mChart.getLegend().setEnabled(false);//设置图例是否显示
//
//        mChart.setDescription("");
//        mChart.setOnChartValueSelectedListener(this);
//
//        // scaling can now only be done on x- and y-axis separately
//        mChart.setPinchZoom(false);
//        mChart.setFitBars(true);
//        mChart.getAxisLeft().setDrawAxisLine(false);//显示数值轴
//        mChart.getAxisLeft().setDrawGridLines(false);//显示边框
//        mChart.getAxisLeft().setDrawZeroLine(false);//显示
//        mChart.getAxisRight().setDrawZeroLine(true);
//        mChart.getAxisRight().setDrawGridLines(true);
//        mChart.getAxisRight().setDrawAxisLine(false);
//
//        colors.clear();
//        int startIndex = 0;
//        if (mTimeSplits.size() > 30) {
//            startIndex = mTimeSplits.size() - 30;
//        }
//
//        for (int index = startIndex, i = 0; i < 31; i++, index++) {
//            int avgHr = 0;
//            if (mTimeSplits.size() > index) {
//                avgHr = mTimeSplits.get(index).avgHr;
//            }
////            Log.v(TAG,"avgHr:" + avgHr);
//            mBarData.add(new BarEntry(i, avgHr * 100 / mUserDE.maxHr));
//            colors.add(ColorU.getColorByHeartbeat(avgHr * 100 / mUserDE.maxHr));
//        }
//
////        for (int i = 0; i < mTimeSplits.size(); i++) {
////            int avgHr = mTimeSplits.get(i).avgHr;
////            mBarData.add(new BarEntry(i, avgHr * 100 / mUserDE.maxHr));
////            colors.add(ColorU.getColorByHeartbeat(avgHr * 100 / mUserDE.maxHr));
////        }
//
//        TimerFormatter timerFormatter = new TimerFormatter((int) (mTimeSplits.size() - 31.5));
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(timerFormatter);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(3);
//        xAxis.setTextSize(9.5f);
//        xAxis.setTextColor(Color.parseColor("#333333"));
//        xAxis.setAxisMinValue(0f);
//
//        AxisValueFormatter custom = new HeartbeatPowerFormatter();
//
//        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setLabelCount(5, false);
//        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
//        leftAxis.setAxisMaxValue(120f);
//        leftAxis.setTextColor(Color.parseColor("#333333"));
//        leftAxis.setTextSize(9.5f);
//
//
//        AxisValueFormatter nullCustom = new HeartbeatFormatter(mUserDE.maxHr);
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setLabelCount(5, false);
//        rightAxis.setValueFormatter(nullCustom);
////        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinValue(0f);
//        rightAxis.setAxisMaxValue(120f);
//        rightAxis.setTextSize(9.5f);
//        rightAxis.setZeroLineColor(Color.parseColor("#333333"));
//        rightAxis.setZeroLineWidth(1.5f);
//
////        mChart.setMarkerView(new XYMarkerView(this));
//
//        mBarSet = new BarDataSet(mBarData, "free sport");
//        mBarSet.setDrawValues(false);
//        mBarSet.setColors(colors);
//        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//        dataSets.add(mBarSet);
//
//        BarData data = new BarData(dataSets);
//        data.setValueTextSize(10f);
//        data.setBarWidth(0.9f);
//        mChart.setData(data);
//    }

}
