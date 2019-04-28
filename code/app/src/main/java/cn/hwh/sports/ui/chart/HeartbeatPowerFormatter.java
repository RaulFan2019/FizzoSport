package cn.hwh.sports.ui.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class HeartbeatPowerFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;
//    private FormattedStringCache.PrimFloat mFormattedStringCache;

    public HeartbeatPowerFormatter() {
//        mFormattedStringCache = new FormattedStringCache.PrimFloat(new DecimalFormat("###,###,###,##0.0"));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value > 100) {
            return "";
        }
        return (int)value + "";
    }
}
