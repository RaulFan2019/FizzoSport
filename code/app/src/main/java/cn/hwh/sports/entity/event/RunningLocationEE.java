package cn.hwh.sports.entity.event;

/**
 * Created by machenike on 2017/5/24 0024.
 */

public class RunningLocationEE {

    public float length;// 距离
    public int speed;// 配速

    public RunningLocationEE() {
    }

    public RunningLocationEE(float length, int speed) {
        this.length = length;
        this.speed = speed;
    }
}
