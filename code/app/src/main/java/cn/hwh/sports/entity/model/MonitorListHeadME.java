package cn.hwh.sports.entity.model;

import java.util.List;

import cn.hwh.sports.entity.db.DayMotionSummaryDE;

/**
 * Created by Raul.Fan on 2016/12/13.
 */

public class MonitorListHeadME {

    public static final int TYPE_STEP = 0x01;
    public static final int TYPE_LENGTH = 0x02;
    public static final int TYPE_SPORT_TIME = 0x03;
    public static final int TYPE_SPORT_POINT = 0x04;
    public static final int TYPE_CALORIE = 0x05;

    public int type;//头布局类型
    public int maxValue;//最大值
    public float targetValue;;//目标值
    public List<MonitorColumnDay> dayList;//一周锻炼数据


    public MonitorListHeadME(int type, int maxValue, float targetValue, List<MonitorColumnDay> dayList) {
        this.type = type;
        this.maxValue = maxValue;
        this.targetValue = targetValue;
        this.dayList = dayList;
    }

    public static class MonitorColumnDay{
        public String weekDay;
        public float value;

        public MonitorColumnDay(String weekDay, float value) {
            this.weekDay = weekDay;
            this.value = value;
        }
    }


}
