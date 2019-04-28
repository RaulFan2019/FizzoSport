package cn.hwh.sports.entity.adapter;

import java.util.List;

import cn.hwh.sports.entity.db.DayMotionSummaryDE;

/**
 * Created by Administrator on 2016/12/1.
 * 距离数据实体类
 */

public class DistanceDetailListAE {
    public int showType;
    public String dayScope;
    public int value ;
    public String unit;
    public int maxDistanceValue;
    public int targetDistanceValue;
    public DayMotionSummaryDE dayDistance;//步数对象
    public List<DayMotionSummaryDE> dayDistanceList;//图表列表


    public DistanceDetailListAE(int showType, String dayScope, int value, String unit,
                                int maxDistanceValue, int targetDistanceValue, DayMotionSummaryDE dayDistance, List<DayMotionSummaryDE> dayDistanceList) {
        this.showType = showType;
        this.dayScope = dayScope;
        this.value = value;
        this.unit = unit;
        this.maxDistanceValue = maxDistanceValue;
        this.targetDistanceValue = targetDistanceValue;
        this.dayDistance = dayDistance;
        this.dayDistanceList = dayDistanceList;
    }

    public DistanceDetailListAE() {
    }
}
