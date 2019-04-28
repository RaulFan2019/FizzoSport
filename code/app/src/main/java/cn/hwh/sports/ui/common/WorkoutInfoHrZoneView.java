package cn.hwh.sports.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hwh.sports.R;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Raul.Fan on 2016/12/21.
 */

public class WorkoutInfoHrZoneView extends LinearLayout {


    View hrZoneV;
    TextView hrTimeTv;
    TextView hrZoneTipTv;

    public WorkoutInfoHrZoneView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_workout_info_hr_zone, this, true);

        hrZoneV = findViewById(R.id.v_zone);
        hrTimeTv = (TextView) findViewById(R.id.tv_time);
        hrZoneTipTv = (TextView) findViewById(R.id.tv_tip);
    }

    public WorkoutInfoHrZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_workout_info_hr_zone, this, true);

        hrZoneV = findViewById(R.id.v_zone);
        hrTimeTv = (TextView) findViewById(R.id.tv_time);
        hrZoneTipTv = (TextView) findViewById(R.id.tv_tip);
    }

    /**
     * 设置数据
     *
     * @param zone
     * @param totalTime
     * @param zoneTime
     */
    public void setData(final int zone, final int totalTime, final int zoneTime) {
        if (zone == 0) {
            hrZoneTipTv.setText("常规活动");
            hrZoneV.setBackgroundResource(R.color.hr_zone_6);
        } else if (zone == 1) {
            hrZoneTipTv.setText("热身");
            hrZoneV.setBackgroundResource(R.color.hr_zone_5);
        } else if (zone == 2) {
            hrZoneTipTv.setText("燃脂");
            hrZoneV.setBackgroundResource(R.color.hr_zone_4);
        } else if (zone == 3) {
            hrZoneTipTv.setText("增强心肺");
            hrZoneV.setBackgroundResource(R.color.hr_zone_3);
        } else if (zone == 4) {
            hrZoneTipTv.setText("提升耐力");
            hrZoneV.setBackgroundResource(R.color.hr_zone_2);
        } else if (zone == 5) {
            hrZoneTipTv.setText("竞技训练");
            hrZoneV.setBackgroundResource(R.color.hr_zone_1);
        }
        hrTimeTv.setText(zoneTime + "分钟");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) hrZoneV.getLayoutParams();
        if (zoneTime == 0 || totalTime == 0) {
            params.width = (int) DeviceU.dpToPixel(2.0f);
        } else {
            if (zoneTime > totalTime){
                params.width = (int) DeviceU.dpToPixel(260f);
            }else {
                params.width = (int) DeviceU.dpToPixel(260f) * zoneTime / totalTime;
            }

        }
        hrZoneV.setLayoutParams(params);
    }
}
