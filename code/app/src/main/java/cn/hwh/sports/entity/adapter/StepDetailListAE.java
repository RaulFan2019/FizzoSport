package cn.hwh.sports.entity.adapter;

import java.util.List;

import cn.hwh.sports.entity.db.DayMotionSummaryDE;
import cn.hwh.sports.entity.net.StepDetailListRE;

/**
 * Created by Administrator on 2016/11/25.
 * 步数数据 实体类
 */

public class StepDetailListAE {
    public int showType;
    public String dayScope;
    public int value ;
    public String unit;
    public int maxStepValue;
    public int targetStepValue;
    public DayMotionSummaryDE dayStep;//步数对象
    public List<DayMotionSummaryDE> dayStepList;//图表列表

    public StepDetailListAE(int showType, String dayScope, int value, String unit, int maxStepValue, int targetStepValue, DayMotionSummaryDE dayStep, List<DayMotionSummaryDE> dayStepList) {
        this.showType = showType;
        this.dayScope = dayScope;
        this.value = value;
        this.unit = unit;
        this.maxStepValue = maxStepValue;
        this.targetStepValue = targetStepValue;
        this.dayStep = dayStep;
        this.dayStepList = dayStepList;
    }

    public StepDetailListAE() {
    }


}
