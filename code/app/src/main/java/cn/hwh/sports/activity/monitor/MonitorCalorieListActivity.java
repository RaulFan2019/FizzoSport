package cn.hwh.sports.activity.monitor;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.settings.EventTargetSetActivity;
import cn.hwh.sports.adapter.MonitorListAdapter;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.DayMotionSummaryDBData;
import cn.hwh.sports.entity.adapter.MonitorListAE;
import cn.hwh.sports.entity.db.DayMotionSummaryDE;
import cn.hwh.sports.entity.model.MonitorListHeadME;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.DayMotionSummaryRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.MonitorListHeadView;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.ui.pulltorefreshlib.PullToRefreshLayout;
import cn.hwh.sports.ui.pulltorefreshlib.PullablePinnedSectionListView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/12/13.
 * 卡路里列表详情页面
 */
public class MonitorCalorieListActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "MonitorCalorieListActivity";

    private static final int MSG_POST_ERROR = 0x00;
    private static final int MSG_POST_OK = 0x01;

    /* view */
    @BindView(R.id.lv_calorie)
    PullablePinnedSectionListView mCalorieLv;
    @BindView(R.id.ptr_view)
    PullToRefreshLayout mPtrView;
    @BindView(R.id.v_loading)
    MyLoadingView mLoadingView;

    MonitorListHeadView mHeadView;
    MonitorListHeadME mListHeadME;

    /* state data */
    private int mUserId;//用户id
    private String mCurrLoadingDay;//yyyy-MM-dd 格式时间 当前已加载的日期
    private boolean mFirstIn = true;

    /* network data */
    private boolean mPostAble = true;//是否允许加载
    private Callback.Cancelable mPostCancelable;

    /* list data */
    private int mSectionPosition = 0;
    private int mListPosition = 0;
    private MonitorListAdapter mAdapter;
    private List<MonitorListAE> mMonitorData = new ArrayList<>();

    private boolean mHasShowChart = false;
    private boolean mIsFirstIn = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_monitor_calorie_list;
    }

    @OnClick({R.id.btn_back, R.id.btn_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_setting:
                startActivity(EventTargetSetActivity.class);
                break;
        }
    }

    Handler mPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostAble = true;
            switch (msg.what) {
                case MSG_POST_ERROR:
                    T.showShort(MonitorCalorieListActivity.this, msg.obj.toString());
                    if (!mFirstIn) {
                        mPtrView.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                    break;
                case MSG_POST_OK:
                    //获取数据成功后，先存入本地数据库中
                    DayMotionSummaryRE entity = JSON.parseObject(msg.obj.toString(), DayMotionSummaryRE.class);
                    saveToDb(entity);
                    break;
            }
        }
    };


    @Override
    protected void initData() {
        mUserId = LocalApplication.getInstance().getLoginUser(MonitorCalorieListActivity.this).userId;
        mCurrLoadingDay = TimeU.nowDay();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAdapter = new MonitorListAdapter(MonitorCalorieListActivity.this, mMonitorData, MonitorListAdapter.VALUE_TYPE_INT);
        mCalorieLv.setAdapter(mAdapter);

        mPtrView.getChildAt(0).setVisibility(View.GONE);
        mPtrView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
//                doRefresh();
                mPtrView.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            //上拉加载
            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                getLastWeekData();
            }
        });
    }

    @Override
    protected void doMyCreate() {
        getChartData();//获取图表数据，并绘制图表
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasShowChart && !mIsFirstIn){
            mListHeadME.targetValue = LocalApplication.getInstance().getLoginUser(MonitorCalorieListActivity.this).targetCalorie;
            mHeadView.bindHeadView(mListHeadME);
        }
        if (mIsFirstIn){
            mIsFirstIn = false;
        }
    }

    @Override
    protected void causeGC() {
        if (mPostCancelable != null) {
            mPostCancelable.cancel();
        }
        if (mPostHandler != null) {
            mPostHandler.removeMessages(MSG_POST_ERROR);
            mPostHandler.removeMessages(MSG_POST_OK);
        }
    }

    /**
     * 获取图表数据
     */
    private void getChartData() {
        List<DayMotionSummaryDE> lWeekDayList = DayMotionSummaryDBData.getDayMotionSummary(mUserId, TimeU.nowDay(), TimeU.getDayByDelay(7), false);
        if (lWeekDayList == null || lWeekDayList.size() == 0) {
            getLastWeekData();
            return;
        }

        List<MonitorListHeadME.MonitorColumnDay> mDays = new ArrayList<>();
        //本周最大步数
        int lMaxCalorie = 0;
        //计算总步数，并补全一周
        for (int i = 0; i < 7; i++) {
            if (lWeekDayList.size() < (i + 1)) {
                mDays.add(new MonitorListHeadME.MonitorColumnDay(TimeU.intToWeekChinese((lWeekDayList.get(lWeekDayList.size() - 1).weekday - lWeekDayList.size() + i) % 7), 0));
            } else {
                mDays.add(new MonitorListHeadME.MonitorColumnDay(TimeU.intToWeekChinese(lWeekDayList.get(i).weekday), lWeekDayList.get(i).calorie));
                //lWeekDayList.get(i).stepcount)
                if (lMaxCalorie < lWeekDayList.get(i).calorie) {
                    lMaxCalorie = lWeekDayList.get(i).calorie;
                }
            }
        }
        mListHeadME = new MonitorListHeadME(MonitorListHeadME.TYPE_CALORIE, lMaxCalorie,
                LocalApplication.getInstance().getLoginUser(MonitorCalorieListActivity.this).targetCalorie, mDays);
        mHeadView = new MonitorListHeadView(MonitorCalorieListActivity.this);
        mHeadView.bindHeadView(mListHeadME);
        mCalorieLv.addHeaderView(mHeadView);
        mHasShowChart = true;
        getCurrWeekData();
    }


    /**
     * 获取本周的数据
     */
    public void getCurrWeekData() {
        //获取本周数据
        int lWeekCalorieSum = 0;
        int delay = TimeU.getWeekDay(TimeU.nowDay());
        List<DayMotionSummaryDE> lWeekDayDEList = DayMotionSummaryDBData.getDayMotionSummary(mUserId, TimeU.nowDay(), TimeU.getDayByDelay(delay), true);
        List<MonitorListAE> lWeekList = new ArrayList<>();
        // 遍历本周数据，并计算步数总和
        for (int i = 0; i < lWeekDayDEList.size(); i++) {
            DayMotionSummaryDE summaryDE = lWeekDayDEList.get(i);
            MonitorListAE monitorListAE = new MonitorListAE(MonitorListAE.ITEM, mSectionPosition, mListPosition, 0,
                    "", "千卡", summaryDE.calorie, summaryDE.target_calorie, summaryDE.date,"消耗");
            mListPosition++;
            lWeekList.add(monitorListAE);
            //计算步数和
            lWeekCalorieSum += summaryDE.calorie;
        }
        //增加头信息，增加位置记数
        MonitorListAE monitorSectionAE = new MonitorListAE(MonitorListAE.SECTION, mSectionPosition, mListPosition, lWeekCalorieSum,
                TimeU.formatTimeScope(mCurrLoadingDay, lWeekDayDEList.get(lWeekDayDEList.size() - 1).date), "千卡", 0, 0, "","消耗");

        mMonitorData.add(monitorSectionAE);
        mMonitorData.addAll(lWeekList);
        mSectionPosition++;
        mListPosition++;
        mCurrLoadingDay = TimeU.getDayAtFromDay(mCurrLoadingDay, lWeekDayDEList.size());
        getLastWeekData();
    }

    /**
     * 获取先前星期数据
     */
    public void getLastWeekData() {
        String lEndTime = TimeU.getDayAtFromDay(mCurrLoadingDay, 22);
        List<DayMotionSummaryDE> lLastWeekDayList = DayMotionSummaryDBData.getDayMotionSummary(mUserId, mCurrLoadingDay, lEndTime, true);
        if (lLastWeekDayList == null || lLastWeekDayList.size() < 8) {
            getDayMotionSummary(30, mCurrLoadingDay);
            return;
        }
        List<MonitorListAE> lWeekList = new ArrayList<>();
        int lWeekCalorieSum = 0;
        for (int i = 0; i < lLastWeekDayList.size(); i++) {
            if (i % 7 == 0 && i != 0) {
                //头信息
                MonitorListAE monitorSectionAE = new MonitorListAE(MonitorListAE.SECTION, mSectionPosition, mListPosition, lWeekCalorieSum,
                        TimeU.formatTimeScope(mCurrLoadingDay, lLastWeekDayList.get(i - 1).date), "千卡", 0, 0, "","消耗");
                mMonitorData.add(monitorSectionAE);
                mMonitorData.addAll(lWeekList);
                mSectionPosition++;
                mListPosition++;
                mCurrLoadingDay = lLastWeekDayList.get(i).date;
                lWeekList.clear();
                lWeekCalorieSum = 0;
            }
            DayMotionSummaryDE summaryDE = lLastWeekDayList.get(i);
            MonitorListAE monitorListAE = new MonitorListAE(MonitorListAE.ITEM, mSectionPosition, mListPosition, 0,
                    "", "千卡", summaryDE.calorie, summaryDE.target_calorie, summaryDE.date,"消耗");
            mListPosition++;
            lWeekList.add(monitorListAE);
            //计算卡路里和
            lWeekCalorieSum += summaryDE.calorie;
        }
        mAdapter.notifyDataSetChanged();
        if (mFirstIn) {
            mLoadingView.loadFinish();
            mFirstIn = false;
        } else {
            mPtrView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
    }


    /**
     * 获取一段时间内的运动数据
     */
    private void getDayMotionSummary(final int delay, final String toDay) {
        if (!mPostAble) {
            return;
        }
        mPostAble = false;

        x.task().post(new Runnable() {
            @Override
            public void run() {
                String lFromDay = TimeU.getDayAtFromDay(toDay, delay);
                RequestParams params = RequestParamsBuilder.buildGetDayMotionSummaryRP(MonitorCalorieListActivity.this,
                        UrlConfig.URL_GET_DAY_MOTION_SUMMARY, lFromDay, toDay, mUserId);
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
                        mPostAble = true;
                        if (mPostHandler != null) {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = "取消加载";
                            mPostHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 保存数据至数据库
     */
    private void saveToDb(final DayMotionSummaryRE entity) {
        int lUserId = LocalApplication.getInstance().mLoginUser.userId;
        List<DayMotionSummaryDE> mDayDEList = new ArrayList<>();
        for (int i = 0, size = entity.days.size(); i < size; i++) {
            DayMotionSummaryRE.DaysBean summaryRE = entity.days.get(i);
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

        //开始获取数据
        if (mFirstIn) {
            getChartData();
        } else {
            getLastWeekData();
        }
    }

}
