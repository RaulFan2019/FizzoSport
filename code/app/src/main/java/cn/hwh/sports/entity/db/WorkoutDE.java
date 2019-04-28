package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/24.
 */
@Table(name = "workout")
public class WorkoutDE {

    @Column(name = "workoutId", isId = true, autoGen = false)
    public long workoutId;//本地自增长ID
    @Column(name = "name")
    public String name;// 运动名称
    @Column(name = "startTime")
    public String startTime;// 开始时间
    @Column(name = "duration")
    public long duration;// 跑步所花时间
    @Column(name = "length")
    public float length;// 跑步距离
    @Column(name = "calorie")
    public float calorie;// 消耗卡路里
    @Column(name = "maxHeight")
    public double maxHeight;// 最高海拔
    @Column(name = "minHeight")
    public double minHeight;// 最低海拔
    @Column(name = "maxPace")
    public int maxPace;// 最大配速
    @Column(name = "minPace")
    public int minPace;// 最小配速
    @Column(name = "avgPace")
    public int avgPace;// 平均配速
    @Column(name = "avgHr")
    public int avgHr;// 平均心率
    @Column(name = "maxHr")
    public int maxHr;// 最大心率
    @Column(name = "minHr")
    public int minHr;// 最小心率
    @Column(name = "userId")
    public int userId;//用户ID
    @Column(name = "ownerId")
    public int ownerId;//workout所属用户ID
    @Column(name = "startStep")
    public int startStep;
    @Column(name = "endStep")
    public int endStep;
    @Column(name = "status")
    public int status;// 跑步状态 1. 正在跑 , 2. 跑步结束
    @Column(name = "type")
    public int type;//运动类型 1.室内健身，2.室外跑步
    @Column(name = "effortPoint")
    public int effortPoint;//锻炼点数
    @Column(name = "targetType")
    public int targetType;//锻炼目标
    @Column(name = "targetTime")
    public int targetTime;//锻炼目标时长
    @Column(name = "feel")
    public int feel;//感受


    public int totalHrSize;

    public WorkoutDE() {

    }

    public WorkoutDE(long workoutId, String name, String startTime, long duration,
                     float length, float calorie, double maxHeight, double minHeight, int maxPace,
                     int minPace, int avgPace, int avgHr, int maxHr, int minHr, int userId,
                     int ownerId, int startStep, int endStep, int status, int type, int effortPoint,
                     int targetType, int targetTime,int feel) {
        this.workoutId = workoutId;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.length = length;
        this.calorie = calorie;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.maxPace = maxPace;
        this.minPace = minPace;
        this.avgPace = avgPace;
        this.avgHr = avgHr;
        this.maxHr = maxHr;
        this.minHr = minHr;
        this.userId = userId;
        this.startStep = startStep;
        this.endStep = endStep;
        this.status = status;
        this.type = type;
        this.effortPoint = effortPoint;
        this.ownerId = ownerId;
        this.targetType = targetType;
        this.targetTime = targetTime;
        this.feel = feel;
    }
}
