package cn.hwh.sports.ui.datepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;

import cn.hwh.sports.R;

/**
 * Created by Administrator on 2016/12/5.
 */

public class TimePicker extends LinearLayout implements NumberPicker.OnValueChangeListener {

    private NumberPicker mHourPicker;
    private NumberPicker mMinPicker;

    private Calendar mCalendar;

    private LayoutInflater mLayoutInflater;

    public TimePicker(Context context) {
        super(context);
        init(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        mLayoutInflater.inflate(R.layout.time_picker_layout,this,true);
        mHourPicker = (NumberPicker) findViewById(R.id.hour_picker);
        mMinPicker = (NumberPicker) findViewById(R.id.min_picker);

        mCalendar = Calendar.getInstance();
        setTime(mCalendar.getTime());
    }

    public TimePicker setTime(Date date){
        mCalendar.setTime(date);
        mHourPicker.setEndNumber(mCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        mMinPicker.setEndNumber(mCalendar.getActualMaximum(Calendar.MINUTE));

        mHourPicker.setCurrentNumber(mCalendar.get(Calendar.HOUR_OF_DAY));
        mMinPicker.setCurrentNumber(mCalendar.get(Calendar.MINUTE));
        return this;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public int getHour(){
        return mHourPicker.getCurrentNumber();
    }

    public int getMin(){
        return mMinPicker.getCurrentNumber();
    }

    public TimePicker setRowNumber(int rowNumber) {
        mHourPicker.setRowNumber(rowNumber);
        mMinPicker.setRowNumber(rowNumber);
        return this;
    }

    public TimePicker setTextSize(float textSize) {
        mHourPicker.setTextSize(textSize);
        mMinPicker.setTextSize(textSize);
        return this;
    }

    public TimePicker setFlagTextSize(float textSize) {
        mHourPicker.setFlagTextSize(textSize);
        mMinPicker.setFlagTextSize(textSize);
        return this;
    }

    public TimePicker setTextColor(int color) {
        mHourPicker.setTextColor(color);
        mMinPicker.setTextColor(color);
        return this;
    }

    public TimePicker setFlagTextColor(int color) {
        mHourPicker.setFlagTextColor(color);
        mMinPicker.setFlagTextColor(color);
        return this;
    }

    public TimePicker setBackground(int color) {
        super.setBackgroundColor(color);
        mHourPicker.setBackground(color);
        mMinPicker.setBackground(color);
        return this;
    }
}
