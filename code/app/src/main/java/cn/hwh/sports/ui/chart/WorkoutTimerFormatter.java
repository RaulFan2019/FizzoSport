package cn.hwh.sports.ui.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import cn.hwh.sports.utils.TimeU;

/**
 * Created by Raul.Fan on 2016/12/21.
 */

public class WorkoutTimerFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value <= 0){
            return "开始";
        }else {
            return TimeU.formatSecondsToShortHourTime((long) (value*60));
        }
    }
}
