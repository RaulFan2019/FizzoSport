
package cn.hwh.sports.ui.chart;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import cn.hwh.sports.R;
import cn.hwh.sports.data.db.HrDBData;
import cn.hwh.sports.entity.db.HrDE;
import cn.hwh.sports.utils.DeviceU;
import cn.hwh.sports.utils.Log;


/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MarkerViewForRunning extends MarkerView {

    private TextView tv;
    private View mAccentV;
    private View mContrastV;


    private RelativeLayout.LayoutParams mAccentLp;
    private RelativeLayout.LayoutParams mContrastLp;

    private String workoutStartTime;

    public MarkerViewForRunning(Context context, int layoutResource, String workoutStartTime) {
        super(context, layoutResource);
        tv = (TextView) findViewById(R.id.tv_info);
        mAccentV = findViewById(R.id.v_mark_accent);
        mContrastV = findViewById(R.id.v_mark_contrast);

        mAccentLp = (LayoutParams) mAccentV.getLayoutParams();
        mContrastLp = (LayoutParams) mContrastV.getLayoutParams();

        this.workoutStartTime = workoutStartTime;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        HrDE hrDE = HrDBData.getHrInfoAlainTimeOffSet(workoutStartTime, (int) e.getX());
        if (hrDE != null) {
            tv.setText(hrDE.timeOffSet / 60 + "分" + hrDE.timeOffSet % 60 + "秒\n" + "心率：" + hrDE.heartbeat + "\n" + "步频：" + hrDE.strideFreQuency);
            mAccentLp.topMargin = (int) DeviceU.dpToPixel((float) (180 * (1 - (hrDE.strideFreQuency / 386.0))) - 2);
            mContrastLp.topMargin = (int) DeviceU.dpToPixel((float) (180 * (1 - (hrDE.heartbeat / 309.0))) - 2);
            mAccentV.setLayoutParams(mAccentLp);
            mContrastV.setLayoutParams(mContrastLp);
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
