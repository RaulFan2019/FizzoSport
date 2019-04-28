package cn.hwh.sports.entity.adapter;

import java.util.List;

import cn.hwh.sports.entity.db.DayMotionSummaryDE;

/**
 * Created by Administrator on 2016/12/2.
 * 运动时间列表实体类
 */

public class SportTimeDetailListAE {
    public int showType;
    public String dayScope;
    public int value ;
    public String unit;
    public int maxTimeValue;
    public int targetTimeValue;
    public DayMotionSummaryDE dayTime;//步数对象
    public List<DayMotionSummaryDE> dayTimeList;//图表列表

    public SportTimeDetailListAE(int showType, String dayScope, int value,
                                 String unit, int maxTimeValue, int targetTimeValue,
                                 DayMotionSummaryDE dayTime, List<DayMotionSummaryDE> dayTimeList) {

        this.showType = showType;
        this.dayScope = dayScope;
        this.value = value;
        this.unit = unit;
        this.maxTimeValue = maxTimeValue;
        this.targetTimeValue = targetTimeValue;
        this.dayTime = dayTime;
        this.dayTimeList = dayTimeList;
    }

    public SportTimeDetailListAE() {
    }
}
