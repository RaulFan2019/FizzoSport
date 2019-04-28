package cn.hwh.sports.entity.event;

/**
 * Created by Raul.Fan on 2016/11/24.
 * 跑步时间更新事件
 */
public class RunningTimeEE {

    public long time;//跑步时间
    public float length;//距离
    public int stepCount;

    public RunningTimeEE() {
    }

    public RunningTimeEE(long time) {
        this.time = time;
    }

    public RunningTimeEE(long time, float length,int stepCount ) {
        this.time = time;
        this.length = length;
        this.stepCount =stepCount;
    }
}
