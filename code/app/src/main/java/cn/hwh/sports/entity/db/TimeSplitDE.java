package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/24.
 * 时间split数据库
 */
@Table(name = "timeSplit")
public class TimeSplitDE {

    @Column(name = "timeSplitId", isId = true, autoGen = false)
    public long timeSplitId;
    @Column(name = "workoutStartTime")
    public String workoutStartTime;//运动ID
    @Column(name = "timeIndex")
    public int timeIndex;//序号
    @Column(name = "avgHr")
    public int avgHr;//平均心率
    @Column(name = "status")
    public int status;
    @Column(name = "duration")
    public long duration;
    @Column(name = "userId")
    public int userId;
    @Column(name = "ownerId")
    public int ownerId;//workout所属用户ID

    public TimeSplitDE() {
    }

    public TimeSplitDE(long timeSplitId, String workoutStartTime, int timeIndex, int avgHr,
                       int status, long duration, int userId, int ownerId) {
        this.timeSplitId = timeSplitId;
        this.workoutStartTime = workoutStartTime;
        this.timeIndex = timeIndex;
        this.avgHr = avgHr;
        this.status = status;
        this.duration = duration;
        this.userId = userId;
        this.ownerId = ownerId;
    }
}
