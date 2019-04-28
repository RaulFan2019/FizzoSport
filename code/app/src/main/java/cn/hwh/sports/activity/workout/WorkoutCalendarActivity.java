package cn.hwh.sports.activity.workout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.R;
import cn.hwh.sports.activity.BaseActivity;
import cn.hwh.sports.adapter.WorkoutCalendarListAdapter;
import cn.hwh.sports.config.SportEnum;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.data.db.WorkoutCalendarDBData;
import cn.hwh.sports.entity.db.WorkoutCalendarDE;
import cn.hwh.sports.entity.net.BaseRE;
import cn.hwh.sports.entity.net.GetCalendarRE;
import cn.hwh.sports.network.BaseResponseParser;
import cn.hwh.sports.network.RequestParamsBuilder;
import cn.hwh.sports.ui.calendar.DPCManager;
import cn.hwh.sports.ui.calendar.DPDecor;
import cn.hwh.sports.ui.calendar.DPMode;
import cn.hwh.sports.ui.calendar.DatePicker;
import cn.hwh.sports.ui.calendar.DatePicker2;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;

/**
 * Created by Raul.Fan on 2017/5/2.
 */

public class WorkoutCalendarActivity extends BaseActivity {

    private static final String TAG = "WorkoutCalendarActivity";

    private static final int MSG_UPDATE_CURR_VIEW = 0x01;


    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.calendar)
    DatePicker2 mCalendar;
    @BindView(R.id.tv_day_step)
    TextView tvDayStep;
    @BindView(R.id.tv_day_calorie)
    TextView tvDayCalorie;
    @BindView(R.id.tv_day_time)
    TextView tvDayTime;
    @BindView(R.id.lv_day)
    ListView lvDay;
    @BindView(R.id.tv_data_none)
    TextView tvDataNone;
    @BindView(R.id.ll_data_none)
    LinearLayout llDataNone;

    private WorkoutCalendarListAdapter mAdapter;

    /* local data */
    private int mCurrYear;
    private int mCurrMouth;
    private String mCurrDay;
    private int mUserId;

    private Typeface mTypeface;
    private List<String> mWorkday = new ArrayList<>();
    private Callback.Cancelable mCancelable;
    private List<GetCalendarRE.CalendarEntity.WorkoutsEntity> workouts = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workout_calendar;
    }


    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_CURR_VIEW:
                    updateCurrMouthView();
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_trend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_trend:
                startActivity(WorkoutTrendActivity.class);
                break;
        }
    }

    @Override
    protected void initData() {
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/TradeGothicLTStd-BdCn20.otf");
        mUserId = LocalApplication.getInstance().getLoginUser(WorkoutCalendarActivity.this).userId;
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMouth = calendar.get(Calendar.MONTH) + 1;
        mCurrDay = mCurrYear + "-" + mCurrMouth + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        mAdapter = new WorkoutCalendarListAdapter(WorkoutCalendarActivity.this, workouts);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tvDayCalorie.setTypeface(mTypeface);
        tvDayStep.setTypeface(mTypeface);
        tvDayTime.setTypeface(mTypeface);
        initCalendar();
        mTitleTv.setText(mCurrYear + "年" + mCurrMouth + "月");
        mCalendar.setDate(mCurrYear, mCurrMouth);
        lvDay.setAdapter(mAdapter);
        lvDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetCalendarRE.CalendarEntity.WorkoutsEntity ae = workouts.get(i);
                if (ae.type == SportEnum.EffortType.RUNNING_INDOOR) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("workoutId", ae.id);
                    bundle.putString("workoutStartTime", ae.starttime);
                    bundle.putInt("resource", ae.resource);
                    bundle.putInt("calorie", ae.calorie);
                    startActivity(WorkoutIndoorActivity.class, bundle);
                } else if (ae.type == SportEnum.EffortType.RUNNING_OUTDOOR){
                    Bundle bundle = new Bundle();
                    bundle.putInt("workoutId", ae.id);
                    bundle.putString("workoutStartTime", ae.starttime);
                    bundle.putInt("resource", ae.resource);
                    bundle.putInt("calorie", ae.calorie);
                    startActivity(WorkoutRunningOutdoorActivity.class, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("workoutId", ae.id);
                    bundle.putString("workoutStartTime", ae.starttime);
                    bundle.putInt("calorie", ae.calorie);
                    bundle.putInt("resource", ae.resource);
                    bundle.putInt("type", ae.type);
                    startActivity(WorkoutIndoorActivity.class, bundle);
                }
            }
        });

    }

    @Override
    protected void doMyCreate() {
        updateCurrMouthView();
        mLocalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pickToday();
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        postGetCurrMouthData();
    }

    @Override
    protected void causeGC() {
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        mLocalHandler.removeCallbacksAndMessages(null);
    }


    private void initCalendar() {
        mCalendar.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(Color.parseColor("#ff4612"));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(rect.centerX(), rect.centerY() - rect.height() / 10, rect.width() / 3.8F, paint);
            }
        });
        mCalendar.setOnMouthChangeListener(new DatePicker2.onMouthChangeListener() {
            @Override
            public void onChange(int year, int month) {
                mCurrMouth = month;
                mCurrYear = year;
                mTitleTv.setText(mCurrYear + "年" + mCurrMouth + "月");
//                updateCurrMouthView();
                postGetCurrMouthData();
            }
        });
        mCalendar.setMode(DPMode.SINGLE);
        mCalendar.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                mCurrDay = date;
                updateDayView();
            }
        });
    }

    /**
     * 更新当前日历月
     */
    private void updateCurrMouthView() {
//        String startDay = mCurrYear + "-" + mCurrMouth + "-1";
//        String endDay = null;
//        if (mCurrMouth == 12) {
//            endDay = (mCurrYear + 1) + "-1-1";
//        } else {
//            endDay = mCurrYear + "-" + (mCurrMouth + 1) + "-1";
//        }
//        Log.v(TAG,"endDay:" + endDay);
        List<WorkoutCalendarDE> workoutCalendarDEs = WorkoutCalendarDBData.getWorkoutCalendarList(mUserId);
        mWorkday.clear();
        if (workoutCalendarDEs != null && workoutCalendarDEs.size() > 0) {
            for (WorkoutCalendarDE workoutCalendarDE : workoutCalendarDEs) {
                GetCalendarRE.CalendarEntity calendar = JSONObject.parseObject(workoutCalendarDE.data, GetCalendarRE.CalendarEntity.class);
                if (calendar.workoutcount > 0){
                    mWorkday.add(workoutCalendarDE.day);
                }
            }
        }
        DPCManager.getInstance().clearnDATE_CACHE();
        DPCManager.getInstance().setDecorBG(mWorkday);
        mCalendar.invalidate();
    }

    /**
     * 更新当日信息
     */
    private void updateDayView() {
//        Log.v(TAG,"updateDayView");
        WorkoutCalendarDE de = WorkoutCalendarDBData.getWorkoutCalendarData(mUserId, mCurrDay);
        if (de != null) {
            GetCalendarRE.CalendarEntity calendar = JSONObject.parseObject(de.data, GetCalendarRE.CalendarEntity.class);
            tvDayCalorie.setText(calendar.calorie + "");
            tvDayStep.setText(calendar.stepcount + "");
            tvDayTime.setText(calendar.minutes + "");
            workouts.clear();
            for (int i = calendar.workouts.size() -1 ; i >= 0 ; i--){
                workouts.add(calendar.workouts.get(i));
            }
            mAdapter.notifyDataSetChanged();
            if (workouts.size()>0){
                lvDay.setVisibility(View.VISIBLE);
                llDataNone.setVisibility(View.GONE);
            }else {
                llDataNone.setVisibility(View.VISIBLE);
                lvDay.setVisibility(View.GONE);
            }
        }else {
            llDataNone.setVisibility(View.VISIBLE);
            lvDay.setVisibility(View.GONE);
        }
    }

    /**
     * 获取当前月的数据
     */
    private void postGetCurrMouthData() {
        final String startDay = mCurrYear + "-" + mCurrMouth + "-1";
        String endDay = "";
        if (mCurrMouth == 12) {
            endDay = (mCurrYear + 1) + "-1-1";
        } else {
            endDay = mCurrYear + "-" + (mCurrMouth + 1) + "-1";
        }
        final String finalEndDay = endDay;
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetCalendarData(WorkoutCalendarActivity.this,
                        UrlConfig.URL_GET_CALENDAR_DATA, mUserId, startDay, finalEndDay);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetCalendarRE re = JSON.parseObject(result.result, GetCalendarRE.class);
                            if (re != null && re.calendar.size() > 0) {
                                for (GetCalendarRE.CalendarEntity entity : re.calendar) {
//                                    Log.v(TAG, "entity.workoutcount :" + entity.workoutcount);
//                                    if (entity.workoutcount != 0) {
//                                        Log.v(TAG, "date short :" + entity.date_short);
                                        WorkoutCalendarDBData.saveOrUpdate(mUserId, entity);
//                                    }
                                }
                                mLocalHandler.sendEmptyMessage(MSG_UPDATE_CURR_VIEW);
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
     * 选择今天
     */
    private void pickToday() {
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.WEEK_OF_MONTH) - 1;
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//        Log.v(TAG, "week:" + week);
//        Log.v(TAG, "weekDay" + weekDay);
        int y = DeviceU.dip2px(WorkoutCalendarActivity.this, 19 + 32 * week);
        int x = DeviceU.dip2px(WorkoutCalendarActivity.this, 14 + 52 * weekDay);
        mCalendar.defineRegion(x, y);
    }

}
