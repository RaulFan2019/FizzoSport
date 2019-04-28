package cn.hwh.sports.fragment.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.main.ArticleWebViewActivity;
import cn.hwh.sports.activity.main.FizzoHelpActivity;
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.monitor.MonitorCalorieListActivity;
import cn.hwh.sports.activity.monitor.MonitorDistanceListActivity;
import cn.hwh.sports.activity.monitor.MonitorSportTimeListActivity;
import cn.hwh.sports.activity.monitor.RestHRChangeInfoActivity;
import cn.hwh.sports.activity.monitor.MonitorStepListActivity;
import cn.hwh.sports.activity.monitor.WeightChangeInfoActivity;
import cn.hwh.sports.activity.settings.BleAutoBindActivity;
import cn.hwh.sports.activity.workout.WorkoutListActivity;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.DayMotionSummaryDBData;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.DayMotionSummaryDE;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.event.MaxHrEE;
import cn.hwh.sports.entity.event.SyncWatchWorkoutEE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.DayMotionSummaryRE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.service.UploadWatcherService;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.pulltorefreshlib.PullToRefreshLayout;
import cn.hwh.sports.ui.pulltorefreshlib.PullToSyncLayout;
import cn.hwh.sports.ui.pulltorefreshlib.PullableScrollView;
import cn.hwh.sports.ui.wheelindicator.WheelIndicatorItem;
import cn.hwh.sports.ui.wheelindicator.WheelIndicatorView;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.NetworkU;
import cn.hwh.sports.utils.StringU;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;
import cn.hwh.sports.utils.WeekSportImageU;

/**
 * Created by Raul.Fan on 2016/11/11.
 */

public class MainHealthFragment extends BaseFragment {


    /* contain */
    private static final String TAG = "MainHealthFragment";

    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;

    private static final int FROM_DAY_DELAY = 30;//延时天数

    /**
     * view
     */
    @BindView(R.id.wheel_view_step)
    WheelIndicatorView mStepWheelView;//步数进度条
    @BindView(R.id.tv_step)
    TextView mStepTv;//步数文本
    @BindView(R.id.wheel_view_length)
    WheelIndicatorView mLengthWheelView;//长度进度条
    @BindView(R.id.tv_length)
    TextView mLengthTv;//长度文本
    @BindView(R.id.wheel_view_cal)
    WheelIndicatorView mCalWheelView;//卡路里进度条
    @BindView(R.id.tv_calorie)
    TextView mCalorieTv;//卡路里文本
    @BindView(R.id.wheel_view_sport_time)
    WheelIndicatorView mSportTimeWheelViewSport;//锻炼时长进度条
    @BindView(R.id.tv_sport_time)
    TextView mSportTimeTv;//锻炼时长文本

    @BindView(R.id.tv_hr_target_high)
    TextView mHrTargetHighTv;//靶心率上限
    @BindView(R.id.tv_hr_target_low)
    TextView mHrTargetLowTv;//靶心率下限
    @BindView(R.id.tv_hr_warning)
    TextView mHrWarningTv;//报警心率

    @BindView(R.id.v_watch)
    View mWatchV;//手表标志
    @BindView(R.id.v_watch_battery)
    View mWatchBatteryV;//手环电量
    @BindView(R.id.btn_bind_watch)
    Button mBindBtn;
    @BindView(R.id.ll_watch_connecting)
    LinearLayout mWatchConnectingV;

    /* local view about sync*/
    @BindView(R.id.ll_sync)
    LinearLayout mSyncLl;
    @BindView(R.id.v_sync_anim)
    View mSyncAnimV;
    @BindView(R.id.tv_sync_time)
    TextView mSyncTimeTv;
    @BindView(R.id.fl_sync_pro)
    FrameLayout mSyncProLl;//同步状态布局
    @BindView(R.id.iv_progress)
    ImageView mSyncProIv;//进度图片
    private ClipDrawable mClipDrawable;

    @BindView(R.id.ll_low_battery)
    LinearLayout mLowBatteryLl;

    @BindView(R.id.ll_curr_hr)
    RelativeLayout mCurrHrLl;
    @BindView(R.id.tv_curr_hr)
    TextView mCurrHrTv;

    @BindView(R.id.v_week_sport)
    View mWeekSportV;//周锻炼视图
    @BindView(R.id.tv_week_sport_target)
    TextView mWeekSportTargetTv;//周锻炼目标文本
    @BindView(R.id.tv_week_sport_do)
    TextView mWeekSportDoTv;//周锻炼已完成文本

    @BindView(R.id.tv_rest_hr)
    TextView mRestHrTv;//静息心率文本
    @BindView(R.id.tv_weight)
    TextView mWeightTv;//体重文本
    @BindView(R.id.sv_health)
    PullableScrollView mHealthSv;
    @BindView(R.id.tv_change_coach)
    ImageView mChangeCoachTv;
    @BindView(R.id.ptr_view)
    PullToSyncLayout mRefreshL;

    @BindView(R.id.tv_error)
    TextView mErrorTipTv;

    RelativeLayout mLoadMoreLl;//加载更多页面

    DialogBuilder mDialog;

    private boolean mPostEnable;
    private Callback.Cancelable mPostCancelable;
    private ConnectionChangeReceiver myReceiver;

    private List<DayMotionSummaryRE.DaysBean> mDaysList;
    private List<DayMotionSummaryDE> mDayDEList;
    private int mCurrentDay;

    private RotateAnimation mSyncRotateAnim;
    private boolean mSyncAnimStarting = false;

    Typeface mNumberTypeFace;

    /* 构造函数 */
    public static MainHealthFragment newInstance() {
        MainHealthFragment fragment = new MainHealthFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_health;
    }


    @OnClick({R.id.ll_week_sport, R.id.ll_rest_hr, R.id.ll_help,
            R.id.ll_weight, R.id.ll_health_sign, R.id.wheel_view_step,
            R.id.wheel_view_length, R.id.tv_change_coach, R.id.wheel_view_cal,
            R.id.wheel_view_sport_time, R.id.btn_bind_watch, R.id.ll_watch_connecting})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击步数
            case R.id.wheel_view_step:
                startActivity(MonitorStepListActivity.class);
                break;
            //点击距离
            case R.id.wheel_view_length:
                startActivity(MonitorDistanceListActivity.class);
                break;
            //点击卡路里
            case R.id.wheel_view_cal:
                startActivity(MonitorCalorieListActivity.class);
                break;
            //点击锻炼时间
            case R.id.wheel_view_sport_time:
                startActivity(MonitorSportTimeListActivity.class);
                break;
            //点击绑定设备
            case R.id.btn_bind_watch:
                startActivity(BleAutoBindActivity.class);
                break;
            //点击锻炼次数
            case R.id.v_week_sport:

                break;
            case R.id.ll_week_sport:
                startActivity(WorkoutListActivity.class);
                break;
            //静息心率变化
            case R.id.ll_rest_hr:
                startActivity(RestHRChangeInfoActivity.class);
                break;
            //点击体重记录
            case R.id.ll_weight:
                startActivity(WeightChangeInfoActivity.class);
                break;
            //点击FIZZO助手
            case R.id.ll_help:
                startActivity(FizzoHelpActivity.class);
                break;
            //点击睡眠记录
//            case R.id.ll_sleep:
//                startActivity(SleepChangeInfoActivity.class);
//                break;
//            case R.id.ll_health_sign:
//                startActivity(HealthSignChangeInfoActivity.class);
//                break;
            //切换
            case R.id.tv_change_coach:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setTabSelection(MainActivity.TAB_COACH);
                break;
            case R.id.ll_watch_connecting:
                Bundle bundleC = new Bundle();
                bundleC.putString("url", "http://www.123yd.cn/fizzoh5/howToConnect.html");
                startActivity(ArticleWebViewActivity.class, bundleC);
                break;
        }
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostEnable = true;
            switch (msg.what) {
                case MSG_POST_OK:
                    mDialog.dismiss();
                    DayMotionSummaryRE entity = JSON.parseObject(msg.obj.toString(), DayMotionSummaryRE.class);
                    mDaysList = entity.getDays();
                    saveToDb();
                    break;
                case MSG_POST_ERROR:
                    mDialog.dismiss();
                    T.showShort(getActivity(), msg.obj.toString());
                    mDayDEList = DayMotionSummaryDBData.getDayMotionSummary(LocalApplication.getInstance().mLoginUser.userId);
                    if (mDayDEList != null && mDayDEList.size() > 0) {
                        //定位当前页数默认页面
                        mCurrentDay = mDayDEList.size() - 1;
                        //刷新页面
                        updateMonitorView(mDayDEList.size() - 1);
                    } else {
                        //防止mDayDEList 为null
                        mDayDEList = new ArrayList<>();
                    }
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
//            Log.v(TAG,"MSG_NEW_HEARTBEAT");
            mCurrHrTv.setText(event.hr + "");
        } else if (event.msg == BleManager.MSG_GET_HISTORY_NUM_ERROR) {
            mRefreshL.refreshFinish(PullToSyncLayout.FAIL);
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
        if (event.msg == SyncWatchWorkoutEE.MSG_FINISH) {
            T.showShort(getActivity(), "同步完成");
        } else if (event.msg == SyncWatchWorkoutEE.MSG_SYNC_ERROR) {
            T.showShort(getActivity(), event.workoutInfo);
        } else if (event.msg == SyncWatchWorkoutEE.MSG_FINISH_WITHOUT_TOAST) {
            //DO NOTHING
        }
        updateWatchView();
    }


    /**
     * 有一条历史记录同步完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWorkoutEnd(WorkoutEndEE event) {
        getDayMotionSummary();
    }


    @Override
    protected void initParams() {
        mDialog = new DialogBuilder();
        mPostEnable = true;
        mDaysList = new ArrayList<>();
        mDayDEList = new ArrayList<>();

        mStepWheelView.addWheelIndicatorItem(new WheelIndicatorItem(1.0f, Color.parseColor("#ff4612")));
        mLengthWheelView.addWheelIndicatorItem(new WheelIndicatorItem(1.0f, Color.parseColor("#ff4612")));
        mCalWheelView.addWheelIndicatorItem(new WheelIndicatorItem(1.0f, Color.parseColor("#ff4612")));
        mSportTimeWheelViewSport.addWheelIndicatorItem(new WheelIndicatorItem(1.0f, Color.parseColor("#ff4612")));

        mSyncRotateAnim = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.rotating);

        mLoadMoreLl = (RelativeLayout) mRefreshL.getChildAt(2);
        mLoadMoreLl.setVisibility(View.GONE);

        mClipDrawable = (ClipDrawable) mSyncProIv.getDrawable();
        mNumberTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mCurrHrTv.setTypeface(mNumberTypeFace);

        mRefreshL.setOnRefreshListener(new PullToSyncLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToSyncLayout pullToRefreshLayout) {
                if (BleManager.getBleManager().mBleConnectE != null &&
                        BleManager.getBleManager().mBleConnectE.mIsConnected) {
                    BleManager.getBleManager().mBleConnectE.reStartSync();
                    mRefreshL.refreshFinish(PullToRefreshLayout.SUCCEED);
                    updateWatchView();
                } else {
                    mRefreshL.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToSyncLayout pullToRefreshLayout) {
                mRefreshL.loadmoreFinish(PullToRefreshLayout.DONE);
            }
        });

        if (UserSPData.getUserRole(getActivity()) == AppEnum.UserRoles.MANAGE
                || UserSPData.getUserRole(getActivity()) == AppEnum.UserRoles.COACH) {
            mChangeCoachTv.setVisibility(View.VISIBLE);
        } else {
            mChangeCoachTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void causeGC() {
        if (mPostCancelable != null && !mPostCancelable.isCancelled()) {
            mPostCancelable.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
    }

    @Override
    protected void onVisible() {
        UserSPData.setUserPage(getActivity(), AppEnum.UserRoles.NORMAL);
        registerReceiver();
        mSyncAnimStarting = false;
        updateHrView();
        updateWatchView();
        getDayMotionSummary();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onInVisible() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver();
        mSyncRotateAnim.cancel();
    }

    /**
     * 更新页面数据
     *
     * @param pos
     */
    private void updateMonitorView(int pos) {
        DayMotionSummaryDE daysBean = mDayDEList.get(pos);
        int lStepPercent = 0;   //步数进度
        int lLengthPercent = 0;  //长度进度
        int lCalPercent = 0;   //卡路里进度
        int lSportTimePercent = 0; //运动时间进度

        if (daysBean.target_step != 0 & daysBean.target_length != 0 & daysBean.target_calorie != 0
                & daysBean.target_point != 0 & daysBean.target_exercised_minutes != 0) {

            lStepPercent = percent(daysBean.stepcount, daysBean.target_step);
            lLengthPercent = percent(daysBean.length, daysBean.target_length);  //长度进度
            lCalPercent = percent(daysBean.calorie, daysBean.target_calorie);   //卡路里进度
            lSportTimePercent = percent(daysBean.exercised_minutes, daysBean.target_exercised_minutes); //运动时间进度
        }


        //进度条进度
        mStepWheelView.setFilledPercent(lStepPercent);
        mLengthWheelView.setFilledPercent(lLengthPercent);
        mCalWheelView.setFilledPercent(lCalPercent);
        mSportTimeWheelViewSport.setFilledPercent(lSportTimePercent);

        mStepWheelView.notifyDataSetChanged();
        mLengthWheelView.notifyDataSetChanged();
        mCalWheelView.notifyDataSetChanged();
        mSportTimeWheelViewSport.notifyDataSetChanged();

        mStepWheelView.startItemsAnimation();
        mLengthWheelView.startItemsAnimation();
        mCalWheelView.startItemsAnimation();
        mSportTimeWheelViewSport.startItemsAnimation();

        //进度 数据文本
        mStepTv.setText(String.valueOf(daysBean.stepcount));    //步数文本
        mLengthTv.setText(LengthUtils.formatLength(daysBean.length));  //长度文本
        mCalorieTv.setText(String.valueOf(daysBean.calorie));      //卡路里文本
        mSportTimeTv.setText(String.valueOf(daysBean.exercised_minutes));   //运动时间文本

        //底部 每周锻炼
        mWeekSportTargetTv.setText(String.valueOf(daysBean.week_target_days));//周锻炼目标天数文本
        mWeekSportDoTv.setText(String.valueOf(daysBean.week_exercised_days));//周锻炼已完成天数文本
        mWeekSportV.setBackgroundResource(WeekSportImageU.getWeekSportImage(daysBean.week_target_days, daysBean.week_exercised_days));

        //每分钟静息心跳数
        mRestHrTv.setText(String.valueOf(daysBean.rest_hr));

        //体重
        mWeightTv.setText(String.valueOf(daysBean.weight));
    }

    /**
     * 保存数据至数据库
     */
    private void saveToDb() {
        int lDaysListNum = mDaysList.size();
        int lUserId = LocalApplication.getInstance().mLoginUser.userId;
        if (mDayDEList != null && mDayDEList.size() > 0) {
            mDayDEList.clear();
        }

        for (int i = 0; i < lDaysListNum; i++) {
            DayMotionSummaryRE.DaysBean summaryRE = mDaysList.get(i);
            DayMotionSummaryDE summaryDE = new DayMotionSummaryDE(
                    lUserId + summaryRE.date, lUserId, summaryRE.date, summaryRE.weekday, summaryRE.weight, summaryRE.rest_hr,
                    summaryRE.stepcount, summaryRE.target_step, "", summaryRE.length, summaryRE.target_length, "",
                    summaryRE.effort_point, summaryRE.target_point, "", summaryRE.exercised_minutes, summaryRE.target_exercised_minutes, "",
                    summaryRE.calorie, summaryRE.target_calorie, "", summaryRE.week_exercised_days, summaryRE.week_target_days, "",
                    summaryRE.sleepminutes, summaryRE.wakeuptimes, summaryRE.nremtimes, summaryRE.target_sleep_minutes);
            mDayDEList.add(summaryDE);
        }
        //保存至数据库
        DayMotionSummaryDBData.saveOrUpdate(mDayDEList);
        //定位当前页数默认页面
        mCurrentDay = mDayDEList.size() - 1;
        //刷新页面
        updateMonitorView(mDayDEList.size() - 1);
    }

    /**
     * 计算百分比
     *
     * @param d
     * @param e
     * @return
     */
    public static int percent(double d, double e) {
        double p = d / e;
        return (int) (p * 100);
    }

    /**
     * 获取一段时间内的运动数据
     */
    private void getDayMotionSummary() {
        if (!mPostEnable) {
            return;
        }
        mPostEnable = false;
        x.task().post(new Runnable() {
            @Override
            public void run() {
                String lFromDay = TimeU.getDayByDelay(FROM_DAY_DELAY);
                RequestParams params = RequestParamsBuilder.buildGetDayMotionSummaryRP(getActivity(), UrlConfig.URL_GET_DAY_MOTION_SUMMARY,
                        lFromDay, null, LocalApplication.getInstance().mLoginUser.userId);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (mPostHandler != null) {
                            if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                                Message msg = new Message();
                                msg.what = MSG_POST_OK;
                                msg.obj = result.result;
                                mPostHandler.sendMessage(msg);
                            } else {
                                Message msg = new Message();
                                msg.what = MSG_POST_ERROR;
                                msg.obj = result.errormsg;
                                mPostHandler.sendMessage(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mPostHandler != null) {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                            mPostHandler.sendMessage(msg);
                        }
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
     * 更新心率区间文本
     */
    private void updateHrView() {
        mHrTargetHighTv.setText(LocalApplication.getInstance().getLoginUser(getActivity()).targetHrHigh + "");
        mHrTargetLowTv.setText(LocalApplication.getInstance().getLoginUser(getActivity()).targetHrLow + "");
        mHrWarningTv.setText(LocalApplication.getInstance().getLoginUser(getActivity()).alertHr + "");
    }

    /**
     * 更新手表页面
     */
    private void updateWatchView() {
        //若手表未绑定
        if (LocalApplication.getInstance().getLoginUser(getActivity()).bleMac == null
                || LocalApplication.getInstance().getLoginUser(getActivity()).bleMac.equals("")) {
            mCurrHrTv.setText("- -");
            mWatchV.setVisibility(View.GONE);
            mWatchBatteryV.setVisibility(View.GONE);
            mLowBatteryLl.setVisibility(View.GONE);
            mBindBtn.setVisibility(View.VISIBLE);
            mWatchConnectingV.setVisibility(View.GONE);
            mCurrHrLl.setVisibility(View.GONE);
            mSyncLl.setVisibility(View.GONE);
            mSyncProLl.setVisibility(View.GONE);
            return;
        }
        mWatchV.setVisibility(View.VISIBLE);

        //若设备未连接
        if (BleManager.getBleManager().mBleConnectE == null
                || !BleManager.getBleManager().mBleConnectE.mIsConnected) {
            mCurrHrTv.setText("- -");
            mCurrHrLl.setVisibility(View.GONE);
            mWatchBatteryV.setVisibility(View.GONE);
            mLowBatteryLl.setVisibility(View.GONE);
            mBindBtn.setVisibility(View.GONE);
            mWatchConnectingV.setVisibility(View.VISIBLE);
            mSyncLl.setVisibility(View.GONE);
            mSyncProLl.setVisibility(View.GONE);
            return;
        }
        mWatchConnectingV.setVisibility(View.GONE);
        mBindBtn.setVisibility(View.GONE);
        mWatchBatteryV.setVisibility(View.VISIBLE);
        //显示电量左上角小图标
        //T.showShort(getActivity(),"电量：" + BleManager.getBleManager().mBleConnectE.mCurrBattery);
        if (BleManager.getBleManager().mBleConnectE.mCurrBattery != -1) {
            if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 20) {
                mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_low);
            } else if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 60) {
                mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_middle);
            } else {
                mWatchBatteryV.setBackgroundResource(R.drawable.ic_battery_high);
            }
        }
        //若正在同步
        if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
            if (!mSyncAnimStarting) {
                mSyncRotateAnim.start();
                mSyncAnimV.setAnimation(mSyncRotateAnim);
                mSyncAnimStarting = true;
            }
            mSyncLl.setVisibility(View.VISIBLE);
            mSyncProLl.setVisibility(View.VISIBLE);
            mCurrHrLl.setVisibility(View.GONE);
            mLowBatteryLl.setVisibility(View.GONE);
            int percent = 10;
            if (BleManager.getBleManager().mBleConnectE.mTotalSyncCount == 0) {
                percent = 90;
            } else {
                percent = 10 + (BleManager.getBleManager().mBleConnectE.mNeedSyncCount * 90 / BleManager.getBleManager().mBleConnectE.mTotalSyncCount);
            }
            mSyncTimeTv.setText("预计 " + TimeU.formatChineseDuration(14 * BleManager.getBleManager().mBleConnectE.mNeedSyncCount) + "后完成");
            mClipDrawable.setLevel(((100 - percent) * 10000 / 100));
            return;
        }
        mSyncLl.setVisibility(View.GONE);
        mSyncProLl.setVisibility(View.GONE);
        mSyncLl.setVisibility(View.GONE);
        mSyncRotateAnim.cancel();
        mSyncAnimStarting = false;
        //若是低电量
        if (BleManager.getBleManager().mBleConnectE.mCurrBattery < 20) {
            mLowBatteryLl.setVisibility(View.VISIBLE);
            mCurrHrLl.setVisibility(View.GONE);
        } else {
            mLowBatteryLl.setVisibility(View.GONE);
            mCurrHrLl.setVisibility(View.VISIBLE);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        myReceiver = new ConnectionChangeReceiver();
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
            if (bluetoothState && networkState){
                mErrorTipTv.setVisibility(View.GONE);
                return;
            }
            if (!networkState){
                mErrorTipTv.setVisibility(View.VISIBLE);
                mErrorTipTv.setText("无法连接网络");
                return;
            }

            if (!bluetoothState){
                mErrorTipTv.setVisibility(View.VISIBLE);
                mErrorTipTv.setText("蓝牙已关闭");
                updateWatchView();
                return;
            }
        }
    }
}
