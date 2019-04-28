package cn.hwh.sports.activity.run;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.activity.sporting.SportingRunningOutdoorActivity;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.data.db.LapDBData;
import cn.hwh.sports.data.db.TimeSplitDBData;
import cn.hwh.sports.data.db.UploadDBData;
import cn.hwh.sports.data.db.UserDBData;
import cn.hwh.sports.data.db.WorkoutDBData;
import cn.hwh.sports.data.sp.SportSpData;
import cn.hwh.sports.data.sp.UserSPData;
import cn.hwh.sports.entity.db.LapDE;
import cn.hwh.sports.entity.db.WorkoutDE;
import cn.hwh.sports.ui.common.DialogBuilder;
import cn.hwh.sports.ui.common.SwitchView;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.T;

/**
 * Created by Raul.Fan on 2016/11/20.
 * 准备跑步页面
 */
public class ReadyRunActivity extends BaseActivity implements AMapLocationListener {

    private static final int MSG_WAIT_GPS = 0x01;

    /* view */
    @BindView(R.id.map_loc)
    MapView mMapView;
    @BindView(R.id.iv_gps)
    ImageView mGpsIv;
    @BindView(R.id.sv_tts)
    SwitchView mTtsSv;

    DialogBuilder mDialog;

    private AMap mAMap;//地图
    private AMapLocationClient mLocationClient = null;
    private LatLng mCurrLatLng;//当前位置信息

    private AnimationDrawable mAnimationDrawable;

    private int mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_run_ready;
    }


    @OnClick({R.id.btn_back, R.id.btn_start})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_start:
                onStartRunningClick();
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WAIT_GPS:
                    startLocation();
                    break;
            }
        }
    };

    /**
     * 定位返回
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                updateViewByLocation(aMapLocation);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }

        }
    }

    @Override
    protected void initData() {
        mUserId = LocalApplication.getInstance().getLoginUser(ReadyRunActivity.this).userId;

        mDialog = new DialogBuilder();
        //初始化定位参数
        AMapLocationClientOption lLocationOption = new AMapLocationClientOption();
        //
        mLocationClient = new AMapLocationClient(this);
        //设置定位监听
        mLocationClient.setLocationListener(this);
        //设置定位模式
        lLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔为2秒,最小间隔为1秒，默认2秒
        lLocationOption.setInterval(2000);
        //设置定位参数
        mLocationClient.setLocationOption(lLocationOption);
        //开始定位
        startLocation();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        //初始化地图
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        mTtsSv.setOpened(SportSpData.getTtsEnable(this));
        mTtsSv.setCanSwitch(true);

        mTtsSv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                SportSpData.setTtsEnable(ReadyRunActivity.this, true);
                mTtsSv.setOpened(true);
            }

            @Override
            public void toggleToOff(View view) {
                SportSpData.setTtsEnable(ReadyRunActivity.this, false);
                mTtsSv.setOpened(false);
            }
        });

        //初始化定位参数后，检查GPS开关是否打开，未打开时弹出提示框
        if (!DeviceU.getGPSIsOpen(this)) {
            mGpsIv.setImageResource(R.drawable.ic_gps_close);
            mDialog.showChoiceDialog(ReadyRunActivity.this, "提示", "发现GPS未打开", "去设置");
            mDialog.setListener(new DialogBuilder.ChoiceDialogListener() {
                @Override
                public void onConfirmBtnClick() {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }



    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void causeGC() {
        stopLocation();
        mMapView.onDestroy();
        mLocationClient.onDestroy();
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        //先去检查GPS是否打开
        if (!DeviceU.getGPSIsOpen(this)) {
            if (mLocationClient.isStarted()) {
                mLocationClient.stopLocation();
            }
            if (mAnimationDrawable != null) {
                mAnimationDrawable.stop();
            }
            mGpsIv.setImageResource(R.drawable.ic_gps_close);
            mHandler.sendEmptyMessageDelayed(MSG_WAIT_GPS, 2000);

        } else {
            mHandler.removeMessages(MSG_WAIT_GPS);
            mLocationClient.startLocation();
            mGpsIv.setImageResource(R.drawable.anim_gps_search);
            mAnimationDrawable = (AnimationDrawable) mGpsIv.getDrawable();
            mAnimationDrawable.start();
        }
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        mLocationClient.stopLocation();
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    /**
     * 根据Location的变化更新页面
     * @param aMapLocation
     */
    private void updateViewByLocation(AMapLocation aMapLocation){
        float lAccuracy = aMapLocation.getAccuracy();
        if (lAccuracy <= 10) {
            mGpsIv.setImageResource(R.drawable.ic_gps_full);
        } else if (lAccuracy <= 100) {
            mGpsIv.setImageResource(R.drawable.ic_gps_good);
        } else {
            mGpsIv.setImageResource(R.drawable.ic_gps_poor);
        }

        mCurrLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());

        mAMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        mCurrLatLng, 18, 30, 0)));
        mAMap.clear();
        mAMap.addMarker(new MarkerOptions().position(mCurrLatLng)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }


    /**
     * 点击开始跑步
     */
    private void onStartRunningClick(){
        WorkoutDE workoutDE = WorkoutDBData.createNewWorkout("户外跑步",mUserId, mUserId, SportEnum.EffortType.RUNNING_OUTDOOR,
                SportEnum.TargetType.DEFAULT,0);
        LapDE lapDE = LapDBData.createNewLap(workoutDE.startTime, mUserId, mUserId);

        UploadDBData.createWorkoutHeadData(workoutDE);
        UploadDBData.createLapHeadData(lapDE);
        startActivity(SportingRunningOutdoorActivity.class);
    }
}
