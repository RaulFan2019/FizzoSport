package cn.hwh.sports.ui.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class HeartbeatFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;
    private int maxHr;
//    private FormattedStringCache.PrimFloat mFormattedStringCache;

    public HeartbeatFormatter(int maxHr) {
        this.maxHr = maxHr;
//        mFormattedStringCache = new FormattedStringCache.PrimFloat(new DecimalFormat("###,###,###,##0.0"));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value <= 100){
            return (int)(maxHr * value / 100) + "";
        }else {
            return "";
        }
    }

}
