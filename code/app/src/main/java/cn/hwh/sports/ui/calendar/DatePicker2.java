package cn.hwh.sports.ui.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 原版DatePicker
 */
public class DatePicker2 extends LinearLayout {
    private DPTManager mTManager;// 主题管理器
    private DPLManager mLManager;// 语言管理器

    private MonthView monthView;// 月视图

    private onMouthChangeListener mOnMouthChangeListener;


    public void setOnMouthChangeListener(onMouthChangeListener mOnMouthChangeListener) {
        this.mOnMouthChangeListener = mOnMouthChangeListener;
    }

    public interface onMouthChangeListener {
        void onChange(int year, int month);
    }

    ;

    public DatePicker2(Context context) {
        this(context, null);
    }

    public DatePicker2(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mTManager = DPTManager.getInstance();
        mLManager = DPLManager.getInstance();

        // 设置排列方向为竖向
        setOrientation(VERTICAL);

        LayoutParams llParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // --------------------------------------------------------------------------------周视图
        // 周视图根布局
        LinearLayout llWeek = new LinearLayout(context);
        llWeek.setBackgroundColor(mTManager.colorTitleBG());
        llWeek.setOrientation(HORIZONTAL);
        int llWeekPadding = MeasureUtil.dp2px(context, 5);
        llWeek.setPadding(0, llWeekPadding, 0, llWeekPadding);
        LayoutParams lpWeek = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpWeek.weight = 1;

        for (int i = 0; i < mLManager.titleWeek().length; i++) {
            TextView tvWeek = new TextView(context);
            tvWeek.setText(mLManager.titleWeek()[i]);
            tvWeek.setGravity(Gravity.CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.7f);
            tvWeek.setTextColor(mTManager.colorTitle());
            llWeek.addView(tvWeek, lpWeek);
        }
        addView(llWeek, llParams);

        // ------------------------------------------------------------------------------------月视图
        monthView = new MonthView(context);
        monthView.setOnDateChangeListener(new MonthView.OnDateChangeListener() {
            @Override
            public void onMonthChange(int month) {
            }

            @Override
            public void onYearChange(int year) {
            }

            @Override
            public void onAllChange(int year, int month) {

            }
        });
        monthView.setOnDateScrollChangeListener(new MonthView.OnDateScrollChangeListener() {
            @Override
            public void scrollLeft(int year, int month) {
                if (mOnMouthChangeListener != null) {
                    mOnMouthChangeListener.onChange(year, month);
                }
//                String str = "向左滑动=="+"年份="+year+"--月份=="+month;
            }

            @Override
            public void scrollRight(int year, int month) {
                if (mOnMouthChangeListener != null) {
                    mOnMouthChangeListener.onChange(year, month);
                }
            }

            @Override
            public void scrollTop(int year, int month) {
                if (mOnMouthChangeListener != null) {
                    mOnMouthChangeListener.onChange(year, month);
                }
            }

            @Override
            public void scrollBottom(int year, int month) {
                if (mOnMouthChangeListener != null) {
                    mOnMouthChangeListener.onChange(year, month);
                }
            }
        });
        addView(monthView, llParams);
    }

    /**
     * 设置初始化年月日期
     *
     * @param year  ...
     * @param month ...
     */
    public void setDate(int year, int month) {
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        monthView.setDate(year, month);
    }

    public void setDPDecor(DPDecor decor) {
        monthView.setDPDecor(decor);
    }

    /**
     * 设置日期选择模式
     *
     * @param mode ...
     */
    public void setMode(DPMode mode) {
        if (mode != DPMode.MULTIPLE) {
//            tvEnsure.setVisibility(GONE);
        }
        monthView.setDPMode(mode);
    }

    /**
     * 节日标识
     *
     * @param isFestivalDisplay
     */
    public void setFestivalDisplay(boolean isFestivalDisplay) {
        monthView.setFestivalDisplay(isFestivalDisplay);
    }

    /**
     * 今天标识
     *
     * @param isTodayDisplay
     */
    public void setTodayDisplay(boolean isTodayDisplay) {
        monthView.setTodayDisplay(isTodayDisplay);
    }

    /**
     * 假期标识
     *
     * @param isHolidayDisplay
     */
    public void setHolidayDisplay(boolean isHolidayDisplay) {
        monthView.setHolidayDisplay(isHolidayDisplay);
    }

    /**
     * 补休标识
     *
     * @param isDeferredDisplay
     */
    public void setDeferredDisplay(boolean isDeferredDisplay) {
        monthView.setDeferredDisplay(isDeferredDisplay);
    }

    /**
     * 设置单选监听器
     *
     * @param onDatePickedListener ...
     */
    public void setOnDatePickedListener(DatePicker.OnDatePickedListener onDatePickedListener) {
        if (monthView.getDPMode() != DPMode.SINGLE) {
            throw new RuntimeException(
                    "Current DPMode does not SINGLE! Please call setMode set DPMode to SINGLE!");
        }
        monthView.setOnDatePickedListener(onDatePickedListener);
    }

    public void defineRegion(int x, int y) {
        monthView.defineRegion(x, y);
    }

}
