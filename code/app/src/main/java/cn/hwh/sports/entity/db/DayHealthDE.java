package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2017/4/19.
 */
@Table(name = "DayHealth")
public class DayHealthDE {

    @Column(name = "dayHealthId", isId = true, autoGen = false)
    public long dayHealthId;
    @Column(name = "userId")
    public int userId;
    @Column(name = "date")
    public String date;
    @Column(name = "stepCount")
    public int stepCount;
    @Column(name = "exercisedCalorie")
    public int exercisedCalorie;//锻炼卡路里
    @Column(name = "updateTime")
    public String updateTime;
    @Column(name = "exercisedMinutes")
    public int exercisedMinutes;//锻炼时间
    @Column(name = "effectiveMinutes")
    public int effectiveMinutes;//有效时间

    public DayHealthDE() {
    }

    public DayHealthDE(long dayHealthId,int userId, String date, int stepCount, int exercisedCalorie,
                       String updateTime, int exercisedMinutes, int effectiveMinutes) {
        this.dayHealthId = dayHealthId;
        this.userId = userId;
        this.date = date;
        this.stepCount = stepCount;
        this.exercisedCalorie = exercisedCalorie;
        this.updateTime = updateTime;
        this.exercisedMinutes = exercisedMinutes;
        this.effectiveMinutes = effectiveMinutes;
    }
}
