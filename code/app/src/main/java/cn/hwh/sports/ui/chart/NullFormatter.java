package cn.hwh.sports.ui.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class NullFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;
//    private FormattedStringCache.PrimFloat mFormattedStringCache;

    public NullFormatter() {
//        mFormattedStringCache = new FormattedStringCache.PrimFloat(new DecimalFormat("###,###,###,##0.0"));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return "";
    }

}
