package cn.hwh.sports.activity.workout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.db.WorkoutSyncDBData;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetWorkoutInfoRE;
import cn.hwh.sports.entity.net.GetWxShareWorkoutTextRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.chart.HeartbeatFormatter;
import cn.hwh.sports.ui.chart.HeartbeatPowerFormatter;
import cn.hwh.sports.ui.chart.WorkoutTimerFormatter;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.HrZoneU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/12/21.
 */

public class WorkoutFreeEffortActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "WorkoutFreeEffortActivity";

    private static final int MSG_POST_ERROR = 0;
    private static final int MSG_POST_OK = 1;
    private static final int MSG_GET_SHARE_OK = 2;
    private static final int MSG_GET_SHARE_ERROR = 3;


    private static final int SHARE_WE_CHAT_C = 0x01;
    private static final int SHARE_WE_CHAT_MOMENT = 0x02;

    /* view */
    @BindView(R.id.ll_base)
    LinearLayout mBaseLl;
    @BindView(R.id.v_loading)
    MyLoadingView mLoadingView;

    @BindView(R.id.tv_effort_name)
    TextView mEffortNameTv;
    @BindView(R.id.tv_effort_start_time)
    TextView mStartTimeTv;
    @BindView(R.id.tv_duration)
    TextView mDurationTv;
    @BindView(R.id.tv_calorie)
    TextView mCalorieTv;//卡路里文本
    @BindView(R.id.tv_avg_power)
    TextView mAvgPowerTv;//平均强度文本

    @BindView(R.id.tv_effective_time)
    TextView mEffectiveTimeTv;


    @BindView(R.id.tv_avg_hr_big)
    TextView mBigHrTv;
    @BindView(R.id.chart)
    BarChart mChart;
    @BindView(R.id.tv_chart_end_time)
    TextView mEndTimeTv;

    @BindView(R.id.btn_syn)
    ImageButton mSyncBtn;
    @BindView(R.id.btn_share)
    ImageButton mShareBtn;

    DialogBuilder mDialogBuilder;
    @BindView(R.id.tv_hr_scale_1)
    TextView tvHrScale1;
    @BindView(R.id.tv_hr_scale_2)
    TextView tvHrScale2;
    @BindView(R.id.tv_hr_scale_3)
    TextView tvHrScale3;
    @BindView(R.id.tv_hr_scale_4)
    TextView tvHrScale4;
    @BindView(R.id.tv_hr_scale_5)
    TextView tvHrScale5;

    @BindView(R.id.chart_small)
    PieChart mPieChartSmall;//内圈
    @BindView(R.id.chart_big)
    PieChart mPieChartBig;//放大的外圈
    @BindView(R.id.tv_curr_pie_percent)
    TextView mCurrPiePercentTv;
    @BindView(R.id.tv_curr_pie_zone)
    TextView mCurrPieZoneTv;
    @BindView(R.id.tv_curr_pie_time)
    TextView mCurrPieTimeTv;
    @BindView(R.id.tv_graphic_range_0)
    TextView mGraphicRange0Tv;//范围文本0
    @BindView(R.id.tv_graphic_range_1)
    TextView mGraphicRange1Tv;//范围文本1
    @BindView(R.id.tv_graphic_range_2)
    TextView mGraphicRange2Tv;//范围文本2
    @BindView(R.id.tv_graphic_range_3)
    TextView mGraphicRange3Tv;//范围文本3
    @BindView(R.id.tv_graphic_range_4)
    TextView mGraphicRange4Tv;//范围文本4
    @BindView(R.id.tv_graphic_range_5)
    TextView mGraphicRange5Tv;//范围文本5

    int zone0 = 0, zone1 = 0, zone2 = 0, zone3 = 0, zone4 = 0, zone5 = 0;

    /* data */
    private int mWorkoutId;//记录ID
    private String mWorkoutStartTime;//记录开始时间
    private int mCalorie;//上个页面传过来的卡路里
    private int mResource;//来源
    private int mType;//运动类型

    private int mMaxHr;
    private int mUserId;
    private WorkoutDE mWorkoutDE;//锻炼记录信息

    private Typeface mTypeface;

    private List<TimeSplitDE> mTimeSplits = new ArrayList<>();

    ArrayList<BarEntry> mBarData = new ArrayList<BarEntry>();
    BarDataSet mBarSet;

    /* network data */
    private Callback.Cancelable mCancelable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_info_free_effort;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取失败
                case MSG_POST_ERROR:
                    mLoadingView.LoadError((String) msg.obj);
                    T.showShort(WorkoutFreeEffortActivity.this, (String) msg.obj);
                    break;
                //获取成功
                case MSG_POST_OK:
                    updateWorkoutViews();
                    mLoadingView.loadFinish();
                    break;
                //获取分享成功
                case MSG_GET_SHARE_OK:
                    GetWxShareWorkoutTextRE shareEntity = (GetWxShareWorkoutTextRE) msg.obj;
                    SHARE_MEDIA media = SHARE_MEDIA.WEIXIN;
                    if (msg.arg1 == SHARE_WE_CHAT_C) {
                        media = SHARE_MEDIA.WEIXIN;
                    } else if (msg.arg1 == SHARE_WE_CHAT_MOMENT) {
                        media = SHARE_MEDIA.WEIXIN_CIRCLE;
                    }
//                    Log.e(TAG,"link:" + shareEntity.link);
                    UMWeb web = new UMWeb(shareEntity.link);
                    web.setTitle(shareEntity.title);//标题
                    web.setDescription(shareEntity.text);//描述
                    web.setThumb(new UMImage(WorkoutFreeEffortActivity.this, shareEntity.image));
                    new ShareAction(WorkoutFreeEffortActivity.this)
                            .withMedia(web)
                            .setPlatform(media)
                            .setCallback(umShareListener).share();
                    break;
                //获取分享失败
                case MSG_GET_SHARE_ERROR:
                    T.showShort(WorkoutFreeEffortActivity.this, (String) msg.obj);
                    break;
            }
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            T.showShort(WorkoutFreeEffortActivity.this, "分享成功啦");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            T.showShort(WorkoutFreeEffortActivity.this, "分享失败啦");
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            T.showShort(WorkoutFreeEffortActivity.this, "分享取消了");
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_share, R.id.btn_syn})
    public void onClick(View view) {
        switch (view.getId()) {
            //回退
            case R.id.btn_back:
                finish();
                break;
            //分享
            case R.id.btn_share:
                showShareWindow();
                break;
            case R.id.btn_syn:
                T.showShort(WorkoutFreeEffortActivity.this, "正在同步..");
                break;
        }
    }


    /**
     * 手表同步手机完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(WorkoutEndEE event) {
        updateSyncView();
    }

    @Override
    protected void initData() {
        mWorkoutId = getIntent().getExtras().getInt("workoutId");
        mWorkoutStartTime = getIntent().getExtras().getString("workoutStartTime");
        mCalorie = getIntent().getExtras().getInt("calorie");
        mResource = getIntent().getExtras().getInt("resource");
        mType = getIntent().getExtras().getInt("type");

        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mUserId = LocalApplication.getInstance().getLoginUser(WorkoutFreeEffortActivity.this).userId;
        mMaxHr = LocalApplication.getInstance().getLoginUser(WorkoutFreeEffortActivity.this).maxHr;

        if (mWorkoutId == 0) {
            mWorkoutId = (int) WorkoutSyncDBData.getWorkoutServiceId(mWorkoutStartTime, mUserId);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mDialogBuilder = new DialogBuilder();
        mStartTimeTv.setText(mWorkoutStartTime);

        mDurationTv.setTypeface(mTypeface);
        mAvgPowerTv.setTypeface(mTypeface);
        mCalorieTv.setTypeface(mTypeface);

        if (mResource == SportEnum.resource.APP) {
            if (mType == SportEnum.EffortType.LOU_TI) {
                mEffortNameTv.setText("楼梯机");
            } else if (mType == SportEnum.EffortType.DAN_CHE) {
                mEffortNameTv.setText("动感单车");
            } else if (mType == SportEnum.EffortType.TUO_YUAN) {
                mEffortNameTv.setText("椭圆机");
            } else if (mType == SportEnum.EffortType.HUA_CHUAN) {
                mEffortNameTv.setText("划船机");
            } else if (mType == SportEnum.EffortType.XIAO_QI_XIE) {
                mEffortNameTv.setText("小器械");
            } else if (mType == SportEnum.EffortType.TIAO_SHENG){
                mEffortNameTv.setText("跳绳");
            } else {
                mEffortNameTv.setText("无器械");
            }
        } else if (mResource == SportEnum.resource.PC) {
            mEffortNameTv.setText("健身房锻炼");
        } else {
            mEffortNameTv.setText("日常锻炼");
        }

        tvHrScale1.setText(mMaxHr + "");
        tvHrScale2.setText((int) (mMaxHr * 0.8) + "");
        tvHrScale3.setText((int) (mMaxHr * 0.6) + "");
        tvHrScale4.setText((int) (mMaxHr * 0.4) + "");
        tvHrScale5.setText((int) (mMaxHr * 0.2) + "");

        mGraphicRange0Tv.setText((int) (mMaxHr * 0.2) + "~" + (int) (mMaxHr * 0.5 + 1));
        mGraphicRange1Tv.setText((int) (mMaxHr * 0.5) + "~" + (int) (mMaxHr * 0.6 + 1));
        mGraphicRange2Tv.setText((int) (mMaxHr * 0.6) + "~" + (int) (mMaxHr * 0.7 + 1));
        mGraphicRange3Tv.setText((int) (mMaxHr * 0.7) + "~" + (int) (mMaxHr * 0.8 + 1));
        mGraphicRange4Tv.setText((int) (mMaxHr * 0.8) + "~" + (int) (mMaxHr * 0.9 + 1));
        mGraphicRange5Tv.setText((int) (mMaxHr * 0.9) + "~" + (int) (mMaxHr));
        initLineChart();
        initPieChart();
        mChart.invalidate();
    }

    @Override
    protected void doMyCreate() {
        EventBus.getDefault().register(this);
        //查看数据库中是否存在
        mWorkoutDE = WorkoutDBData.getWorkoutByStartTime(mUserId, mWorkoutStartTime);

        //若记录为空, 从网络获取
        if (mWorkoutDE == null) {
            postGetWorkoutInfo();
        } else {
            if (mCalorie != 0) {
                mWorkoutDE.calorie = mCalorie;
            }
            WorkoutDBData.update(mWorkoutDE);
            mTimeSplits = TimeSplitDBData.getSplitsByWorkout(mWorkoutDE);
            mHandler.sendEmptyMessage(MSG_POST_OK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSyncView();

    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_POST_OK);
            mHandler.removeMessages(MSG_POST_ERROR);
        }
    }

    /**
     * 获取锻炼记录信息
     */
    private void postGetWorkoutInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildGetWorkoutInfoRP(WorkoutFreeEffortActivity.this,
                        UrlConfig.URL_GET_WORKOUT_INFO, mWorkoutId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetWorkoutInfoRE re = JSON.parseObject(result.result, GetWorkoutInfoRE.class);
                            saveWorkoutInfoToDB(re);
                            mHandler.sendEmptyMessage(MSG_POST_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_POST_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
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

    /**
     * 保存数据到数据库
     *
     * @param re
     */
    private void saveWorkoutInfoToDB(final GetWorkoutInfoRE re) {
        mWorkoutDE = new WorkoutDE(re.id, re.name, re.starttime, re.duration, 0,
                re.calorie, 0, 0, 0, 0, 0, re.avgheartrate, re.maxheartrate,
                re.minheartrate, mUserId, mUserId, 0, re.stepcount, SportEnum.SportStatus.FINISH,
                SportEnum.EffortType.FREE_INDOOR, re.effort_point, re.planned_goal,
                re.planned_duration, 3);
        WorkoutDBData.saveWorkout(mWorkoutDE);

        long nowTime = System.currentTimeMillis();
        for (int i = 0, size = re.splits.size(); i < size; i++) {
            GetWorkoutInfoRE.SplitsEntity split = re.splits.get(i);
            TimeSplitDE timeDe = new TimeSplitDE(nowTime + i, re.starttime, split.timeoffset / 60,
                    split.avgheartrate, SportEnum.SportStatus.FINISH, split.duration, mUserId, mUserId);
            mTimeSplits.add(timeDe);
        }
        TimeSplitDBData.save(mTimeSplits);
    }

    /**
     * 更新界面
     */
    private void updateWorkoutViews() {
//        DecimalFormat df = new java.text.DecimalFormat("#.##");
        mDurationTv.setText(mTimeSplits.size() + "");
        mCalorieTv.setText((int) mWorkoutDE.calorie + "");
        mAvgPowerTv.setText(mWorkoutDE.avgHr * 100 / mMaxHr + "%");

        updateZoneView();

        mBigHrTv.setText(mWorkoutDE.avgHr + "");
        mEndTimeTv.setText(mTimeSplits.size() + "分钟");
        setLineChartData();
        mChart.invalidate();
    }

    /**
     * 更新区间有关的UI
     */
    private void updateZoneView() {
        for (TimeSplitDE split : mTimeSplits) {
            int zone = EffortPointU.getZone(split.avgHr * 100 / mMaxHr);
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
        int maxZone = 0;
        int maxZoneValue = zone0;
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

        mEffectiveTimeTv.setText("有效时长：" + (zone2 + zone3 + zone4 + zone5) + "分钟");
        setPieChartSelectData(maxZone, maxZoneValue);
    }

    /**
     * 初始化饼状图
     */
    private void initPieChart() {
        mPieChartBig.setUsePercentValues(true);
        mPieChartBig.getDescription().setEnabled(false);
        mPieChartBig.setDrawHoleEnabled(true);
        mPieChartBig.setHoleColor(Color.TRANSPARENT);

        mPieChartBig.setTransparentCircleColor(Color.TRANSPARENT);
        mPieChartBig.setTransparentCircleAlpha(110);

        mPieChartBig.setHoleRadius(74f);
        mPieChartBig.setTransparentCircleRadius(74f);

        mPieChartBig.setDrawCenterText(true);

        mPieChartBig.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChartBig.setRotationEnabled(true);
        mPieChartBig.setHighlightPerTapEnabled(true);

        mPieChartBig.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mPieChartBig.setDrawEntryLabels(false);

        mPieChartBig.getLegend().setEnabled(false);//设置图例是否显示
        mPieChartBig.getDescription().setEnabled(false);
        mPieChartBig.setRotationEnabled(false);

        mPieChartBig.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                setPieChartSelectData((int) h.getX(), (int) e.getY());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        mPieChartSmall.setUsePercentValues(true);
        mPieChartSmall.getDescription().setEnabled(false);
        mPieChartSmall.setDrawHoleEnabled(true);
        mPieChartSmall.setHoleColor(Color.TRANSPARENT);

        mPieChartSmall.setTransparentCircleColor(Color.TRANSPARENT);
        mPieChartSmall.setTransparentCircleAlpha(110);

        mPieChartSmall.setHoleRadius(86f);
        mPieChartSmall.setTransparentCircleRadius(86f);

        mPieChartSmall.setDrawCenterText(true);

        mPieChartSmall.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChartSmall.setRotationEnabled(true);
        mPieChartSmall.setHighlightPerTapEnabled(false);

        mPieChartSmall.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mPieChartSmall.setDrawEntryLabels(false);

        mPieChartSmall.getLegend().setEnabled(false);//设置图例是否显示
        mPieChartSmall.getDescription().setEnabled(false);
        mPieChartSmall.setRotationEnabled(false);
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

        mPieChartSmall.setData(smallData);

        // undo all highlights
        mPieChartSmall.highlightValues(null);

        for (IDataSet<?> set : mPieChartSmall.getData().getDataSets()) {
            set.setDrawValues(false);
        }
        mPieChartSmall.invalidate();

        PieDataSet bigDataSet = new PieDataSet(entries, "");
        bigDataSet.setSliceSpace(0f);
        bigDataSet.setSelectionShift(5f);
        bigDataSet.setColors(bigColors);
        //dataSet.setSelectionShift(0f);

        PieData bigData = new PieData(bigDataSet);
        bigData.setValueFormatter(null);

        mPieChartBig.setData(bigData);

        // undo all highlights
        mPieChartBig.highlightValues(null);

        for (IDataSet<?> set : mPieChartBig.getData().getDataSets()) {
            set.setDrawValues(false);
        }
        mPieChartBig.invalidate();

        mCurrPiePercentTv.setText((int) zoneValue * 100 / mTimeSplits.size() + "");
        mCurrPieZoneTv.setText(HrZoneU.getDescribeByHrZone(zone));
        mCurrPieTimeTv.setText(zoneValue + "分钟");
    }

    /**
     * 插入线性图标的数据
     */
    private void initLineChart() {
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setScaleEnabled(false);//设置是否缩放
        mChart.getLegend().setEnabled(false);//设置图例是否显示
        mChart.getDescription().setEnabled(false);
        mChart.setPadding(0, 0, 0, 0);


        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setFitBars(true);
        mChart.getAxisLeft().setDrawAxisLine(false);//显示数值轴
        mChart.getAxisLeft().setDrawGridLines(false);//显示边框
        mChart.getAxisLeft().setDrawZeroLine(false);//显示
        mChart.getAxisRight().setDrawZeroLine(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisRight().setDrawAxisLine(false);


        WorkoutTimerFormatter timerFormatter = new WorkoutTimerFormatter();
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(timerFormatter);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(2, false);
        xAxis.setTextSize(0f);
//        xAxis.setTextColor(Color.parseColor("#00adadad"));
        xAxis.setAxisMinimum(-0.5f);

        HeartbeatPowerFormatter custom = new HeartbeatPowerFormatter();
        HeartbeatFormatter nullCustom = new HeartbeatFormatter(mMaxHr);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(nullCustom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(Color.TRANSPARENT);
        leftAxis.setTextSize(0f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setValueFormatter(nullCustom);
        rightAxis.setEnabled(false);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(100f);
        rightAxis.setTextSize(0f);
        leftAxis.setTextColor(Color.TRANSPARENT);
    }


    private void setLineChartData() {
//        Log.v(TAG,"mTimeSplits.size:" + mTimeSplits.size());
        List<Integer> colors = new ArrayList<>();
        for (int i = 0, size = mTimeSplits.size(); i < size; i++) {
            mBarData.add(new BarEntry(i, mTimeSplits.get(i).avgHr * 100 / mMaxHr));
            colors.add(ColorU.getColorByHeartbeat(mTimeSplits.get(i).avgHr * 100 / mMaxHr));
        }
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
     * 开启分享窗口
     */
    private void showShareWindow() {
        mDialogBuilder.showShareDialog(WorkoutFreeEffortActivity.this);
        mDialogBuilder.setListener(new DialogBuilder.ShareDialogListener() {
            @Override
            public void onShareWeChatCClick() {
                getShareMsg(SHARE_WE_CHAT_C);
            }

            @Override
            public void onShareWeChatMomentClick() {
                getShareMsg(SHARE_WE_CHAT_MOMENT);
            }

            @Override
            public void onCancelBtnClick() {

            }
        });
    }

    /**
     * 获取分享信息
     *
     * @param shareType
     */
    private void getShareMsg(final int shareType) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetWeixinShareWorkoutText(WorkoutFreeEffortActivity.this,
                        UrlConfig.URL_GET_WE_CHAT_SHARE_WORKOUT_TEXT, mWorkoutId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            Message msg = new Message();
                            msg.what = MSG_GET_SHARE_OK;
                            msg.arg1 = shareType;
                            msg.obj = JSON.parseObject(result.result, GetWxShareWorkoutTextRE.class);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_POST_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
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

    private void updateSyncView(){
        if (UploadDBData.getUploadDEByWorkoutStartTime(mWorkoutStartTime) != null) {
            mShareBtn.setVisibility(View.GONE);
            mSyncBtn.setVisibility(View.VISIBLE);
        } else {
            if (mWorkoutDE != null) {
                if (mWorkoutDE.ownerId == mUserId) {
                    mShareBtn.setVisibility(View.VISIBLE);
                } else {
                    mShareBtn.setVisibility(View.GONE);
                }
            }
            mSyncBtn.setVisibility(View.GONE);
        }
    }

}
