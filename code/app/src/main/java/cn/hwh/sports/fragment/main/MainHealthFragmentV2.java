package cn.hwh.sports.fragment.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.main.FizzoHelpActivity;
import cn.hwh.sports.activity.main.MainActivityV2;
import cn.hwh.sports.activity.settings.BleAutoBindActivity;
import cn.hwh.sports.activity.workout.WorkoutIndoorActivity;
import cn.hwh.sports.activity.workout.WorkoutRunningOutdoorActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.ble.BleUtils;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.DayHealthDBData;
import cn.hwh.sports.data.db.LastSportDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.entity.db.DayHealthDE;
import cn.hwh.sports.entity.db.LastSportDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.event.SyncWatchWorkoutEE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetHealthSummaryRE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.NetworkU;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2017/4/17.
 */

public class MainHealthFragmentV2 extends BaseFragment {


    /* contains */
    private static final String TAG = "MainHealthFragmentV2";

    private static final int MSG_GET_HEALTH_OK = 0x01;
    private static final int MSG_READ_STEP_COUNT = 0x02;

    /* local view */
    @BindView(R.id.chart_sport_time)
    PieChart mChart;//锻炼时间时间轴
    @BindView(R.id.tv_sport_time)
    TextView mSportTimeTv;//锻炼时长文本
    @BindView(R.id.tv_calorie)
    TextView mCalorieTv;//卡路里文本
    @BindView(R.id.tv_step)
    TextView mStepTv;//步数文本

    @BindView(R.id.ll_sync_time)
    LinearLayout mSyncTimeLl;//同步时间布局
    @BindView(R.id.tv_sync_update_time)
    TextView mSyncUpdateTimeTv;//上次同步时间

    @BindView(R.id.btn_bind_watch)
    TextView mBindWatchBtn;

    @BindView(R.id.v_last_sport_type)
    View mLastSportTypeV;
    @BindView(R.id.tv_last_sport_type)
    TextView mLastSportTypeTv;//上次运动的类型
    @BindView(R.id.tv_last_sport_time)
    TextView mLastSportTimeTv;//上次运动的时间
    @BindView(R.id.tv_last_sport_duration)
    TextView mLastSportDurationTv;//上次运动的时长
    @BindView(R.id.ll_last_sport)
    LinearLayout mLastSportLl;//上次运动的布局
    @BindView(R.id.tv_body_title)
    TextView mBodyTitleTv;//身体状态布局
    @BindView(R.id.tv_sleep_info)
    TextView mSleepInfoTv;//睡眠情况文本
    @BindView(R.id.ll_body)
    LinearLayout mBodyLl;//身体状态布局
    @BindView(R.id.tv_tv_help)
    TextView mTvHelpTv;//帮助数量文本

    @BindView(R.id.ll_watch)
    LinearLayout mWatchLl;//手表状态布局
    @BindView(R.id.v_watch_battery)
    View mWatchBatteryV;//手表电量图示
    @BindView(R.id.v_watch_sync)
    View mWatchSyncV;//手表同步图示
    @BindView(R.id.tv_watch_state)
    TextView mWatchStateTv;//手表状态文本


    @BindView(R.id.tv_error)
    TextView mErrorTipTv;
    /* local data */
    private Typeface mTypeFace;

    private DayHealthDE mTodayHealthDe;
    private LastSportDE mLastSportDe;
    private UserDE mUserDe;
    private int mCurrStep;

    private RotateAnimation mSyncRotateAnim;//同步的旋转动画
    private DialogBuilder mDialogBuilder;

    private ConnectionChangeReceiver myReceiver;

    /* 构造函数 */
    public static MainHealthFragmentV2 newInstance() {
        MainHealthFragmentV2 fragment = new MainHealthFragmentV2();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_health_v2;
    }


    @OnClick({R.id.ll_last_sport, R.id.ll_body, R.id.ll_help, R.id.btn_bind_watch, R.id.tv_sport_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_last_sport:
                if (mLastSportDe != null && TimeU.isToday(mLastSportDe.lastSportStartTime, TimeU.FORMAT_TYPE_1)) {
                    if (mLastSportDe.lastSportType == SportEnum.EffortType.RUNNING_OUTDOOR) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("workoutId", mLastSportDe.lastSportId);
                        bundle.putString("workoutStartTime", mLastSportDe.lastSportStartTime);
                        startActivity(WorkoutRunningOutdoorActivity.class, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("workoutId", mLastSportDe.lastSportId);
                        bundle.putString("workoutStartTime", mLastSportDe.lastSportStartTime);
                        bundle.putInt("resource", SportEnum.resource.APP);
                        bundle.putInt("type", mLastSportDe.lastSportType);
                        startActivity(WorkoutIndoorActivity.class, bundle);
                    }
                } else {
                    MainActivityV2 mainActivityV2 = (MainActivityV2) getActivity();
                    mainActivityV2.setTabSelection(MainActivityV2.TAB_SPORT);
                }
                break;
            case R.id.ll_body:
                break;
            case R.id.ll_help:
                startActivity(FizzoHelpActivity.class);
                break;
            case R.id.btn_bind_watch:
                startActivity(BleAutoBindActivity.class);
                break;
            //点击有效时长
            case R.id.tv_sport_time:
                mDialogBuilder.showMsgDialog(getActivity(), "什么是有效时长", getString(R.string.tip_effective_time));
                break;
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_HEALTH_OK:
                    updateHealthView();
                    break;
                //读取步数
                case MSG_READ_STEP_COUNT:
                    if (BleManager.getBleManager().mBleConnectE != null
                            && BleManager.getBleManager().mBleConnectE.mIsConnected
                            && !BleManager.getBleManager().mBleConnectE.mSyncNow) {
                        BleManager.getBleManager().mBleConnectE.readStepCount();
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_READ_STEP_COUNT, 2000);
                    break;
            }
        }
    };


    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT) {

        } else if (event.msg == BleManager.MSG_CURR_STEP_COUNT) {
            mCurrStep = (int) event.currStepCount;
            mStepTv.setText(mCurrStep + "");
        } else {
            updateWatchView();
        }
    }

    /**
     * 手表同步手机完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(SyncWatchWorkoutEE event) {
        updateWatchView();
    }


    /**
     * 有一条历史记录同步完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWorkoutEnd(WorkoutEndEE event) {
        postGetDayHealth();
    }

    @Override
    protected void initParams() {
        mTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mSyncRotateAnim = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.rotating);
        mDialogBuilder = new DialogBuilder();
        initChart();
        mSportTimeTv.setTypeface(mTypeFace);
        mCalorieTv.setTypeface(mTypeFace);
        mStepTv.setTypeface(mTypeFace);
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        mUserDe = LocalApplication.getInstance().getLoginUser(getActivity());
        mTodayHealthDe = DayHealthDBData.getDayHealth(TimeU.nowDay(), mUserDe.userId);
        mLastSportDe = LastSportDBData.getLastSportInfo(mUserDe.userId);
        registerReceiver();
        updateHealthView();
        updateWatchView();
        EventBus.getDefault().register(this);
        postGetDayHealth();
        mHandler.sendEmptyMessage(MSG_READ_STEP_COUNT);
    }

    @Override
    protected void onInVisible() {
        uploadStep();
        EventBus.getDefault().unregister(this);
        SportSpData.setStepCount(getActivity(), mCurrStep);
        SportSpData.setStepRecordDate(getActivity(), TimeU.nowDay());
        unregisterReceiver();
        mSyncRotateAnim.cancel();
        mHandler.removeMessages(MSG_READ_STEP_COUNT);
    }

    /**
     * 初始化锻炼时间轴
     */
    private void initChart() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.TRANSPARENT);

        mChart.setTransparentCircleColor(Color.TRANSPARENT);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(86f);
        mChart.setTransparentCircleRadius(86f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);
        mChart.setDrawEntryLabels(false);

        mChart.getLegend().setEnabled(false);//设置图例是否显示
        mChart.getDescription().setEnabled(false);
        mChart.setRotationEnabled(false);

//        Legend l = mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);

        // entry label styling
    }


    /**
     * 更新时间轴
     */
    private void setSportTime(final int allTime, final int effectiveTime) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        entries.add(new PieEntry((float) effectiveTime, ""));
        colors.add(Color.parseColor("#ff4612"));
        entries.add(new PieEntry((float) (allTime - effectiveTime), ""));
        colors.add(Color.parseColor("#ad2c08"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(null);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        for (IDataSet<?> set : mChart.getData().getDataSets()) {
            set.setDrawValues(false);
        }
        mChart.invalidate();
    }


    /**
     * 更新健康的页面
     */
    private void updateHealthView() {
//        Log.v(TAG,"updateHealthView");
        //健康数据
        if (mTodayHealthDe != null) {
            setSportTime(mTodayHealthDe.exercisedMinutes, mTodayHealthDe.effectiveMinutes);
            mSportTimeTv.setText(mTodayHealthDe.effectiveMinutes + "");
            mCalorieTv.setText(mTodayHealthDe.exercisedCalorie + "");
            if (mTodayHealthDe.updateTime.equals("")) {
                mSyncUpdateTimeTv.setText("今日尚未同步");
            } else {
                mSyncUpdateTimeTv.setText(mTodayHealthDe.updateTime);
            }
            mChart.animate();
        } else {
            setSportTime(1, 0);
            mSportTimeTv.setText(0 + "");
            mCalorieTv.setText(0 + "");
            mSyncUpdateTimeTv.setText("今日手环数据未上传");
        }
        if (SportSpData.getStepRecordDate(getActivity()).equals(TimeU.nowDay())) {
            mCurrStep = SportSpData.getStepCount(getActivity());
        } else {
            mCurrStep = 0;
        }
        mStepTv.setText(mCurrStep + "");
        //上次锻炼信息
        if (mLastSportDe != null
                && TimeU.isToday(mLastSportDe.lastSportStartTime, TimeU.FORMAT_TYPE_1)) {
            mLastSportTimeTv.setVisibility(View.VISIBLE);
            mLastSportDurationTv.setVisibility(View.VISIBLE);
//            Log.v(TAG,"mLastSportDe.lastSportDuration:" + mLastSportDe.lastSportDuration);
//            Log.v(TAG,"TimeU.formatListShowTime(mLastSportDe.lastSportStartTime, 0):" + TimeU.formatListShowTime(mLastSportDe.lastSportStartTime, 0));
//            Log.v(TAG,"mLastSportDe.lastSportType:" + mLastSportDe.lastSportType);

            mLastSportTimeTv.setText(TimeU.formatListShowTime(mLastSportDe.lastSportStartTime, mLastSportDe.lastSportDuration * 1000));
            mLastSportTypeTv.setText(mLastSportDe.lastSportName);
            mLastSportDurationTv.setText(mLastSportDe.lastSportDuration / 60 + "分钟");
            if (mLastSportDe.lastSportType == SportEnum.EffortType.RUNNING_OUTDOOR) {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_1);
            } else if (mLastSportDe.lastSportType == SportEnum.EffortType.RUNNING_INDOOR) {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_4);
            } else if (mLastSportDe.lastSportType == SportEnum.EffortType.LOU_TI) {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_5);
            } else if (mLastSportDe.lastSportType == SportEnum.EffortType.DAN_CHE) {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_6);
            } else if (mLastSportDe.lastSportType == SportEnum.EffortType.TUO_YUAN) {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_7);
            } else if (mLastSportDe.lastSportType == SportEnum.EffortType.HUA_CHUAN) {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_8);
            } else if (mLastSportDe.lastSportType == SportEnum.EffortType.TIAO_SHENG){
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_10);
            } else {
                mLastSportTypeV.setBackgroundResource(R.drawable.ic_list_sport_type_3);
            }

        } else {
            mLastSportTypeTv.setText("您今天还没有锻炼过");
            mLastSportTimeTv.setText("点击前往锻炼");
            mLastSportDurationTv.setVisibility(View.GONE);
        }
    }


    /**
     * 更新手表状态页面
     */
    private void updateWatchView() {
        //若手表没有绑定
        if (LocalApplication.getInstance().getLoginUser(getActivity()).bleMac == null
                || LocalApplication.getInstance().getLoginUser(getActivity()).bleMac.equals("")) {
            mWatchLl.setVisibility(View.GONE);
//            mSyncTimeLl.setVisibility(View.GONE);
            mBindWatchBtn.setVisibility(View.VISIBLE);
            return;
        }
//        mSyncTimeLl.setVisibility(View.VISIBLE);
        mWatchLl.setVisibility(View.VISIBLE);
        mSyncRotateAnim.cancel();
        mWatchSyncV.clearAnimation();
        mWatchSyncV.setVisibility(View.GONE);
        mWatchBatteryV.setVisibility(View.GONE);
        mBindWatchBtn.setVisibility(View.GONE);
        //若手表未连接
        if (BleManager.getBleManager().mBleConnectE == null
                || !BleManager.getBleManager().mBleConnectE.mIsConnected) {
            mWatchStateTv.setText("未连接");
            return;
        }
        //若手表正在同步
        if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
            mWatchStateTv.setText("同步中");
            mWatchSyncV.setVisibility(View.VISIBLE);
            mSyncRotateAnim.start();
            mWatchSyncV.setAnimation(mSyncRotateAnim);
            return;
        }
        mWatchBatteryV.setVisibility(View.VISIBLE);
        mWatchStateTv.setText("");
        //显示电量
        if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 20) {
            mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_20);
            return;
        }
        if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 40) {
            mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_40);
            return;
        }
        if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 60) {
            mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_60);
            return;
        }
        if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 80) {
            mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_80);
            return;
        }
        mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_100);
    }

    /**
     * 获取健康数据
     */
    private void postGetDayHealth() {

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetDayHealthSummaryRP(getActivity(),
                        UrlConfig.URL_GET_HEALTH_SUMMARY, mUserDe.userId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetHealthSummaryRE re = JSON.parseObject(result.result, GetHealthSummaryRE.class);
                            //个人今天健康信息
                            if (mTodayHealthDe != null) {
                                mTodayHealthDe.date = re.date;
                                mTodayHealthDe.exercisedMinutes = re.exercised_minutes;
                                mTodayHealthDe.effectiveMinutes = re.effective_minutes;
                                mTodayHealthDe.exercisedCalorie = re.exercised_calorie;
                                mTodayHealthDe.stepCount = re.stepcount;
                                mTodayHealthDe.updateTime = re.updatetime;
                                DayHealthDBData.update(mTodayHealthDe);
                            } else {
                                mTodayHealthDe = new DayHealthDE(System.currentTimeMillis(),
                                        mUserDe.userId, re.date, re.stepcount, re.exercised_calorie,
                                        re.updatetime, re.exercised_minutes, re.effective_minutes);
                                DayHealthDBData.save(mTodayHealthDe);
                            }
                            //最后一次锻炼记录
                            if (re.last_exercise.id == 0) {
                                mLastSportDe = null;
                                return;
                            } else {
                                if (mLastSportDe != null) {
                                    Log.v(TAG, "update LastSport");
                                    mLastSportDe.lastSportId = re.last_exercise.id;
                                    mLastSportDe.lastSportDuration = re.last_exercise.duration;
                                    mLastSportDe.lastSportName = re.last_exercise.name;
                                    mLastSportDe.lastSportType = re.last_exercise.type;
                                    mLastSportDe.lastSportStartTime = re.last_exercise.starttime;
                                    LastSportDBData.update(mLastSportDe);
                                } else {
                                    mLastSportDe = new LastSportDE(System.currentTimeMillis(), mUserDe.userId,
                                            re.last_exercise.id, re.last_exercise.type, re.last_exercise.name,
                                            re.last_exercise.starttime, re.last_exercise.duration);
                                    LastSportDBData.save(mLastSportDe);
                                }
                            }
                            mHandler.sendEmptyMessage(MSG_GET_HEALTH_OK);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        myReceiver = new MainHealthFragmentV2.ConnectionChangeReceiver();
        getActivity().registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        getActivity().unregisterReceiver(myReceiver);
    }

    /**
     * @author Javen
     */
    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            boolean bluetoothState = (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
            boolean networkState = NetworkU.isNetworkConnected(context);
            if (bluetoothState && networkState) {
                mErrorTipTv.setVisibility(View.GONE);
                return;
            }
            if (!networkState) {
                mErrorTipTv.setVisibility(View.VISIBLE);
                mErrorTipTv.setText("无法连接网络");
                return;
            }

            if (!bluetoothState) {
                mErrorTipTv.setVisibility(View.VISIBLE);
                mErrorTipTv.setText("蓝牙已关闭");
                updateWatchView();
                return;
            }
        }
    }

    private void uploadStep() {
        final JSONArray updateJson = new JSONArray();
        String startTime = TimeU.nowTime(TimeU.FORMAT_TYPE_3);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", startTime);
        jsonObject.put("stepcount", mCurrStep);
        updateJson.add(jsonObject);

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUpdateStepHistory(getActivity(),
                        UrlConfig.URL_UPLOAD_DAY_STEP_COUNTS, updateJson.toJSONString(),
                        LocalApplication.getInstance().getLoginUser(getActivity()).userId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

}
