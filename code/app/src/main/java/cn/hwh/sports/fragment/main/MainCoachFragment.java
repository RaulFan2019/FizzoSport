package cn.hwh.sports.fragment.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
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
import cn.hwh.sports.activity.main.MainActivity;
import cn.hwh.sports.activity.student.StudentManagerActivity;
import cn.hwh.sports.activity.student.TeachSelectStudentListActivity;

import cn.hwh.sports.activity.student.StudentReportActivity;
import cn.hwh.sports.adapter.MainCoachChartAdapter;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.adapter.MainCoachChartAE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetMoverDayCountRE;
import cn.hwh.sports.entity.net.GetMoverDayEffortRE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.indicator.CirclePageIndicator;
import cn.hwh.sports.utils.NetworkU;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by Administrator on 2016/11/29.
 */

public class MainCoachFragment extends BaseFragment {
    /* contains */
    private static final String TAG = "MainCoachFragment";

    private static final int MSG_UPDATE_MOVER_CHART_VIEW = 0x01;
    private static final int MSG_UPDATE_MOVER_ERROR = 0x02;
    private static final int MSG_HIDE_CHANGE_ROLE_TIP = 0x03;

    /* view */
    @BindView(R.id.vp_coach_chart)
    ViewPager mViewpager;
    @BindView(R.id.indicator)
    CirclePageIndicator mIndicator;
    @BindView(R.id.v_syn)
    View mSynV;
    @BindView(R.id.tv_sync_state)
    TextView mSyncStateTv;
    @BindView(R.id.tv_sync_date)
    TextView mSyncDateTv;
    @BindView(R.id.tv_show_role_change_tip)
    TextView mChangeRoleTipTv;
    @BindView(R.id.tv_error)
    TextView mErrorTipTv;

    private MainCoachChartAdapter mPgAdapter;
    private GetMoverDayEffortRE mDayEffortRe;
    private GetMoverDayCountRE mDayCountRe;

    /* data */
    private List<MainCoachChartAE> mCoachChartAEList = new ArrayList<>();
    private int mUserId;
    private RotateAnimation mRotateAnim;
    private boolean mPostAble = true;

    private ConnectionChangeReceiver myReceiver;

    public static MainCoachFragment newInstance() {
        MainCoachFragment fragment = new MainCoachFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mian_coach;
    }

    @OnClick({R.id.ll_student_report, R.id.ll_student_manage, R.id.ll_personal_teach,
            R.id.tv_change_personal, R.id.ll_sync})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_student_report:
                startActivity(StudentReportActivity.class);
                break;
            case R.id.ll_student_manage:
                startActivity(StudentManagerActivity.class);
                break;
            case R.id.ll_personal_teach:
                startActivity(TeachSelectStudentListActivity.class);
                break;
            case R.id.tv_change_personal:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setTabSelection(MainActivity.TAB_HEALTH);
                break;
            //点击同步
            case R.id.ll_sync:
                postGetMoverDayCount();
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新学员Chart界面
                case MSG_UPDATE_MOVER_CHART_VIEW:
                    parseMoverChartData();
                    mPgAdapter = new MainCoachChartAdapter(getActivity(), mCoachChartAEList);
                    mViewpager.setAdapter(mPgAdapter);
                    mRotateAnim.cancel();
                    mSynV.clearAnimation();
                    mSyncStateTv.setText("同步于 ");
                    mSyncDateTv.setVisibility(View.VISIBLE);
                    mSyncDateTv.setText(TimeU.nowTime(TimeU.FORMAT_TYPE_9));
                    mPostAble = true;
                    break;
                //更新学员Chart界面 错误
                case MSG_UPDATE_MOVER_ERROR:
                    mSyncStateTv.setText("同步失败");
                    mSyncDateTv.setVisibility(View.GONE);
                    mRotateAnim.cancel();
                    mSynV.clearAnimation();
                    mPostAble = true;
                    break;
                //切换学员教练提示信息消失
                case MSG_HIDE_CHANGE_ROLE_TIP:
                    mChangeRoleTipTv.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void initParams() {
        mUserId = LocalApplication.getInstance().getLoginUser(getActivity()).userId;
        mRotateAnim = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.rotating);

        mPgAdapter = new MainCoachChartAdapter(getActivity(), mCoachChartAEList);
        mViewpager.setAdapter(mPgAdapter);
        mViewpager.setOffscreenPageLimit(0);
        mIndicator.setViewPager(mViewpager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (mPrograms.get(position).programId == -2) {
//                    mStartBtn.setBackgroundResource(R.drawable.bg_round_rect_pressed);
//                    mStartBtn.setClickable(false);
//                } else if (mHrDeviceConnected) {
//                    mStartBtn.setBackgroundResource(R.drawable.bg_round_rect);
//                    mStartBtn.setClickable(true);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (UserSPData.getShowRoleChangeTip(getActivity())) {
            mChangeRoleTipTv.setVisibility(View.VISIBLE);
            UserSPData.setShowRoleChangeTip(getActivity(), false);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_CHANGE_ROLE_TIP, 4000);
        } else {
            mChangeRoleTipTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void causeGC() {
        if (mCoachChartAEList != null) {
            mCoachChartAEList.clear();
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_UPDATE_MOVER_CHART_VIEW);
        }
    }

    @Override
    protected void onVisible() {
        UserSPData.setUserPage(getActivity(), AppEnum.UserRoles.COACH);
        postGetMoverDayCount();
        registerReceiver();
    }

    @Override
    protected void onInVisible() {
        unregisterReceiver();
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
     * 获取学员数量变化
     */
    private void postGetMoverDayCount() {
        if (!mPostAble) {
            return;
        }
        mSyncStateTv.setText("数据同步中..");
        mSyncDateTv.setVisibility(View.GONE);
        mRotateAnim.start();
        mSynV.setAnimation(mRotateAnim);

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetMoverDayCount(getActivity(), UrlConfig.URL_GET_MOVER_DAY_COUNT, mUserId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mDayCountRe = JSON.parseObject(result.result, GetMoverDayCountRE.class);
                            postGetMoverDayEffort();
                        } else {
                            mHandler.sendEmptyMessage(MSG_UPDATE_MOVER_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mHandler.sendEmptyMessage(MSG_UPDATE_MOVER_ERROR);
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
     * 获取学员锻炼点数变化
     */
    private void postGetMoverDayEffort() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetMoverDayEffort(getActivity(), UrlConfig.URL_GET_MOVER_DAY_EFFORT, mUserId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mDayEffortRe = JSON.parseObject(result.result, GetMoverDayEffortRE.class);
                            mHandler.sendEmptyMessage(MSG_UPDATE_MOVER_CHART_VIEW);
                        } else {
                            mHandler.sendEmptyMessage(MSG_UPDATE_MOVER_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mHandler.sendEmptyMessage(MSG_UPDATE_MOVER_ERROR);
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
     * 解析MoverChartData
     */
    private void parseMoverChartData() {
        mCoachChartAEList.clear();
        List<MainCoachChartAE.ColumnDay> dayCountList = new ArrayList<>();
        int maxCount = 0;
        for (GetMoverDayCountRE.DaysEntity entity : mDayCountRe.days) {
            MainCoachChartAE.ColumnDay day = new MainCoachChartAE.ColumnDay(TimeU.intToWeekChinese(entity.weekday), entity.movercount);
            dayCountList.add(day);
            if (maxCount < entity.movercount) {
                maxCount = entity.movercount;
            }
        }
        MainCoachChartAE countAe = new MainCoachChartAE("训练人数", maxCount, dayCountList);
        mCoachChartAEList.add(countAe);

        List<MainCoachChartAE.ColumnDay> dayEffortList = new ArrayList<>();
        int maxEffort = 0;
        for (GetMoverDayEffortRE.DaysEntity entity : mDayEffortRe.days) {
            MainCoachChartAE.ColumnDay day = new MainCoachChartAE.ColumnDay(TimeU.intToWeekChinese(entity.weekday), entity.effort_point);
            dayEffortList.add(day);
            if (maxEffort < entity.effort_point) {
                maxEffort = entity.effort_point;
            }
        }
        MainCoachChartAE effortAe = new MainCoachChartAE("锻炼点数", maxEffort, dayEffortList);
        mCoachChartAEList.add(effortAe);
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
                return;
            }
        }
    }
}
