package cn.hwh.sports.activity.workout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
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
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.db.WorkoutSyncDBData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetWorkoutDetailInfoRE;
import cn.hwh.sports.entity.net.GetWxShareWorkoutTextRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.chart.MarkerViewForRunning;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.ui.common.WorkoutInfoHrZoneView;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.EffortPointU;
import cn.hwh.sports.utils.HrZoneU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2017/2/22.
 */

public class WorkoutRunningIndoorActivity extends BaseActivity implements OnChartValueSelectedListener {


    /* contains */
    private static final String TAG = "WorkoutRunningIndoorActivity";

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

    @BindView(R.id.tv_effort_start_time)
    TextView mStartTimeTv;
    @BindView(R.id.tv_duration)
    TextView mDurationTv;
    @BindView(R.id.tv_length)
    TextView mLengthTv;
    @BindView(R.id.tv_calorie)
    TextView mCalorieTv;//卡路里文本
    @BindView(R.id.tv_step_count)
    TextView mStepCountTv;
    @BindView(R.id.tv_avg_power)
    TextView mPowerTv;//速度
    @BindView(R.id.tv_candence)
    TextView mCandenceTv;//步频


    @BindView(R.id.tv_effective_time)
    TextView mEffectiveTimeTv;


    @BindView(R.id.tv_avg_hr_big)
    TextView mBigHrTv;//平均心率文本
    @BindView(R.id.chart)
    LineChart mChart;//曲线图
    @BindView(R.id.tv_chart_end_time)
    TextView mEndTimeTv;//曲线图上结束时间文本

    @BindView(R.id.btn_syn)
    ImageButton mSyncBtn;
    @BindView(R.id.btn_share)
    ImageButton mShareBtn;

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


    DialogBuilder mDialogBuilder;

    /* data */
    private int mWorkoutId;//记录ID
    private String mWorkoutStartTime;//记录开始时间
    private int mCalorie;//上个页面传过来的卡路里

    private int mMaxHr;
    private int mUserId;
    private WorkoutDE mWorkoutDE;//锻炼记录信息

    private List<TimeSplitDE> mTimeSplits = new ArrayList<>();
    int zone0 = 0, zone1 = 0, zone2 = 0, zone3 = 0, zone4 = 0, zone5 = 0;

    /* network data */
    private Callback.Cancelable mCancelable;

    private Typeface mTypeface;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_info_running_indoor;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取失败
                case MSG_POST_ERROR:
                    mLoadingView.LoadError((String) msg.obj);
                    T.showShort(WorkoutRunningIndoorActivity.this, (String) msg.obj);
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
                    web.setThumb(new UMImage(WorkoutRunningIndoorActivity.this, shareEntity.image));
                    new ShareAction(WorkoutRunningIndoorActivity.this)
                            .withMedia(web)
                            .setPlatform(media)
                            .setCallback(umShareListener).share();

                    break;
                //获取分享失败
                case MSG_GET_SHARE_ERROR:
                    T.showShort(WorkoutRunningIndoorActivity.this, (String) msg.obj);
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
            T.showShort(WorkoutRunningIndoorActivity.this, "分享成功啦");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            T.showShort(WorkoutRunningIndoorActivity.this, "分享失败啦");
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            T.showShort(WorkoutRunningIndoorActivity.this, "分享取消了");
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
                T.showShort(WorkoutRunningIndoorActivity.this, "正在同步..");
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
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void initData() {
        mWorkoutId = getIntent().getExtras().getInt("workoutId");
        mWorkoutStartTime = getIntent().getExtras().getString("workoutStartTime");
        mCalorie = getIntent().getExtras().getInt("calorie");
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");

        mUserId = LocalApplication.getInstance().getLoginUser(WorkoutRunningIndoorActivity.this).userId;
        mMaxHr = LocalApplication.getInstance().getLoginUser(WorkoutRunningIndoorActivity.this).maxHr;

        if (mWorkoutId == 0) {
            mWorkoutId = (int) WorkoutSyncDBData.getWorkoutServiceId(mWorkoutStartTime, mUserId);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mDurationTv.setTypeface(mTypeface);
        mLengthTv.setTypeface(mTypeface);
        mCalorieTv.setTypeface(mTypeface);
        mCandenceTv.setTypeface(mTypeface);
        mStepCountTv.setTypeface(mTypeface);
        mPowerTv.setTypeface(mTypeface);


        mDialogBuilder = new DialogBuilder();
        mStartTimeTv.setText(mWorkoutStartTime);

        mGraphicRange0Tv.setText((int) (mMaxHr * 0.2) + "~" + (int) (mMaxHr * 0.5 + 1));
        mGraphicRange1Tv.setText((int) (mMaxHr * 0.5) + "~" + (int) (mMaxHr * 0.6 + 1));
        mGraphicRange2Tv.setText((int) (mMaxHr * 0.6) + "~" + (int) (mMaxHr * 0.7 + 1));
        mGraphicRange3Tv.setText((int) (mMaxHr * 0.7) + "~" + (int) (mMaxHr * 0.8 + 1));
        mGraphicRange4Tv.setText((int) (mMaxHr * 0.8) + "~" + (int) (mMaxHr * 0.9 + 1));
        mGraphicRange5Tv.setText((int) (mMaxHr * 0.9) + "~" + (int) (mMaxHr));

        initPieChart();
        initLineChart();

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
        if (UploadDBData.getUploadDEByWorkoutStartTime(mWorkoutStartTime) != null) {
            mShareBtn.setVisibility(View.GONE);
            mSyncBtn.setVisibility(View.VISIBLE);
        } else {
            mSyncBtn.setVisibility(View.GONE);
            if (mWorkoutDE != null) {
                if (mWorkoutDE.ownerId == mUserId) {
                    mShareBtn.setVisibility(View.VISIBLE);
                } else {
                    mShareBtn.setVisibility(View.GONE);
                }
                mShareBtn.setVisibility(View.VISIBLE);
            }
        }
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
                final RequestParams requestParams = RequestParamsBuilder.buildGetWorkoutInfoRP(WorkoutRunningIndoorActivity.this,
                        UrlConfig.URL_GET_WORKOUT_DETAIL_INFO, mWorkoutId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetWorkoutDetailInfoRE re = JSON.parseObject(result.result, GetWorkoutDetailInfoRE.class);
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
    private void saveWorkoutInfoToDB(final GetWorkoutDetailInfoRE re) {
        mWorkoutDE = new WorkoutDE(re.id, re.name, re.starttime, re.duration, re.length,
                re.calorie, 0, 0, 0, 0, 0, re.avgheartrate, re.maxheartrate,
                re.minheartrate, mUserId, mUserId, 0, re.stepcount, SportEnum.SportStatus.FINISH,
                SportEnum.EffortType.FREE_INDOOR, re.effort_point, re.planned_goal,
                re.planned_duration, 3);
        WorkoutDBData.saveWorkout(mWorkoutDE);

        long nowTime = System.currentTimeMillis();
        for (int i = 0, size = re.splits.size(); i < size; i++) {
            GetWorkoutDetailInfoRE.SplitsEntity split = re.splits.get(i);
            TimeSplitDE timeDe = new TimeSplitDE(nowTime + i, re.starttime, split.timeoffset / 60,
                    split.avgheartrate, SportEnum.SportStatus.FINISH, split.duration, mUserId, mUserId);
            mTimeSplits.add(timeDe);
        }
        TimeSplitDBData.save(mTimeSplits);

        List<HrDE> mHrLists = new ArrayList<>();
        long index = nowTime;
        for (GetWorkoutDetailInfoRE.BpmsEntity bpmsEntity : re.bpms) {
            mHrLists.add(new HrDE(index, re.starttime, re.starttime, bpmsEntity.timeoffset,bpmsEntity.timeoffset,
                    bpmsEntity.bpm, bpmsEntity.timeoffset / 60, 0, bpmsEntity.stridefrequency));
            index++;
        }
        HrDBData.save(mHrLists);
    }

    /**
     * 更新界面
     */
    private void updateWorkoutViews() {
//        DecimalFormat df = new java.text.DecimalFormat("#.##");
        mDurationTv.setText(mWorkoutDE.duration / 60 + "");
        mCalorieTv.setText((int) mWorkoutDE.calorie + "");
        mLengthTv.setText(LengthUtils.formatLength(mWorkoutDE.length));
        mStepCountTv.setText(mWorkoutDE.endStep + "");
        mPowerTv.setText(mWorkoutDE.avgHr * 100 / mMaxHr + "%");
        mCandenceTv.setText((int) (mWorkoutDE.endStep * 60 / mWorkoutDE.duration) + "");

        updateZoneView();
        mBigHrTv.setText(mWorkoutDE.avgHr + "");
        mEndTimeTv.setText(mTimeSplits.size() + "分钟");
        setChartData();
        mChart.invalidate();
        mChart.animateX(1000);

        //模拟一个touch
//        int x = 300;
//        int y = 100;
//        final long downTime = SystemClock.uptimeMillis();
//        final MotionEvent downEvent = MotionEvent.obtain(
//                downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
//        final MotionEvent upEvent = MotionEvent.obtain(
//                downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
//        mChart.dispatchTouchEvent(downEvent);
//        mChart.dispatchTouchEvent(upEvent);
//        downEvent.recycle();
//        upEvent.recycle();
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


    private void initLineChart() {
        mChart.setScaleEnabled(false);//设置是否缩放
        mChart.getLegend().setEnabled(false);//设置图例是否显示
        // no description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.TRANSPARENT);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        mChart.getAxisLeft().setDrawAxisLine(false);//显示数值轴
        mChart.getAxisLeft().setDrawGridLines(false);//显示边框
        mChart.getAxisLeft().setDrawZeroLine(false);//显示
        mChart.getAxisRight().setDrawZeroLine(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisRight().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(100f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(100f);

        MarkerViewForRunning mv = new MarkerViewForRunning(this, R.layout.chart_custom_marker_view, mWorkoutStartTime);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        mChart.setOnChartValueSelectedListener(this);
    }

    private void setChartData() {
        List<HrDE> mHrDeList = HrDBData.getHrListInWorkout(mWorkoutStartTime);

        ArrayList<Entry> hrValues = new ArrayList<Entry>();
        ArrayList<Entry> stepValues = new ArrayList<Entry>();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum((float) (0 - mWorkoutDE.duration * 0.15));
        xAxis.setAxisMaximum((float) (mWorkoutDE.duration * 1.15));

        int interval = mHrDeList.size() / 100 + 1;
        int index = 0;

        for (HrDE hr : mHrDeList) {
            if (index % interval == 0) {
                hrValues.add(new Entry(hr.timeOffSet, hr.heartbeat * 100 / 309));
                stepValues.add(new Entry(hr.timeOffSet, hr.strideFreQuency * 100 / 386));
            }
            index++;
        }

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
        stepSet.setColor(Color.parseColor("#ff4612"));
        stepSet.setLineWidth(1.5f);
        stepSet.setDrawCircles(false);
        stepSet.setDrawValues(false);
        stepSet.setHighLightColor(Color.TRANSPARENT);
        stepSet.setDrawCircleHole(false);
        stepSet.setDrawFilled(true);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable hrDrawable = ContextCompat.getDrawable(this, R.drawable.chart_fade_contrast);
            hrSet.setFillDrawable(hrDrawable);
            Drawable stepDrawable = ContextCompat.getDrawable(this, R.drawable.chart_fade_accent);
            stepSet.setFillDrawable(stepDrawable);
        } else {
            hrSet.setFillColor(Color.parseColor("#ffb300"));
            stepSet.setFillColor(Color.parseColor("#ff4612"));
        }

        // create a data object with the datasets
        LineData data = new LineData(hrSet, stepSet);

        mChart.setData(data);
        mChart.getData().setHighlightEnabled(true);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

    }

    /**
     * 开启分享窗口
     */
    private void showShareWindow() {
        mDialogBuilder.showShareDialog(WorkoutRunningIndoorActivity.this);
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
     * 更新同步相关页面
     */
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

    /**
     * 获取分享信息
     *
     * @param shareType
     */
    private void getShareMsg(final int shareType) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetWeixinShareWorkoutText(WorkoutRunningIndoorActivity.this,
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

}
