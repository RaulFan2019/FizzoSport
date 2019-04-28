package cn.hwh.sports.activity.workout;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import cn.hwh.sports.activity.monitor.MonitorCalorieListActivity;
import cn.hwh.sports.activity.settings.SportTargetSetActivity;
import cn.hwh.sports.adapter.MonitorListAdapter;
import cn.hwh.sports.adapter.WorkoutListAdapter;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.adapter.MonitorListAE;
import cn.hwh.sports.entity.adapter.WorkoutListAE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetUserWorkoutListRE;
import cn.hwh.sports.entity.net.GetWeekExercisedDaysRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.ui.pulltorefreshlib.PullToRefreshLayout;
import cn.hwh.sports.ui.pulltorefreshlib.PullablePinnedSectionListView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;
import cn.hwh.sports.utils.TimeU;
import cn.hwh.sports.utils.WeekSportImageU;

/**
 * Created by Raul.Fan on 2016/12/12.
 * 项目列表
 */
public class WorkoutListActivity extends BaseActivity {

    private static final String TAG = "WorkoutListActivity";

    private static final int MSG_UPDATE_ERROR = 0;//更新失败
    private static final int MSG_UPDATE_LIST_VIEW = 1;//更新列表页面
    private static final int MSG_UPDATE_HEAD_VIEWS = 2;//更新头信息布局

    /* view */
    @BindView(R.id.rv_history)
    PullablePinnedSectionListView mHistoryLv;
    @BindView(R.id.ptr_view)
    PullToRefreshLayout mPtrView;
    @BindView(R.id.v_loading)
    MyLoadingView mLoadingView;

    /* network data */
    private boolean mPostAble = true;//是否允许加载
    private Callback.Cancelable mPostCancelable;
    private boolean mFreshError = false;

    /* list data */
    private int mSectionPosition = -1;
    private int mListPosition = -1;
    private String mWeekStartTime = "";
    private WorkoutListAdapter mAdapter;
    private List<WorkoutListAE> mWorkoutListData = new ArrayList<>();

    /* local status data */
    private int mUserId;//用户id
    private String mCurrLoadingDate;//yyyy-MM-dd 格式时间 当前已加载的日期
    private boolean mIsFirstIn = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_list;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新列表
                case MSG_UPDATE_LIST_VIEW:
                    mPostAble = true;
                    parseWorkoutList((GetUserWorkoutListRE) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    if (mIsFirstIn) {
                        mIsFirstIn = false;
                        mLoadingView.loadFinish();
                    } else {
                        mPtrView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                    break;
                //更新错误
                case MSG_UPDATE_ERROR:
                    T.showShort(WorkoutListActivity.this, msg.obj.toString());
                    mPostAble = true;
                    if (mIsFirstIn) {
                        mFreshError = true;
                        mLoadingView.LoadError((String) msg.obj);
                    } else {
                        mPtrView.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                    break;
                //更新头布局
                case MSG_UPDATE_HEAD_VIEWS:
                    updateHeadViews((GetWeekExercisedDaysRE) msg.obj);
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back,R.id.btn_trend,R.id.v_loading})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_trend:
                startActivity(WorkoutTrendActivity.class);
                break;
            case R.id.v_loading:
                if (mFreshError){
                    postGetThisWeekTotal();
                }
                break;

        }
    }

    @Override
    protected void initData() {
        mUserId = LocalApplication.getInstance().getLoginUser(WorkoutListActivity.this).userId;
        mCurrLoadingDate = TimeU.nowTime();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAdapter = new WorkoutListAdapter(WorkoutListActivity.this, mWorkoutListData);
        mHistoryLv.setAdapter(mAdapter);
        mPtrView.getChildAt(0).setVisibility(View.GONE);
        mHistoryLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.v(TAG, "pos:" + i);
//                if (i == 0){
//                    return;
//                }
                WorkoutListAE ae = mWorkoutListData.get(i);
                if (ae.type == WorkoutListAE.ITEM_EFFORT) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("workoutId", ae.workoutEntity.id);
                    bundle.putString("workoutStartTime", ae.workoutEntity.starttime);
                    bundle.putInt("calorie",ae.workoutEntity.calorie);
                    bundle.putInt("resource",ae.workoutEntity.resource);
                    bundle.putInt("type", ae.workoutEntity.type);
                    startActivity(WorkoutIndoorActivity.class, bundle);
                }else if (ae.type == WorkoutListAE.ITEM_RUN){
                    Bundle bundle = new Bundle();
                    bundle.putInt("workoutId", ae.workoutEntity.id);
                    bundle.putString("workoutStartTime", ae.workoutEntity.starttime);
                    bundle.putInt("calorie",ae.workoutEntity.calorie);
                    startActivity(WorkoutRunInfoActivity.class, bundle);
                }else if (ae.type == WorkoutListAE.ITEM_RUNNING_INDOOR){
                    Bundle bundle = new Bundle();
                    bundle.putInt("workoutId", ae.workoutEntity.id);
                    bundle.putString("workoutStartTime", ae.workoutEntity.starttime);
                    bundle.putInt("calorie",ae.workoutEntity.calorie);
                    startActivity(WorkoutIndoorActivity.class, bundle);
                }
            }
        });

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
                if (mPostAble) {
                    loadMore();
                }
            }
        });
    }

    @Override
    protected void doMyCreate() {
//        postGetThisWeekTotal();
        postGetWorkoutList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void causeGC() {
        if (mPostCancelable != null) {
            mPostCancelable.cancel();
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_UPDATE_ERROR);
            mHandler.removeMessages(MSG_UPDATE_LIST_VIEW);
            mHandler.removeMessages(MSG_UPDATE_HEAD_VIEWS);
        }
        if (mWorkoutListData != null) {
            mWorkoutListData.clear();
        }
    }

    /**
     * 执行刷新
     */
    private void doRefresh() {
        postGetThisWeekTotal();
    }

    /**
     * 执行加载更多
     */
    private void loadMore() {
        postGetWorkoutList();
    }


    /**
     * 获取本周数据
     */
    private void postGetThisWeekTotal() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetWeekExercisedDays(WorkoutListActivity.this, UrlConfig.URL_GET_WEEK_EXERCISED_DAYS, mUserId);
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetWeekExercisedDaysRE exercisedDaysRE = JSON.parseObject(result.result, GetWeekExercisedDaysRE.class);
                            Message msg = new Message();
                            msg.what = MSG_UPDATE_HEAD_VIEWS;
                            msg.obj = exercisedDaysRE;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_UPDATE_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_UPDATE_ERROR;
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
     * 更新头信息页面
     */
    private void updateHeadViews(final GetWeekExercisedDaysRE exercisedDaysRE) {
        LinearLayout mHeadView = (LinearLayout) LayoutInflater.from(WorkoutListActivity.this)
                .inflate(R.layout.layout_workout_list_head, null, true);
        TextView mWeekDurationTv = (TextView) mHeadView.findViewById(R.id.tv_duration_this_week);
        View mTargetV = mHeadView.findViewById(R.id.v_workout_head_target);
        View mTargetBigV = mHeadView.findViewById(R.id.v_target_big);
        TextView mTipUpTv = (TextView) mHeadView.findViewById(R.id.tv_week_list_head_tip_1);
        TextView mTipBottomTv = (TextView) mHeadView.findViewById(R.id.tv_week_list_head_tip_2);

        mWeekDurationTv.setText(TimeU.formatChineseDurationByMin(exercisedDaysRE.week_exercised_minutes));
        mTargetV.setBackgroundResource(WeekSportImageU.getWeekSportImage(exercisedDaysRE.week_target_days,
                exercisedDaysRE.week_exercised_days));
        mTargetBigV.setBackgroundResource(WeekSportImageU.getWeekTargetImage(exercisedDaysRE.week_target_days,
                exercisedDaysRE.week_exercised_days));
        if (exercisedDaysRE.week_exercised_days > 0) {
            mTipUpTv.setText(exercisedDaysRE.week_target_days + "天中的第" + exercisedDaysRE.week_exercised_days + "天");
            mTipBottomTv.setText("第" + exercisedDaysRE.week_exercised_days + "天圆满完成。干的漂亮！");
        } else {
            mTipUpTv.setText("本周还没有锻炼记录。");
            mTipBottomTv.setText("戴上手环，开始你的健康生活！");
        }
        mHistoryLv.addHeaderView(mHeadView);
        postGetWorkoutList();
    }

    /**
     * 获取个人锻炼记录列表
     */
    private void postGetWorkoutList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetWorkoutListRP(WorkoutListActivity.this,
                        UrlConfig.URL_GET_WORKOUT_LIST, mUserId, mCurrLoadingDate, "0,10");
                mPostCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetUserWorkoutListRE workoutListRE = JSON.parseObject(result.result, GetUserWorkoutListRE.class);
                            Message msg = new Message();
                            msg.obj = workoutListRE;
                            msg.what = MSG_UPDATE_LIST_VIEW;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_UPDATE_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_UPDATE_ERROR;
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
     * 解析获取的历史列表
     */
    private void parseWorkoutList(final GetUserWorkoutListRE workoutListRE) {
        //若空记录
        if (workoutListRE == null || workoutListRE.workout == null || workoutListRE.workout.size() == 0) {
            return;
        }
        List<WorkoutListAE> mTempWorkoutList = new ArrayList<>();
        for (GetUserWorkoutListRE.WorkoutEntity workoutEntity : workoutListRE.workout) {
            if (!workoutEntity.week_starttime.equals(mWeekStartTime)) {
                mSectionPosition++;
                mListPosition++;
                mWeekStartTime = workoutEntity.week_starttime;
                WorkoutListAE workoutListAE = new WorkoutListAE(WorkoutListAE.SECTION, mSectionPosition, mListPosition,
                        workoutEntity.week_starttime, workoutEntity.week_exercised_minutes, null);
                mTempWorkoutList.add(workoutListAE);
            }
            mListPosition++;
            if (workoutEntity.type == SportEnum.EffortType.RUNNING_OUTDOOR) {
                WorkoutListAE workoutListAE = new WorkoutListAE(WorkoutListAE.ITEM_RUN, mSectionPosition, mListPosition,
                        workoutEntity.week_starttime, workoutEntity.week_exercised_minutes, workoutEntity);
                mTempWorkoutList.add(workoutListAE);
            } else if (workoutEntity.type == SportEnum.EffortType.RUNNING_INDOOR){
                WorkoutListAE workoutListAE = new WorkoutListAE(WorkoutListAE.ITEM_RUNNING_INDOOR, mSectionPosition, mListPosition,
                        workoutEntity.week_starttime, workoutEntity.week_exercised_minutes, workoutEntity);
                mTempWorkoutList.add(workoutListAE);
            }else {
                WorkoutListAE workoutListAE = new WorkoutListAE(WorkoutListAE.ITEM_EFFORT, mSectionPosition, mListPosition,
                        workoutEntity.week_starttime, workoutEntity.week_exercised_minutes, workoutEntity);
                mTempWorkoutList.add(workoutListAE);
            }
            mCurrLoadingDate = workoutEntity.starttime;
        }
        mWorkoutListData.addAll(mTempWorkoutList);
    }
}
