package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/24.
 */
@Table(name = "location")
public class LocationDE {

    @Column(name = "locationId", isId = true, autoGen = false)
    public long locationId;
    @Column(name = "workoutStartTime")
    public String workoutStartTime;//跑步历史名称
    @Column(name = "lapStartTime")
    public String lapStartTime;//Lap名称
    @Column(name = "latitude")
    public double latitude;//纬度
    @Column(name = "longitude")
    public double longitude;//经度
    @Column(name = "hAccuracy")
    public float hAccuracy;// 水平精度
    @Column(name = "height")
    public double height;// 海拔
    @Column(name = "vAccuracy")
    public float vAccuracy;// 垂直精度
    @Column(name = "timeOffSet")
    public long timeOffSet;//相对时间
    @Column(name = "pace")
    public int pace;// 秒/公里
    @Column(name = "userId")
    public int userId;//用户ID
    @Column(name = "ownerId")
    public int ownerId;//workout所属用户ID
    @Column(name = "hr")
    public int hr;
    @Column(name = "timeSplitId")
    public long timeSplitId;

    public LocationDE() {
    }

    public LocationDE(long locationId, String workoutStartTime, String lapStartTime, double latitude,
                      double longitude, float hAccuracy, double height, float vAccuracy,
                      long timeOffSet, int pace, int userId, int ownerId, int hr,long timeSplitId) {
        this.locationId = locationId;
        this.workoutStartTime = workoutStartTime;
        this.lapStartTime = lapStartTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hAccuracy = hAccuracy;
        this.height = height;
        this.vAccuracy = vAccuracy;
        this.timeOffSet = timeOffSet;
        this.pace = pace;
        this.userId = userId;
        this.ownerId = ownerId;
        this.hr = hr;
        this.timeSplitId = timeSplitId;
    }
}
