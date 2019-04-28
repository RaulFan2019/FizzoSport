package cn.hwh.sports.entity.adapter;

import java.util.List;

import cn.hwh.sports.entity.db.DayMotionSummaryDE;

/**
 * Created by Administrator on 2016/12/2.
 * 卡路里列表显示实体类
 */

public class CalorieDetailListAE {
    public int showType;
    public String dayScope;
    public int value ;
    public String unit;
    public int maxCalorieValue;
    public int targetCalorieValue;
    public DayMotionSummaryDE dayCalorie;//步数对象
    public List<DayMotionSummaryDE> dayCalorieList;//图表列表

    public CalorieDetailListAE(int showType, String dayScope, int value, String unit, int maxCalorieValue,
                               int targetCalorieValue, DayMotionSummaryDE dayCalorie, List<DayMotionSummaryDE> dayCalorieList) {
        this.showType = showType;
        this.dayScope = dayScope;
        this.value = value;
        this.unit = unit;
        this.maxCalorieValue = maxCalorieValue;
        this.targetCalorieValue = targetCalorieValue;
        this.dayCalorie = dayCalorie;
        this.dayCalorieList = dayCalorieList;
    }

    public CalorieDetailListAE() {
    }
}
