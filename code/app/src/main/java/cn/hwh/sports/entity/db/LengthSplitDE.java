package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/24.
 * 距离split数据库
 */
@Table(name = "lengthSplit")
public class LengthSplitDE {

    @Column(name = "lengthSplitId", isId = true, autoGen = false)
    public long lengthSplitId;//本地自增长ID
    @Column(name = "splitIndex")
    public int splitIndex;// 序号
    @Column(name = "workoutStartTime")
    public String workoutStartTime;// 跑步历史名称
    @Column(name = "length")
    public float length;// 距离
    @Column(name = "duration")
    public long duration;// 用时
    @Column(name = "avgHr")
    public int avgHr;// 平均心率
    @Column(name = "minHeight")
    public double minHeight;// 最低海拔
    @Column(name = "maxHeight")
    public double maxHeight;// 最高海拔
    @Column(name = "latitude")
    public double latitude;// 纬度
    @Column(name = "longitude")
    public double longitude;// 经度
    @Column(name = "status")
    public int status;// 1正在跑,2 结束了
    @Column(name = "userId")
    public int userId;//用户ID
    @Column(name = "ownerId")
    public int ownerId;//workout所属用户ID


    public LengthSplitDE() {
    }

    public LengthSplitDE(long lengthSplitId, int splitIndex, String workoutStartTime, float length,
                         long duration, int avgHr, double minHeight, double maxHeight, double latitude,
                         double longitude, int status, int userId, int ownerId) {
        this.lengthSplitId = lengthSplitId;
        this.splitIndex = splitIndex;
        this.workoutStartTime = workoutStartTime;
        this.length = length;
        this.duration = duration;
        this.avgHr = avgHr;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.userId = userId;
    }
}