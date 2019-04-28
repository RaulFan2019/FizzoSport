package cn.hwh.sports.entity.adapter;

/**
 * Created by Raul.Fan on 2016/12/14.
 */

public class MonitorListAE {


    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public int type;//类型
    public int sectionPosition;//section位置
    public int listPosition;//list 位置

    public String weekString;//周文本
    public float weekValue;//周总计数据
    public float dayValue;
    public float targetValue;
    public String unit;
    public String date;
    public String preTip;//前缀说明文字

    public MonitorListAE(int type, int sectionPosition, int listPosition, float weekValue,
                         String weekString, String unit, float dayValue, float targetValue
                         ,String date ,String preTip) {
        this.type = type;
        this.sectionPosition = sectionPosition;
        this.listPosition = listPosition;
        this.weekValue = weekValue;
        this.weekString = weekString;
        this.unit = unit;
        this.dayValue = dayValue;
        this.targetValue = targetValue;
        this.date = date;
        this.preTip = preTip;
    }
}
