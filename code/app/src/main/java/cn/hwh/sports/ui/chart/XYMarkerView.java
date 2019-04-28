
package cn.hwh.sports.ui.chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.DecimalFormat;

import cn.hwh.sports.R;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class XYMarkerView extends MarkerView {

    private TextView mTimeTv;
    private TextView mPowerTv;

    private DecimalFormat format;

    public XYMarkerView(Context context) {
        super(context, R.layout.chart_marker_view);

        mTimeTv = (TextView) findViewById(R.id.tv_time);
        mPowerTv = (TextView) findViewById(R.id.tv_power);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mTimeTv.setText((int)e.getX() + "分钟");
        mPowerTv.setText((int)e.getY() + "%");
//        mPowerTv.setTextColor(ColorUtils.getColorByHeartbeat((int) e.getY()));
    }

//    @Override
//    public int getXOffset(float xpos) {
//        // this will center the marker-view horizontally
//        return -(getWidth() / 2);
//    }
//
//    @Override
//    public int getYOffset(float ypos) {
//        // this will cause the marker-view to be above the selected value
//        return -getHeight();
//    }
}
