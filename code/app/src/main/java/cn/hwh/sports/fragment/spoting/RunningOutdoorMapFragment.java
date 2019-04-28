package cn.hwh.sports.fragment.spoting;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import butterknife.Unbinder;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.sporting.SportingRunningOutdoorActivity;
import cn.hwh.sports.config.AppEnum;
import cn.hwh.sports.config.MapEnum;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.LocationDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.Log;
import cn.hwh.sports.utils.TTsU;

/**
 * Created by machenike on 2017/6/1 0001.
 */

public class RunningOutdoorMapFragment extends BaseFragment implements AMapLocationListener {


    private static final String TAG = "RunningOutdoorMapFragment";

    private static final int MSG_ZOOM_MAP = 0x04;
    private static final long INTERVAL_FRESH = 1000 * 2;//刷新抓取的时间间隔

    /* local view */
    @BindView(R.id.map_running)
    MapView mapRunning;
    @BindView(R.id.v_gps_state)
    View vGpsState;
    @BindView(R.id.tv_gps_state)
    TextView tvGpsState;


    @BindView(R.id.tv_zone_0)
    TextView tvZone0;
    @BindView(R.id.tv_zone_1)
    TextView tvZone1;
    @BindView(R.id.tv_zone_2)
    TextView tvZone2;
    @BindView(R.id.tv_zone_3)
    TextView tvZone3;
    @BindView(R.id.tv_zone_4)
    TextView tvZone4;
    @BindView(R.id.tv_zone_5)
    TextView tvZone5;
    @BindView(R.id.tv_length)
    TextView tvLength;
    @BindView(R.id.tv_altitude)
    TextView tvAltitude;

    private AMap mAMap;//地图

    /* local data */
    private UserDE mUserDe;
    private WorkoutDE mWorkoutDE;//跑步信息

    //UI
    private Typeface mTypeFace;//数字字体
    private int mMaxHr;

    //绘图相关
    private List<LocationDE> mPointList = new ArrayList<LocationDE>();
    private Marker mStartMk;// 起点
    private Marker mEndMk;// 终点
    private BitmapDescriptor mUserBitmapDes;// 用户头像
    private float mPolylineWidth = MapEnum.WIDTH_POLYLINE;
    private float mOutOfMapY = DeviceU.dpToPixel(150);

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private long mLastLocationId = 0;// 最后一次位置序号
    private boolean mIsFirstIn = true;//是否第一次进来

    private Handler mHandler;// 刷新地图的Handler
    private Runnable mTicker;// 刷新地图的Runnable

    /* 构造函数 */
    public static RunningOutdoorMapFragment newInstance() {
        RunningOutdoorMapFragment fragment = new RunningOutdoorMapFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_running_outdoor_map;
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

    @OnClick(R.id.btn_close_map)
    public void onViewClicked() {
        SportingRunningOutdoorActivity activity = (SportingRunningOutdoorActivity) getActivity();
        activity.closeMap();
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
            return;
        }
        if (aMapLocation.getAccuracy() > AppEnum.Gps.ACCURACY_POWER_HIGH){
            vGpsState.setBackgroundResource(R.drawable.ic_gps_running_map_full);
            tvGpsState.setText("信号很强");
        }else if (aMapLocation.getAccuracy() > AppEnum.Gps.ACCURACY_POWER_LOW){
            vGpsState.setBackgroundResource(R.drawable.ic_gps_running_map_good);
            tvGpsState.setText("信号一般");
        }else {
            vGpsState.setBackgroundResource(R.drawable.ic_gps_running_map_poor);
            tvGpsState.setText("建议避开高楼大厦");
        }
        tvAltitude.setText(aMapLocation.getAltitude() + "");
    }

    @Override
    protected void initParams() {
        mapRunning.onCreate(mSavedInstanceState);

        mUserDe = LocalApplication.getInstance().getLoginUser(getActivity());
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDe.userId);
        mTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mMaxHr = mUserDe.maxHr;
        //设置字体
        tvAltitude.setTypeface(mTypeFace);
        tvLength.setTypeface(mTypeFace);

        //设置心率范围文本
        tvZone0.setText((int) (mMaxHr * 0.2) + "~" + (int) (mMaxHr * 0.5 + 1));
        tvZone1.setText((int) (mMaxHr * 0.5) + "~" + (int) (mMaxHr * 0.6 + 1));
        tvZone2.setText((int) (mMaxHr * 0.6) + "~" + (int) (mMaxHr * 0.7 + 1));
        tvZone3.setText((int) (mMaxHr * 0.7) + "~" + (int) (mMaxHr * 0.8 + 1));
        tvZone4.setText((int) (mMaxHr * 0.8) + "~" + (int) (mMaxHr * 0.9 + 1));
        tvZone5.setText((int) (mMaxHr * 0.9) + "~" + (int) (mMaxHr));

        if (mAMap == null) {
            mAMap = mapRunning.getMap();
        }
        //初始化定位
        initLocationClient();
        //加载用户头像
        doLoadUserAvatar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapRunning.onSaveInstanceState(outState);
    }

    @Override
    protected void causeGC() {
        if (mapRunning != null){
            mapRunning.onDestroy();
        }
    }

    @Override
    protected void onVisible() {
        mapRunning.onResume();
        initFreshHandler();
        mWorkoutDE = WorkoutDBData.getUnFinishWorkout(mUserDe.userId);
        tvLength.setText(LengthUtils.formatLength(mWorkoutDE.length));
        locationClient.startLocation();
    }

    @Override
    protected void onInVisible() {
        mapRunning.onPause();
        mHandler.removeCallbacks(mTicker);
        locationClient.stopLocation();
    }

    /**
     * 加载用户头像图片
     */
    private void doLoadUserAvatar() {
        if (mUserDe.avatar != null) {
            String loadAvatar = "";
            if (!mUserDe.avatar.startsWith("http://wx.qlogo.cn/")
                    && !mUserDe.avatar.equals("")) {
                loadAvatar = mUserDe.avatar + "?imageView2/1/w/200/h/200";
            } else {
                loadAvatar = mUserDe.avatar;
            }
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setLoadingDrawableId(R.drawable.ic_user_default)
                    .setFailureDrawableId(R.drawable.ic_user_default)
                    .build();
            x.image().loadFile(loadAvatar, imageOptions, new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(File file) {
                    mUserBitmapDes = ImageU.loadUserDesFromFile(getActivity(), file);
                    mEndMk.remove();
                    mEndMk.destroy();
                    mEndMk = null;
                    return false;
                }

                @Override
                public void onSuccess(File file) {
                    mUserBitmapDes = ImageU.loadUserDesFromFile(getActivity(), file);
                    mEndMk.remove();
                    mEndMk.destroy();
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
            if (mapRunning.getTop() > point.y || mapRunning.getBottom() - point.y < mOutOfMapY
                    || mapRunning.getLeft() > point.x || mapRunning.getRight() < point.x) {
                mAMap.animateCamera(CameraUpdateFactory.changeLatLng(
                        new LatLng(location.latitude, location.longitude)));
            }
            drawPolyLine(mPointList);
            if (mStartMk == null) {
                LocationDE startLoc = mPointList.get(0);
                mStartMk = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0f)
                        .position(new LatLng(startLoc.latitude, startLoc.longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                                (getResources(), R.drawable.ic_run_start)))
                        .draggable(false));
            }
            LocationDE endLoc = mPointList.get(mPointList.size() - 1);
            if (mEndMk == null) {
                mEndMk = mAMap
                        .addMarker(new MarkerOptions().icon(mUserBitmapDes)
                                .anchor(0.5f, 0.5f).position(
                                        new LatLng(endLoc.latitude, endLoc.longitude))
                                .draggable(false));
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
//        polylineOption = new PolylineOptions();
        for (int i = 0; i < drawList.size(); i++) {
            LocationDE location = drawList.get(i);
            //有分段
            if (!lapStartTime.equals(location.lapStartTime)) {
                //开始时间字段不等于空 ，画虚线
                if (!lapStartTime.equals("")) {
//                    PolylineOptions pausePolylineOption = new PolylineOptions();
//                    LatLng startLoc = new LatLng(drawList.get(i - 1).latitude, drawList.get(i - 1).longitude);
//                    LatLng endLoc = new LatLng(drawList.get(i).latitude, drawList.get(i).longitude);
//                    pausePolylineOption.add(startLoc, endLoc)
//                            .width(mPolylineWidth).setDottedLine(true).geodesic(true)
//                            .color(MapEnum.COLOR_PAUSE_LINE)
//                            .zIndex(MapEnum.ZINDEX_POLYLINE);
//                    mAMap.addPolyline(pausePolylineOption);
                }
//                画实线
                if (polylineOption != null) {
                    polylineOption.colorValues(colorList);
                    polylineOption.width(mPolylineWidth)
                            .color(MapEnum.COLOR_RUNNING_POLYLINE)
                            .zIndex(MapEnum.ZINDEX_POLYLINE)
                            .useGradient(true);
                    mAMap.addPolyline(polylineOption);
                }
                lapStartTime = location.lapStartTime;
                polylineOption = new PolylineOptions();
                polylineOption.getPoints().clear();
                colorList.add(ColorU.getColorByHeartbeat(location.hr * 100 / mUserDe.maxHr));
                polylineOption.add(new LatLng(location.latitude, location.longitude));
            }
            colorList.add(ColorU.getColorByHeartbeat(location.hr * 100 / mUserDe.maxHr));
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

}
