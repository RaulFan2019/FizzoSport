package cn.hwh.sports.entity.adapter;

import java.util.List;

import cn.hwh.sports.entity.db.DayMotionSummaryDE;

/**
 * Created by Administrator on 2016/12/2.
 * 锻炼点数显示实体类
 */

public class PointDetailListAE {
    public int showType;
    public String dayScope;
    public int value ;
    public String unit;
    public int maxPointValue;
    public int targetPointValue;
    public DayMotionSummaryDE dayPoint;//步数对象
    public List<DayMotionSummaryDE> dayPointList;//图表列表


    public PointDetailListAE(int showType, String dayScope, int value, String unit, int maxPointValue,
                             int targetPointValue, DayMotionSummaryDE dayPoint, List<DayMotionSummaryDE> dayPointList) {
        this.showType = showType;
        this.dayScope = dayScope;
        this.value = value;
        this.unit = unit;
        this.maxPointValue = maxPointValue;
        this.targetPointValue = targetPointValue;
        this.dayPoint = dayPoint;
        this.dayPointList = dayPointList;
    }

    public PointDetailListAE() {
    }
}
