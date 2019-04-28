package cn.hwh.sports.entity.adapter;

import java.util.List;

import cn.hwh.sports.entity.model.MonitorListHeadME;

/**
 * Created by Raul.Fan on 2016/12/23.
 */

public class MainCoachChartAE {

    public String title;
    public int maxValue;//最大值
    public List<ColumnDay> dayList;//一周数据


    public static class ColumnDay{
        public String weekDay;
        public int value;

        public ColumnDay(String weekDay, int value) {
            this.weekDay = weekDay;
            this.value = value;
        }
    }

    public MainCoachChartAE(String title, int maxValue,List<ColumnDay> dayList) {
        this.title = title;
        this.maxValue = maxValue;
        this.dayList = dayList;
    }
}
