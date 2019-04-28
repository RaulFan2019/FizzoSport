package cn.hwh.sports.fragment.workout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.config.MapEnum;
import cn.hwh.sports.data.db.LengthSplitDBData;
import cn.hwh.sports.data.db.LocationDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.entity.db.LengthSplitDE;
import cn.hwh.sports.entity.db.LocationDE;
import cn.hwh.sports.entity.db.UserDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.fragment.BaseFragment;
import cn.hwh.sports.utils.ColorU;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.ImageU;
import cn.hwh.sports.utils.LengthUtils;
import cn.hwh.sports.utils.TimeU;

/**
 * Created by machenike on 2017/6/5 0005.
 */

public class WorkoutRunningOutdoorMapFragment extends BaseFragment implements AMap.OnCameraChangeListener {


    /* contains */
    private static final int MSG_REFRESH_MAP = 0x01;

    private static final String TAG = "WorkoutRunningOutdoorMapFragment";

    @BindView(R.id.fl_base)
    public FrameLayout flBase;

    @BindView(R.id.map_running)
    MapView mapRunning;
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
    @BindView(R.id.tv_pace_avg)
    TextView tvPaceAvg;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.v_map_km)
    View vMapKm;
    Unbinder unbinder;


    private AMap mAMap;//地图
    private float mCurrZoom;//当前放大比例

    /* local data */
    private String mWorkoutStartTime;
    private UserDE mUserDe;

    //画图相关
    private WorkoutDE mWorkoutDe;
    private List<LocationDE> locationList = new ArrayList<>();
    private List<LengthSplitDE> splitList = new ArrayList<>();

    private List<Marker> mSplitMarkList = new ArrayList<Marker>();

    private int mSplitMarkerZoomSize;
    private double mMinLaLng = 0;
    private double mMaxLaLng = 0;
    private double mMinLoLng = 0;
    private double mMaxLoLng = 0;
    private LatLngBounds mBounds;// 地图显示范围
    private boolean isShowKm = true;
    private Typeface typeFace;//字体

    public static WorkoutRunningOutdoorMapFragment newInstance() {
        WorkoutRunningOutdoorMapFragment fragment = new WorkoutRunningOutdoorMapFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout_running_outdoor_map;
    }


    @OnClick({R.id.v_map_position, R.id.v_map_km})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.v_map_position:
                freshMapHandler.sendEmptyMessage(MSG_REFRESH_MAP);
                break;
            case R.id.v_map_km:
                if (isShowKm) {
                    destroySplitMarker();
                } else {
                    drawSplitMarker();
                }
                isShowKm = !isShowKm;
                break;
        }
    }

    Handler freshMapHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REFRESH_MAP) {
                if (mBounds != null) {
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, (int) DeviceU.dpToPixel(85)));
                } else {
                    freshMapHandler.sendEmptyMessageDelayed(MSG_REFRESH_MAP, 200);
                }
            }
        }
    };

    @Override
    protected void initParams() {
        mapRunning.onCreate(mSavedInstanceState);
        if (mAMap == null) {
            mAMap = mapRunning.getMap();
            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.setOnCameraChangeListener(this);
            mSplitMarkerZoomSize = 1;
        }
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mUserDe = LocalApplication.getInstance().getLoginUser(getActivity());

        tvDuration.setTypeface(typeFace);
        tvPaceAvg.setTypeface(typeFace);
        tvLength.setTypeface(typeFace);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapRunning.onSaveInstanceState(outState);
    }

    @Override
    protected void causeGC() {
        if (mapRunning != null) {
            mapRunning.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapRunning.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapRunning.onPause();
    }

    @Override
    protected void onVisible() {

    }

    @Override
    protected void onInVisible() {

    }


    /**
     * activity 调用更新
     */
    public void updateViewByActivity(final String workoutStartTime) {
        mWorkoutStartTime = workoutStartTime;
        updateData();
        updateViews();
    }


    /**
     * 更新数据
     */
    private void updateData() {
        mWorkoutDe = WorkoutDBData.getWorkoutByStartTime(mUserDe.userId, mWorkoutStartTime);
        locationList = LocationDBData.getAfterLocFromWorkout(mWorkoutStartTime, -1);
        splitList = LengthSplitDBData.getAllSplitInWorkout(mWorkoutStartTime);
    }

    /**
     * 更新页面
     */
    private void updateViews() {
        updateMapView();
        updateZoneView();
        updateWorkoutDataView();
    }


    /**
     * 更新地图页面
     */
    private void updateMapView() {
//        mAMap.clear();
        if (locationList != null && locationList.size() > 0) {
            // 起始点位置
            mAMap.addMarker(
                    new MarkerOptions().anchor(0.5f, 0f)
                            .position(new LatLng(locationList.get(0).latitude,
                                    locationList.get(0).longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_run_start))
                            .draggable(false));
            // 终点位置
            mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .position(new LatLng(locationList.get(locationList.size() - 1).latitude,
                            locationList.get(locationList.size() - 1).longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_run_end))
                    .draggable(false));
            drawMask(locationList.get(0));
            drawSplitMarker();
            //画线
            new Thread() {
                public void run() {
                    PolylineOptions polylineOption = null;
                    List<Integer> colorList = new ArrayList<Integer>();
                    String lapstartTime = "";
                    for (int i = 0; i < locationList.size(); i++) {
                        LocationDE location = locationList.get(i);
                        if (!lapstartTime.equals(location.lapStartTime)) {
                            if (polylineOption != null) {
                                polylineOption.colorValues(colorList);
                                polylineOption.width(MapEnum.WIDTH_POLYLINE)
                                        .color(MapEnum.COLOR_POLYLINE)
                                        .zIndex(MapEnum.ZINDEX_POLYLINE)
                                        .useGradient(true);
                                mAMap.addPolyline(polylineOption);
                            }
                            polylineOption = new PolylineOptions();
                            colorList.clear();
                            lapstartTime = location.lapStartTime;
                        }
                        polylineOption.add(new LatLng(location.latitude, location.longitude));
                        colorList.add(ColorU.getColorByHeartbeat(location.hr * 100 / mUserDe.maxHr));
                        if (location.latitude < mMinLaLng || mMinLaLng == 0) {
                            mMinLaLng = location.latitude;
                        }
                        if (location.latitude > mMaxLaLng || mMaxLaLng == 0) {
                            mMaxLaLng = location.latitude;
                        }
                        if (location.longitude < mMinLoLng || mMinLoLng == 0) {
                            mMinLoLng = location.longitude;
                        }
                        if (location.longitude > mMaxLoLng || mMaxLoLng == 0) {
                            mMaxLoLng = location.longitude;
                        }
                    }
                    if (polylineOption != null) {
                        polylineOption.colorValues(colorList);
                        polylineOption.width(MapEnum.WIDTH_POLYLINE)
                                .color(MapEnum.COLOR_POLYLINE).
                                zIndex(MapEnum.ZINDEX_POLYLINE)
                                .useGradient(true);
                        mAMap.addPolyline(polylineOption);
                    }
                    if (mMinLaLng != 0 && mMaxLaLng != 0 && mMinLoLng != 0 && mMaxLoLng != 0) {
                        mBounds = new LatLngBounds(new LatLng(mMinLaLng, mMinLoLng), new LatLng(mMaxLaLng, mMaxLoLng));
                    }
                    freshMapHandler.sendEmptyMessage(MSG_REFRESH_MAP);
                }
            }.start();
        }
    }


    /**
     * 绘制蒙版
     */
    private void drawMask(LocationDE loc) {
        // 绘制一个长方形
        mAMap.addPolygon(
                new PolygonOptions().addAll(createRectangle(new LatLng(loc.latitude, loc.longitude), 10, 10))
                        .fillColor(MapEnum.COLOR_MASK).strokeColor(Color.TRANSPARENT).strokeWidth(1))
                .setZIndex(MapEnum.ZINDEX_MASK);
    }

    /**
     * 生成一个长方形的四个坐标点
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
    }

    /**
     * 销毁 split 标记
     */
    private void destroySplitMarker() {
        vMapKm.setBackgroundResource(R.drawable.ic_map_km_off);
        for (Marker marker : mSplitMarkList) {
            marker.remove();
            marker.destroy();
        }
    }

    /**
     * 画splite 的标记
     */
    private void drawSplitMarker() {
        vMapKm.setBackgroundResource(R.drawable.ic_map_km_on);
        // splite 终点的位置
        mSplitMarkList = new ArrayList<Marker>();
        mSplitMarkList.clear();
        if (splitList == null) {
            return;
        }
        for (int i = 0, splitSize = splitList.size(); i < splitSize; i += mSplitMarkerZoomSize) {
            LengthSplitDE split = splitList.get(i);
            if (split.latitude == 0 && split.longitude == 0) {
                break;
            }
            LatLng latLng = new LatLng(split.latitude, split.longitude);
            mSplitMarkList.add(mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng)
                    .draggable(false).zIndex(MapEnum.ZINDEX_SPLITE_IMAGE)
                    .icon(BitmapDescriptorFactory.fromBitmap(ImageU.GetSplitBitmap(getActivity(), (i + 1), typeFace)))));
        }
    }


    /**
     * 更新心率区间页面
     */
    private void updateZoneView() {
        //设置心率范围文本
        tvZone0.setText((int) (mUserDe.maxHr * 0.2) + "~" + (int) (mUserDe.maxHr * 0.5 + 1));
        tvZone1.setText((int) (mUserDe.maxHr * 0.5) + "~" + (int) (mUserDe.maxHr * 0.6 + 1));
        tvZone2.setText((int) (mUserDe.maxHr * 0.6) + "~" + (int) (mUserDe.maxHr * 0.7 + 1));
        tvZone3.setText((int) (mUserDe.maxHr * 0.7) + "~" + (int) (mUserDe.maxHr * 0.8 + 1));
        tvZone4.setText((int) (mUserDe.maxHr * 0.8) + "~" + (int) (mUserDe.maxHr * 0.9 + 1));
        tvZone5.setText((int) (mUserDe.maxHr * 0.9) + "~" + (int) (mUserDe.maxHr));
    }

    /**
     * 更新记录相关数据页面
     */
    private void updateWorkoutDataView() {
        tvLength.setText(LengthUtils.formatLength(mWorkoutDe.length));
        tvPaceAvg.setText(TimeU.formatSecondsToPace((long) (mWorkoutDe.duration * 1000 / mWorkoutDe.length)));
        tvDuration.setText(mWorkoutDe.duration / 60 + "");
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (!isShowKm || (int) mCurrZoom == (int) cameraPosition.zoom) {
            return;
        }

        if (cameraPosition.zoom > 15) {
            mSplitMarkerZoomSize = 1;
        } else if (cameraPosition.zoom > 13) {
            mSplitMarkerZoomSize = 2;
        } else if (cameraPosition.zoom > 12) {
            mSplitMarkerZoomSize = 4;
        } else if (cameraPosition.zoom > 11) {
            mSplitMarkerZoomSize = 7;
        } else if (cameraPosition.zoom > 10) {
            mSplitMarkerZoomSize = 11;
        } else {
            mSplitMarkerZoomSize = 16;
        }
        destroySplitMarker();
        drawSplitMarker();
        mCurrZoom = cameraPosition.zoom;
    }


}
