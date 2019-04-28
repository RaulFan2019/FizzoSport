package cn.hwh.sports.activity.sporting;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.config.MapEnum;
import cn.hwh.sports.data.db.LocationDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.run.RunPauseFragment;
import cn.hwh.sports.fragment.run.RunRunningFragment;
import cn.hwh.sports.service.RunningService;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.TTsU;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class RunningOutdoorActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "RunningOutdoorActivity";

    private static final int MSG_ANIM_BIG = 0x01;
    private static final int MSG_ANIM_FINISH = 0x02;
    private static final int MSG_ZOOM_MAP = 0x04;

    private static final long INTERVAL_FRESH = 1000 * 2;//刷新抓取的时间间隔

    /* view */
    @BindView(R.id.tv_count_num)
    TextView mCountNumTv;//倒计时文本
    @BindView(R.id.ll_count_num)
    LinearLayout mCountNumLl;//倒计时布局

    @BindView(R.id.map_running)
    MapView mMapView;
    @BindView(R.id.tv_control)
    TextView mControlTv;
    @BindView(R.id.ll_basic)
    LinearLayout mBasicLl;
    @BindView(R.id.ll_base)
    FrameLayout mBaseLl;
    @BindView(R.id.btn_running_map)
    Button mMapBtn;
    @BindView(R.id.btn_running_control)
    Button mControlBtn;

    private AMap mAMap;//地图

    private boolean mStartFromUser = false;

    private UserDE mUserDE;//用户信息
    private WorkoutDE mWorkoutDE;//跑步信息

    //倒计时
    private Animation mBigAnimation;// 变大动画
    private int count = 4;// 计数数字
    private boolean mFirstStart = false;

    //绘图相关
    private List<LocationDE> mPointList = new ArrayList<LocationDE>();
    private Marker mStartMk;// 起点
    private Marker mEndMk;// 终点
    private BitmapDescriptor mUserBitmapDes;// 用户头像
    private float mPolylineWidth = MapEnum.WIDTH_POLYLINE;

    //当前跑步状态
    private boolean mIsShowData = true;//当前是否显示数据页面
    private boolean mIsRunning = true;//是否正在跑状态
    private long mLastLocationId = 0;// 最后一次位置序号
    private boolean mIsFirstIn = true;//是否第一次进来

    private Handler mHandler;// 刷新地图的Handler
    private Runnable mTicker;// 刷新地图的Runnable

    private RunRunningFragment mRunFragment;
    private RunPauseFragment mPauseFragment;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_running_outdoor;
    }

    /**
     * 地图放大Handler
     */
    Handler zoomHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //地图缩放
            if (msg.what == MSG_ZOOM_MAP) {
                if (mAMap.getCameraPosition() != null && mAMap.getCameraPosition().zoom > 16) {
                    mIsFirstIn = false;
                    if (mEndMk != null) {
                        mAMap.animateCamera(CameraUpdateFactory.changeLatLng(
                                new LatLng(mEndMk.getPosition().latitude, mEndMk.getPosition().longitude)));
                    }
                    return;
                }
                if (mIsFirstIn) {
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    if (zoomHandler != null) {
                        zoomHandler.sendEmptyMessageDelayed(MSG_ZOOM_MAP, 500);
                    }
                }
            }
        }
    };

    /**
     * 动画Handler
     */
    Handler animHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIM_BIG:
                    count--;
                    mCountNumTv.setText("" + count);
                    mBigAnimation.reset();
                    mCountNumTv.startAnimation(mBigAnimation);
                    if (count == 0) {
                        animHandler.sendEmptyMessageDelayed(MSG_ANIM_FINISH, 1000);
                    } else {
                        animHandler.sendEmptyMessageDelayed(MSG_ANIM_BIG, 1000);
                    }
                    break;
                case MSG_ANIM_FINISH:
                    mCountNumTv.clearAnimation();
                    mCountNumTv.setVisibility(View.GONE);
                    mBasicLl.setVisibility(View.VISIBLE);
                    animHandler.removeMessages(MSG_ANIM_FINISH);
                    setDataFragment();
                    break;
            }
        }
    };


    @OnClick({R.id.btn_running_finish, R.id.btn_running_control, R.id.btn_running_map})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击结束按钮
            case R.id.btn_running_finish:
                break;
            //点击控制按钮
            case R.id.btn_running_control:
                onControlBtnClick();
                break;
            //点击地图按钮
            case R.id.btn_running_map:
                onClickMapBtn();
                break;
        }
    }

    @Override
    protected void initData() {
        mUserDE = LocalApplication.getInstance().getLoginUser(RunningOutdoorActivity.this);
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDE.userId);
        //是否通过用户手动启动的
        if (getIntent().hasExtra("FirstStart")) {
            mFirstStart = true;
        } else {
            mFirstStart = false;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        //初始化地图
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void doMyCreate() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //判断是否显示倒数动画
        if (!mFirstStart) {
            mCountNumLl.setVisibility(View.GONE);
            mBasicLl.setVisibility(View.VISIBLE);
            setDataFragment();
        } else {
            mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_tv_big);
            animHandler.sendEmptyMessageDelayed(MSG_ANIM_BIG, 1000);
            animHandler.sendEmptyMessageDelayed(MSG_ANIM_FINISH, 10000);
        }

        //加载用户头像
        doLoadUserAvatar();
        // 启动服务
        Intent intent = new Intent(this, RunningService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        initFreshHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mHandler.removeCallbacks(mTicker);
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
        mMapView.onDestroy();
    }


    /**
     * 开始更新地图
     */
    private void initFreshHandler() {
        mHandler = new Handler();
        mTicker = new Runnable() {
            public void run() {
                if (mPointList.isEmpty()) {
                    // 过滤可以一次发送的
                    getUnDrawLocation();
                    if (mPointList.size() > 0) {
                        refreshMap();
                    }
                    mPointList.clear();
                }
                mHandler.postDelayed(mTicker, INTERVAL_FRESH);
            }
        };
        mHandler.post(mTicker);
    }

    /**
     * 获取没有画的点
     */
    private void getUnDrawLocation() {
        List<LocationDE> result = LocationDBData.getAfterLocFromWorkout(mWorkoutDE.startTime, mLastLocationId);
        if (result != null && result.size() > 0) {
            mPointList.addAll(result);
            mLastLocationId = result.get(result.size() - 1).locationId;
        }
    }

    /**
     * 刷新map
     */
    private void refreshMap() {
        if (mPointList.size() > 0) {
            LocationDE location = mPointList.get(mPointList.size() - 1);
            Point point = mAMap.getProjection().toScreenLocation(
                    new LatLng(location.latitude, location.longitude));
            if (mMapView.getTop() > point.y || mMapView.getBottom() < point.y
                    || mMapView.getLeft() > point.x || mMapView.getRight() < point.x) {
                mAMap.animateCamera(CameraUpdateFactory.changeLatLng(
                        new LatLng(location.latitude, location.longitude)));
            }
            drawPolyLine(mPointList);
            if (mStartMk == null) {
                LocationDE startLoc = mPointList.get(0);
                mStartMk = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(startLoc.latitude, startLoc.longitude))
                        .title("起点")
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                                (getResources(), R.drawable.ic_run)))
                        .draggable(true));
            }
            LocationDE endLoc = mPointList.get(mPointList.size() - 1);
            if (mEndMk == null) {
                mEndMk = mAMap
                        .addMarker(new MarkerOptions().icon(mUserBitmapDes)
                                .anchor(0.5f, 0.5f).position(
                                        new LatLng(endLoc.latitude, endLoc.longitude))
                                .setFlat(true));
            } else {
                mEndMk.setPosition(new LatLng(endLoc.latitude, endLoc.longitude));
            }
            if (mIsFirstIn) {
                if (zoomHandler != null) {
                    zoomHandler.sendEmptyMessage(MSG_ZOOM_MAP);
                }
            }
        }
    }

    /**
     * 画线段
     */
    private void drawPolyLine(final List<LocationDE> drawList) {
        PolylineOptions polylineOption = null;
        List<Integer> colorList = new ArrayList<Integer>();
        String lapStartTime = "";
        Log.e(TAG, "drawPolyLine drawList.size():" + drawList.size());
        for (int i = 0; i < drawList.size(); i++) {
            LocationDE location = drawList.get(i);
            //有分段
            if (!lapStartTime.equals(location.lapStartTime)) {
                //画虚线
                if (!lapStartTime.equals("")) {
                    PolylineOptions pausePolylineOption = new PolylineOptions();
                    LatLng startLoc = new LatLng(drawList.get(i - 1).latitude, drawList.get(i - 1).longitude);
                    LatLng endLoc = new LatLng(drawList.get(i).latitude, drawList.get(i).longitude);
                    pausePolylineOption.add(startLoc, endLoc)
                            .width(mPolylineWidth).setDottedLine(true).geodesic(true)
                            .color(MapEnum.COLOR_PAUSE_LINE)
                            .zIndex(MapEnum.ZINDEX_POLYLINE);
                    mAMap.addPolyline(pausePolylineOption);
                }
                //画实线
                if (polylineOption != null) {
                    polylineOption.width(mPolylineWidth)
                            .color(MapEnum.COLOR_RUNNING_POLYLINE)
                            .zIndex(MapEnum.ZINDEX_POLYLINE)
                            .useGradient(true);
                    mAMap.addPolyline(polylineOption);
                }
                lapStartTime = location.lapStartTime;
                polylineOption = new PolylineOptions();
            }
            colorList.add(ColorU.getColorByHeartbeat(location.hr * 100 / mUserDE.maxHr));
            polylineOption.add(new LatLng(location.latitude, location.longitude));
        }

        if (polylineOption != null) {
            polylineOption.colorValues(colorList);
            polylineOption.width(mPolylineWidth)
                    .color(MapEnum.COLOR_RUNNING_POLYLINE)
                    .zIndex(MapEnum.ZINDEX_POLYLINE)
                    .useGradient(true);
            mAMap.addPolyline(polylineOption);
        }
    }

    /**
     * 显示Fragment
     */
    public void setDataFragment() {
        try {
            // 开启一个Fragment事务
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
            if (mIsRunning) {
                if (mRunFragment == null) {
                    mRunFragment = RunRunningFragment.newInstance();
                }
                transaction.replace(R.id.ll_running_fragment_root, mRunFragment);
                mIsShowData = true;
                mMapBtn.setBackgroundResource(R.drawable.btn_run_map_off);
                //若跑步暂停
            } else {
                if (mPauseFragment == null) {
                    mPauseFragment = RunPauseFragment.newInstance();
                }
                transaction.replace(R.id.ll_running_fragment_root, mPauseFragment);
                transaction.addToBackStack(null);
                mIsShowData = true;
                mMapBtn.setBackgroundResource(R.drawable.btn_run_map_off);
            }
            transaction.commitAllowingStateLoss();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 点击控制按钮
     */
    private void onControlBtnClick() {
        if (mIsRunning) {
            setRunningStatus(RunningService.CMD_PAUSE);
            setDataFragment();
            mControlBtn.setBackgroundResource(R.drawable.btn_run_play);
            mControlTv.setText("继续");
            TTsU.playPauseRun(RunningOutdoorActivity.this);
        } else {
            setRunningStatus(RunningService.CMD_CONTINUE);
            setDataFragment();
            mControlBtn.setBackgroundResource(R.drawable.btn_run_pause);
            mControlTv.setText("暂停");
            TTsU.playContinueRun(RunningOutdoorActivity.this);
        }
    }

    /**
     * 改变跑步状态
     *
     * @param msg
     */
    public void setRunningStatus(int msg) {

        switch (msg) {
            //暂停跑步
            case RunningService.CMD_PAUSE:
                mIsRunning = false;
                TTsU.playPauseRun(RunningOutdoorActivity.this);
                break;
            //跑步继续
            case RunningService.CMD_CONTINUE:
                mIsRunning = true;
                TTsU.playContinueRun(RunningOutdoorActivity.this);
                break;
            //跑步结束
            case RunningService.CMD_FINISH:
                break;
        }
        Intent intent = new Intent(this, RunningService.class);
        intent.putExtra("CMD", msg);
        startService(intent);
    }

    /**
     * 点击地图按钮
     */
    private void onClickMapBtn() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mIsShowData) {
            if (mIsRunning) {
                if (mRunFragment != null) {
                    transaction.remove(mRunFragment);
                }
            } else {
                if (mPauseFragment != null) {
                    transaction.remove(mPauseFragment);
                }
            }
        } else {
            if (mIsRunning) {
                if (mRunFragment == null) {
                    mRunFragment = RunRunningFragment.newInstance();
                }
                transaction.replace(R.id.ll_running_fragment_root, mRunFragment);
            } else {
                if (mPauseFragment == null) {
                    mPauseFragment = RunPauseFragment.newInstance();
                }
                transaction.replace(R.id.ll_running_fragment_root, mPauseFragment);
            }
        }
        transaction.commitAllowingStateLoss();
        mIsShowData = !mIsShowData;
    }


    /**
     * 加载用户头像图片
     */
    private void doLoadUserAvatar() {
        if (mUserDE.avatar != null) {
            String loadAvatar = "";
            if (!mUserDE.avatar.startsWith("http://wx.qlogo.cn/")
                    && !mUserDE.avatar.equals("")) {
                loadAvatar = mUserDE.avatar + "?imageView2/1/w/200/h/200";
            } else {
                loadAvatar = mUserDE.avatar;
            }
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setLoadingDrawableId(R.drawable.ic_user_default)
                    .setFailureDrawableId(R.drawable.ic_user_default)
                    .build();
            x.image().loadFile(loadAvatar, imageOptions, new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(File file) {
                    mUserBitmapDes = ImageU.loadUserDesFromFile(RunningOutdoorActivity.this, file);
                    mEndMk = null;
                    return false;
                }

                @Override
                public void onSuccess(File file) {
                    mUserBitmapDes = ImageU.loadUserDesFromFile(RunningOutdoorActivity.this, file);
                    mEndMk = null;
                }

                @Override
                public void onError(Throwable throwable, boolean b) {

                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }
}
