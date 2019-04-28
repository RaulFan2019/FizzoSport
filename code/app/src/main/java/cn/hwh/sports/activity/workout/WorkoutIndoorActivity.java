package cn.hwh.sports.activity.workout;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.rey.material.widget.TabPageIndicator;
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
import butterknife.OnClick;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.WorkoutIndoorPagerAdapter;
import cn.hwh.sports.adapter.WorkoutRunningOutdoorPagerAdapter;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.db.WorkoutSyncDBData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.event.WorkoutEndEE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetWorkoutDetailInfoRE;
import cn.hwh.sports.entity.net.GetWorkoutInfoRE;
import cn.hwh.sports.entity.net.GetWxShareWorkoutTextRE;
import cn.hwh.sports.fragment.workout.WorkoutIndoorChartFragment;
import cn.hwh.sports.fragment.workout.WorkoutIndoorHrFragment;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.HttpExceptionHelper;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.CustomViewPager;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.MyLoadingView;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.T;

/**
 * Created by machenike on 2017/6/12 0012.
 */

public class WorkoutIndoorActivity extends BaseActivity {

    /* contains */
    private static final int SHARE_WE_CHAT_C = 0x01;
    private static final int SHARE_WE_CHAT_MOMENT = 0x02;

    private static final int MSG_POST_ERROR = 0;
    private static final int MSG_POST_OK = 1;
    private static final int MSG_GET_SHARE_OK = 2;
    private static final int MSG_GET_SHARE_ERROR = 3;

    /* local view */
    @BindView(R.id.tv_effort_name)
    TextView tvEffortName;
    @BindView(R.id.tv_effort_start_time)
    TextView tvEffortStartTime;

    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.btn_syn)
    ImageButton btnSyn;

    @BindView(R.id.tbi_history)
    TabPageIndicator tbiHistory;
    @BindView(R.id.vp_history)
    CustomViewPager vpHistory;

    @BindView(R.id.v_loading)
    MyLoadingView vLoading;
    @BindView(R.id.v_edit)
    View vEdit;

    /* local data */
    private int mWorkoutId;//记录ID
    private String mWorkoutStartTime;//记录开始时间
    private int mCalorie;//上个页面传过来的卡路里
    private List<TimeSplitDE> mTimeSplits = new ArrayList<>();

    private UserDE mUserDe;//用户信息
    private WorkoutDE mWorkoutDE;//锻炼记录信息

    DialogBuilder mDialogBuilder;

    //UI
    WorkoutIndoorHrFragment fragmentHr;
    WorkoutIndoorChartFragment fragmentChart;

    private WorkoutIndoorPagerAdapter.Tab[] mItems;
    private WorkoutIndoorPagerAdapter mPagerAdapter;//Tab适配器
    private FragmentManager mFragMgr;
    private int mLastFragment = 1;//跳转前页面显示位置

    /* network data */
    private Callback.Cancelable mCancelable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_indoor;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取失败
                case MSG_POST_ERROR:
                    vLoading.LoadError((String) msg.obj);
                    Toasty.error(WorkoutIndoorActivity.this, (String) msg.obj).show();
                    break;
                //获取成功
                case MSG_POST_OK:
                    updateWorkoutViews();
                    vLoading.loadFinish();
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
                    web.setThumb(new UMImage(WorkoutIndoorActivity.this, shareEntity.image));
                    new ShareAction(WorkoutIndoorActivity.this)
                            .withMedia(web)
                            .setPlatform(media)
                            .setCallback(umShareListener).share();

                    break;
                //获取分享失败
                case MSG_GET_SHARE_ERROR:
                    Toasty.error(WorkoutIndoorActivity.this, (String) msg.obj).show();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_share, R.id.btn_syn,R.id.ll_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_share:
                showShareWindow();
                break;
            case R.id.btn_syn:
                Toasty.info(WorkoutIndoorActivity.this, "正在同步..").show();
                break;
            //点击头部布局
            case R.id.ll_title:
                if (mWorkoutDE != null){
                    Bundle bundle = new Bundle();
                    bundle.putInt("id",mWorkoutId);
                    bundle.putString("name",mWorkoutDE.name);
                    bundle.putString("startTime",mWorkoutStartTime);
                    startActivity(WorkoutRenameActivity.class,bundle);
                }
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

        mUserDe = LocalApplication.getInstance().getLoginUser(WorkoutIndoorActivity.this);

        if (mWorkoutId == 0) {
            mWorkoutId = (int) WorkoutSyncDBData.getWorkoutServiceId(mWorkoutStartTime, mUserDe.userId);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mDialogBuilder = new DialogBuilder();
//        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tvEffortStartTime.setText(mWorkoutStartTime);

        fragmentHr = WorkoutIndoorHrFragment.newInstance();
        fragmentChart = WorkoutIndoorChartFragment.newInstance();

        /* TAB */
        mItems = new WorkoutIndoorPagerAdapter.Tab[]{WorkoutIndoorPagerAdapter.Tab.HEARTRATE, WorkoutIndoorPagerAdapter.Tab.CHART};
        mPagerAdapter = new WorkoutIndoorPagerAdapter(getSupportFragmentManager(), mItems,
                new Fragment[]{fragmentHr, fragmentChart});
        vpHistory.setAdapter(mPagerAdapter);
        vpHistory.setOffscreenPageLimit(4);

        tbiHistory.setViewPager(vpHistory);
        tbiHistory.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
//                imm.hideSoftInputFromWindow(btnSyn.getWindowToken(), 0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        vpHistory.setCurrentItem(0);
    }

    @Override
    protected void doMyCreate() {
        EventBus.getDefault().register(this);
        //查看数据库中是否存在
        mWorkoutDE = WorkoutDBData.getWorkoutByStartTime(mUserDe.userId, mWorkoutStartTime);
        //若记录为空, 从网络获取
        if (mWorkoutDE == null) {
            postGetWorkoutInfo();
        } else {
            if (mCalorie != 0) {
                mWorkoutDE.calorie = mCalorie;
            }
            WorkoutDBData.update(mWorkoutDE);
            mHandler.sendEmptyMessageDelayed(MSG_POST_OK,1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSyncView();
        if (mWorkoutDE != null){
            mWorkoutDE = WorkoutDBData.getWorkoutByStartTime(mUserDe.userId,mWorkoutStartTime);
            tvEffortName.setText(mWorkoutDE.name);
        }
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mCancelable != null){
            mCancelable.cancel();
        }
    }

    private void updateSyncView(){
        if (UploadDBData.getUploadDEByWorkoutStartTime(mWorkoutStartTime) != null) {
            btnShare.setVisibility(View.GONE);
            btnSyn.setVisibility(View.VISIBLE);
        } else {
            btnSyn.setVisibility(View.GONE);
            if (mWorkoutDE != null) {
                if (mWorkoutDE.ownerId == mUserDe.userId) {
                    btnShare.setVisibility(View.VISIBLE);
                } else {
                    btnShare.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 开启分享窗口
     */
    private void showShareWindow() {
        mDialogBuilder.showShareDialog(WorkoutIndoorActivity.this);
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            T.showShort(WorkoutIndoorActivity.this, "分享成功啦");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            T.showShort(WorkoutIndoorActivity.this, "分享失败啦");
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            T.showShort(WorkoutIndoorActivity.this, "分享取消了");
        }
    };

    /**
     * 获取分享信息
     *
     * @param shareType
     */
    private void getShareMsg(final int shareType) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetWeixinShareWorkoutText(WorkoutIndoorActivity.this,
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

    /**
     * 获取workout 信息
     */
    private void postGetWorkoutInfo(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildGetWorkoutInfoRP(WorkoutIndoorActivity.this,
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
     * 更新workout页面
     */
    private void updateWorkoutViews(){
        tvEffortName.setText(mWorkoutDE.name);
        fragmentHr.updateViewByActivity(mWorkoutStartTime);
        fragmentChart.updateViewByActivity(mWorkoutStartTime);
        vEdit.setVisibility(View.VISIBLE);
    }


    /**
     * 保存数据到数据库
     *
     * @param re
     */
    private void saveWorkoutInfoToDB(final GetWorkoutDetailInfoRE re) {
        mWorkoutDE = new WorkoutDE(re.id, re.name, re.starttime, re.duration, 0,
                re.calorie, 0, 0, 0, 0, 0, re.avgheartrate, re.maxheartrate,
                re.minheartrate, mUserDe.userId, mUserDe.userId, 0, re.stepcount,
                SportEnum.SportStatus.FINISH, SportEnum.EffortType.FREE_INDOOR, re.effort_point,
                re.planned_goal, re.planned_duration, 3);
        WorkoutDBData.saveWorkout(mWorkoutDE);

        long nowTime = System.currentTimeMillis();
        for (int i = 0, size = re.splits.size(); i < size; i++) {
            GetWorkoutDetailInfoRE.SplitsEntity split = re.splits.get(i);
            TimeSplitDE timeDe = new TimeSplitDE(nowTime + i, re.starttime, split.timeoffset / 60,
                    split.avgheartrate, SportEnum.SportStatus.FINISH, split.duration,
                    mUserDe.userId, mUserDe.userId);
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

}
