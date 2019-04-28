package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/24.
 */
@Table(name = "lap")
public class LapDE {

    @Column(name = "lapId", isId = true, autoGen = false)
    public long lapId;//本地自增长ID
    @Column(name = "userId")
    public int userId;//用户ID
    @Column(name = "ownerId")
    public int ownerId;//workout所属用户ID
    @Column(name = "startTime")
    public String startTime;// lap 名
    @Column(name = "workoutStartTime")
    public String workoutStartTime;// 跑步历史名称
    @Column(name = "duration")
    public long duration;// 耗时
    @Column(name = "lapIndex")
    public long lapIndex;// lap序号
    @Column(name = "length")
    public float length;// 长度
    @Column(name = "status")
    public int status;// 状态


    public LapDE() {
    }

    public LapDE(long lapId, String startTime, String workoutStartTime, long duration,
                 long lapIndex, float length, int status, int userId, int ownerId) {
        this.lapId = lapId;
        this.startTime = startTime;
        this.workoutStartTime = workoutStartTime;
        this.duration = duration;
        this.lapIndex = lapIndex;
        this.length = length;
        this.status = status;
        this.userId = userId;
        this.ownerId = ownerId;
    }
}
