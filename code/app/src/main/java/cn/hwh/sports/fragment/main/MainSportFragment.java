package cn.hwh.sports.fragment.main;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.fizzo.baseutilslib.anim.vp.RotateDownAndScalePageTransformer;
import cn.fizzo.baseutilslib.toast.Toasty;
import cn.hwh.sports.BleManager;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.settings.WorkOutSetActivity;
import cn.hwh.sports.activity.sporting.RunningIndoorActivityV2;
import cn.hwh.sports.activity.sporting.SportingIndoorActivity;
import cn.hwh.sports.activity.sporting.SportingRunningOutdoorActivity;
import cn.hwh.sports.activity.workout.WorkoutCalendarActivity;
import cn.hwh.sports.adapter.SelectSportVpAdapter;
import cn.hwh.sports.ble.BleConnectEE;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.LengthSplitDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.db.WorkoutCalendarDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.db.TimeSplitDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutCalendarDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetCalendarRE;
import cn.hwh.sports.entity.net.UserInfoRE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2017/4/17.
 */

public class MainSportFragment extends BaseFragment implements AMapLocationListener {


    private static final String TAG = "MainSportFragment";

    private static final int MSG_UPDATE_VIEW = 0x01;
    private static final int MSG_UPDATE_WEEK_DAY_VIEW = 0x02;
    private static final int MSG_LOC_ERROR = 0x03;

    private static final long INTERVAL_LOC_ERROR = 5000;

    /* local view */
    @BindView(R.id.tv_state_hr)
    TextView mStateHrTv;//手表状态文本
    @BindView(R.id.v_sport_state_sound)
    View mSportStateSoundV;
    @BindView(R.id.vp_select_sport)
    ViewPager mSelectSportVp;


    @BindView(R.id.tv_week_1)
    TextView tvWeek1;
    @BindView(R.id.v_week_1)
    View vWeek1;
    @BindView(R.id.tv_week_2)
    TextView tvWeek2;
    @BindView(R.id.v_week_2)
    View vWeek2;
    @BindView(R.id.tv_week_3)
    TextView tvWeek3;
    @BindView(R.id.v_week_3)
    View vWeek3;
    @BindView(R.id.tv_week_4)
    TextView tvWeek4;
    @BindView(R.id.v_week_4)
    View vWeek4;
    @BindView(R.id.tv_week_5)
    TextView tvWeek5;
    @BindView(R.id.v_week_5)
    View vWeek5;
    @BindView(R.id.tv_week_6)
    TextView tvWeek6;
    @BindView(R.id.v_week_6)
    View vWeek6;
    @BindView(R.id.tv_week_7)
    TextView tvWeek7;
    @BindView(R.id.v_week_7)
    View vWeek7;

    @BindView(R.id.tv_week_name_1)
    TextView tvWeekName1;
    @BindView(R.id.tv_week_name_2)
    TextView tvWeekName2;
    @BindView(R.id.tv_week_name_3)
    TextView tvWeekName3;
    @BindView(R.id.tv_week_name_4)
    TextView tvWeekName4;
    @BindView(R.id.tv_week_name_5)
    TextView tvWeekName5;
    @BindView(R.id.tv_week_name_6)
    TextView tvWeekName6;
    @BindView(R.id.tv_week_name_7)
    TextView tvWeekName7;

    @BindView(R.id.v_gps_state)
    View vGpsState;
    @BindView(R.id.tv_gps_state)
    TextView tvGpsState;
    @BindView(R.id.ll_gps)
    LinearLayout llGps;

    /* local data */
    SelectSportVpAdapter mAdapter;
    private int mUserId;
    private List<TextView> mWeekTvs = new ArrayList<>();
    private List<View> mPointVs = new ArrayList<>();
    private List<TextView> mWeekNameTvs = new ArrayList<>();

    private DialogBuilder mDialogBuilder;

    /* 定位相关 */
    private int mSuccessCount;//定位成功数量
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private boolean mHasLocation = false;//是否已经定位成功


    int[] imgRes = {R.drawable.bg_select_sport_3,//无器械
            R.drawable.bg_select_sport_9,//小器械
            R.drawable.bg_select_sport_1,
            R.drawable.bg_select_sport_10,//跳绳
            R.drawable.bg_select_sport_4,//跑步机
            R.drawable.bg_select_sport_8,//划船机
            R.drawable.bg_select_sport_6,//动感单车
            R.drawable.bg_select_sport_7,//椭圆机
            R.drawable.bg_select_sport_5,//楼梯机
    };

    /* 构造函数 */
    public static MainSportFragment newInstance() {
        MainSportFragment fragment = new MainSportFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_sport;
    }

    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPDATE_VIEW) {
                updateSportView();
            } else if (msg.what == MSG_UPDATE_WEEK_DAY_VIEW) {
                updateSportView();
            }
        }
    };

    @OnClick({R.id.v_sport_state_sound, R.id.btn_start_sport, R.id.ll_week_view})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击声音按钮
            case R.id.v_sport_state_sound:
                startActivity(WorkOutSetActivity.class);
                break;
            //点击开始运动
            case R.id.btn_start_sport:
                SportSpData.setLastSportSelectIndex(getActivity(), mSelectSportVp.getCurrentItem());
                if (mSelectSportVp.getCurrentItem() == 4) {
                    startRunningInDoor();
                } else if (mSelectSportVp.getCurrentItem() == 2) {
                    startRunningOutDoor();
                } else {
                    startSportInDoor(mSelectSportVp.getCurrentItem());
                }
                break;
            case R.id.ll_week_view:
                startActivity(WorkoutCalendarActivity.class);
                break;
        }
    }

    /**
     * 位置信息变化
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //若定位有错误
        if (aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
//            Toasty.error(getActivity(), aMapLocation.getErrorInfo()).show();
            return;
        }
        //若定位
        float accuracy = aMapLocation.getAccuracy();// 精度
        //信号强
        if (accuracy < AppEnum.Gps.ACCURACY_POWER_HIGH) {
            tvGpsState.setText("强");
        } else {
            tvGpsState.setText("弱");
        }
        if (accuracy < AppEnum.Gps.ACCURACY_POWER_LOW
                && aMapLocation.getLatitude() > 10 && aMapLocation.getLongitude() > 10) {
            mSuccessCount++;
            if (mSuccessCount > 1) {
                if (!mHasLocation) {

                }
                mHasLocation = true;
                mLocalHandler.removeMessages(MSG_LOC_ERROR);
            }
        } else {

        }
    }


    /**
     * 接收到ble数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEventBus(BleConnectEE event) {
        if (event.msg == BleManager.MSG_NEW_HEARTBEAT) {
            if (event.hr != 0) {
                mStateHrTv.setText(event.hr + "");
            } else {
                mStateHrTv.setText("- -");
            }
        } else {
            updateWatchView();
        }
    }

    @Override
    protected void initParams() {
        mAdapter = new SelectSportVpAdapter(getActivity(), imgRes);
        mDialogBuilder = new DialogBuilder();
        mUserId = LocalApplication.getInstance().getLoginUser(getActivity()).userId;
        //初始化定位
        initLocationClient();

        mSelectSportVp.setPageMargin((int) (0 - DeviceU.dpToPixel(140)));
        mSelectSportVp.setOffscreenPageLimit(3);
        mSelectSportVp.setAdapter(mAdapter);
        mSelectSportVp.setPageTransformer(true, new RotateDownAndScalePageTransformer());
        mSelectSportVp.setCurrentItem(SportSpData.getLastSportSelectIndex(getActivity()));
        mSelectSportVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (mSelectSportVp.getCurrentItem() == 2) {
                        startCheckLocation();
                    } else {
                        llGps.setVisibility(View.GONE);
                    }
                } else {
                    locationClient.stopLocation();
                }
            }
        });

        mWeekTvs.add(tvWeek1);
        mWeekTvs.add(tvWeek2);
        mWeekTvs.add(tvWeek3);
        mWeekTvs.add(tvWeek4);
        mWeekTvs.add(tvWeek5);
        mWeekTvs.add(tvWeek6);
        mWeekTvs.add(tvWeek7);

        mPointVs.add(vWeek1);
        mPointVs.add(vWeek2);
        mPointVs.add(vWeek3);
        mPointVs.add(vWeek4);
        mPointVs.add(vWeek5);
        mPointVs.add(vWeek6);
        mPointVs.add(vWeek7);

        mWeekNameTvs.add(tvWeekName1);
        mWeekNameTvs.add(tvWeekName2);
        mWeekNameTvs.add(tvWeekName3);
        mWeekNameTvs.add(tvWeekName4);
        mWeekNameTvs.add(tvWeekName5);
        mWeekNameTvs.add(tvWeekName6);
        mWeekNameTvs.add(tvWeekName7);
    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
        EventBus.getDefault().register(this);
        updateSportView();
        updateWatchView();
        postGetWeekDayData();
        if (mSelectSportVp.getCurrentItem() == 2) {
            startCheckLocation();
        }
//        postGetUserInfo();
    }

    @Override
    protected void onInVisible() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 开始室内跑步
     */
    private void startRunningInDoor() {
        if (BleManager.getBleManager().mBleConnectE == null
                || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
            mDialogBuilder.showWatchDisconnectDialog(getActivity());
        } else {
            if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
                BleManager.getBleManager().mBleConnectE.stopSync();
            }
            BleManager.getBleManager().mBleConnectE.controlLight(true);
            int userId = LocalApplication.getInstance().getLoginUser(getActivity()).userId;
            WorkoutDE workoutDE = WorkoutDBData.createNewWorkout("室内跑步",userId, userId,
                    SportEnum.EffortType.RUNNING_INDOOR, 0, 0);
            LapDE lapDE = LapDBData.createNewLap(workoutDE.startTime, userId, userId);
            TimeSplitDBData.createNewTimeSplit(workoutDE.startTime, 0, userId, userId);
            LengthSplitDBData.createNewSplit(0,workoutDE.startTime,userId,userId);

            UploadDBData.createWorkoutHeadData(workoutDE);
            UploadDBData.createLapHeadData(lapDE);

            Bundle b = new Bundle();
            b.putBoolean("FirstStart", true);
            startActivity(RunningIndoorActivityV2.class, b);
        }
    }

    /**
     * 开始检查定位
     */
    private void startCheckLocation() {
        locationClient.startLocation();
        mHasLocation = true;
        mSuccessCount = 0;
        mLocalHandler.sendEmptyMessageDelayed(MSG_LOC_ERROR, INTERVAL_LOC_ERROR);
        llGps.setVisibility(View.VISIBLE);
    }


    /**
     * 开始室外跑步
     */
    private void startRunningOutDoor() {
        if (BleManager.getBleManager().mBleConnectE == null
                || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
            mDialogBuilder.showWatchDisconnectDialog(getActivity());
            return;
        }
        if (!mHasLocation) {
            Toasty.error(getActivity(), "请等待GPS信号正常").show();
            return;
        }
        locationClient.stopLocation();
        if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
            BleManager.getBleManager().mBleConnectE.stopSync();
        }
        BleManager.getBleManager().mBleConnectE.controlLight(true);
        int userId = LocalApplication.getInstance().getLoginUser(getActivity()).userId;
        WorkoutDE workoutDE = WorkoutDBData.createNewWorkout("户外跑步",userId, userId,
                SportEnum.EffortType.RUNNING_OUTDOOR, 0, 0);
        LapDE lapDE =  LapDBData.createNewLap(workoutDE.startTime, userId, userId);

        UploadDBData.createWorkoutHeadData(workoutDE);
        UploadDBData.createLapHeadData(lapDE);

        Bundle b = new Bundle();
        b.putBoolean("FirstStart", true);
        startActivity(SportingRunningOutdoorActivity.class, b);
    }


    /**
     * 开始室内健身
     */
    private void startSportInDoor(int itemIndex) {
        if (BleManager.getBleManager().mBleConnectE == null
                || BleManager.getBleManager().mBleConnectE.mIsConnected == false) {
            mDialogBuilder.showWatchDisconnectDialog(getActivity());
        } else {
            if (BleManager.getBleManager().mBleConnectE.mSyncNow) {
                BleManager.getBleManager().mBleConnectE.stopSync();
            }
            BleManager.getBleManager().mBleConnectE.controlLight(true);
            int userId = LocalApplication.getInstance().getLoginUser(getActivity()).userId;
            int type = SportEnum.EffortType.FREE_INDOOR;
            String workoutName = "无器械";
            if (itemIndex == 1) {
                type = SportEnum.EffortType.XIAO_QI_XIE;
                workoutName = "小器械";
            } else if (itemIndex == 3){
                type = SportEnum.EffortType.TIAO_SHENG;
                workoutName = "跳绳";
            }else if (itemIndex == 5) {
                type = SportEnum.EffortType.HUA_CHUAN;
                workoutName = "划船机";
            } else if (itemIndex == 6) {
                type = SportEnum.EffortType.DAN_CHE;
                workoutName = "动感单车";
            } else if (itemIndex == 7) {
                type = SportEnum.EffortType.TUO_YUAN;
                workoutName = "椭圆机";
            } else if (itemIndex == 8) {
                type = SportEnum.EffortType.LOU_TI;
                workoutName = "楼梯机";
            }

            WorkoutDE workoutDE = WorkoutDBData.createNewWorkout(workoutName,userId, userId, type,
                    SportEnum.TargetType.DEFAULT, 0);
            LapDE lapDE = LapDBData.createNewLap(workoutDE.startTime, userId, userId);

            UploadDBData.createWorkoutHeadData(workoutDE);
            UploadDBData.createLapHeadData(lapDE);

            Bundle b = new Bundle();
            b.putBoolean("FirstStart", true);
            startActivity(SportingIndoorActivity.class, b);
        }
    }


    /**
     * 更新运动页面
     */
    private void updateSportView() {
        mStateHrTv.setText("- -");
        Calendar todayCalendar = Calendar.getInstance();
        //获取本周一的日期
        Calendar calendar = Calendar.getInstance();
        int weekIndex = 1 - getWeekIndex(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_WEEK, weekIndex);
        for (int i = 0; i < 7; i++) {
            mWeekTvs.get(i).setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
            int CurrYear = calendar.get(Calendar.YEAR);
            int CurrMouth = calendar.get(Calendar.MONTH) + 1;
            String day = CurrYear + "-" + CurrMouth + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            //查询是否有锻炼
            WorkoutCalendarDE de = WorkoutCalendarDBData.getWorkoutCalendarData(mUserId, day);
            if (de != null) {
                GetCalendarRE.CalendarEntity cEntity = JSONObject.parseObject(de.data, GetCalendarRE.CalendarEntity.class);
                if (cEntity.workoutcount > 0) {
                    mPointVs.get(i).setBackgroundResource(R.drawable.ic_point_accent);
                } else {
                    mPointVs.get(i).setBackgroundResource(R.drawable.ic_point_gray);
                }
            } else {
                mPointVs.get(i).setBackgroundResource(R.drawable.ic_point_gray);
            }
            if (calendar.after(todayCalendar)) {
                mWeekTvs.get(i).setTextColor(Color.parseColor("#777777"));
                mWeekNameTvs.get(i).setTextColor(Color.parseColor("#777777"));
            } else {
                mWeekTvs.get(i).setTextColor(Color.parseColor("#ffffff"));
                mWeekNameTvs.get(i).setTextColor(Color.parseColor("#ffffff"));
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }


    /**
     * 更新手表状态页面
     */
    private void updateWatchView() {
        //若手表没有绑定
        if (LocalApplication.getInstance().getLoginUser(getActivity()).bleMac == null
                || LocalApplication.getInstance().getLoginUser(getActivity()).bleMac.equals("")) {
            mStateHrTv.setText("- -");
            return;
        }
        //若手表未连接
        if (BleManager.getBleManager().mBleConnectE == null
                || !BleManager.getBleManager().mBleConnectE.mIsConnected) {
            mStateHrTv.setText("- -");
            return;
        }

        if (BleManager.getBleManager().mBleConnectE.mIsConnected) {
            if (mDialogBuilder != null && mDialogBuilder.mWatchDisconnectDialog != null) {
                mDialogBuilder.mWatchDisconnectDialog.dismiss();
            }
        }
    }

    /**
     * 获取个人信息
     */
    private void postGetUserInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                //开始请求服务器
                RequestParams requestParams = RequestParamsBuilder.buildGetUserInfoRP(getActivity(),
                        UrlConfig.URL_GET_USER_INFO, UserSPData.getUserId(LocalApplication.applicationContext));
                x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            UserInfoRE user = JSON.parseObject(result.result, UserInfoRE.class);
                            UserDE userDE = LocalApplication.getInstance().getLoginUser(getActivity());
                            user.sessionid = userDE.sessionId;
                            UserDBData.CommonLogin(getActivity(), user);
                            mLocalHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
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

    /**
     * 获取周运动数据
     */
    private void postGetWeekDayData() {
        Calendar calendar = Calendar.getInstance();
        int weekIndex = 1 - getWeekIndex(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_WEEK, weekIndex);
        int CurrYear = calendar.get(Calendar.YEAR);
        int CurrMouth = calendar.get(Calendar.MONTH) + 1;
        final String startDay = CurrYear + "-" + CurrMouth + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        CurrYear = calendar.get(Calendar.YEAR);
        CurrMouth = calendar.get(Calendar.MONTH) + 1;
        final String endDay = CurrYear + "-" + CurrMouth + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetCalendarData(getActivity(),
                        UrlConfig.URL_GET_CALENDAR_DATA, mUserId, startDay, endDay);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetCalendarRE re = JSON.parseObject(result.result, GetCalendarRE.class);
                            if (re != null && re.calendar.size() > 0) {
                                for (GetCalendarRE.CalendarEntity entity : re.calendar) {
                                    WorkoutCalendarDBData.saveOrUpdate(mUserId, entity);
                                }
                                mLocalHandler.sendEmptyMessage(MSG_UPDATE_WEEK_DAY_VIEW);
                            }

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

    /**
     * 初始化定位
     */
    private void initLocationClient() {
        locationClient = new AMapLocationClient(getActivity());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationOption.setNeedAddress(false);//不需要返回位置信息
        locationOption.setWifiActiveScan(false);
        locationOption.setInterval(2000);//定位间隔
        locationOption.setOnceLocation(false);//持续定位
        //关闭缓存机制
        locationOption.setLocationCacheEnable(false);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
    }


    private int getWeekIndex(final int cIndex) {
        switch (cIndex) {
            case 0:
                return 7;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
        }
        return 0;
    }
}
