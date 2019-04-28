package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/27.
 */
@Table(name = "hr")
public class HrDE {

    @Column(name = "hrId", isId = true, autoGen = false)
    public long hrId;//唯一ID
    @Column(name = "workoutStartTime")
    public String workoutStartTime;//运动开始时间
    @Column(name = "lapStartTime")
    public String lapStartTime;//session开始时间
    @Column(name = "timeOffSet")
    public int timeOffSet;//相对开始时间的偏移
    @Column(name = "lapTimeOffSet")
    public int lapTimeOffSet;//相对Lap开始时间的偏移
    @Column(name = "heartbeat")
    public int heartbeat;//心率
    @Column(name = "timeSplitId")
    public long timeSplitId;
    @Column(name = "lengthSplitId")
    public long lengthSplitId;
    @Column(name = "strideFreQuency")
    public int strideFreQuency;//步频

    public int actionType;
    public int actionCount;

    public HrDE() {
    }

    public HrDE(long hrId, String workoutStartTime, String lapStartTime, int timeOffSet,int lapTimeOffSet,
                int heartbeat, long timeSplitId, long lengthSplitId,int strideFreQuency) {
        this.hrId = hrId;
        this.workoutStartTime = workoutStartTime;
        this.lapStartTime = lapStartTime;
        this.timeOffSet = timeOffSet;
        this.lapTimeOffSet = lapTimeOffSet;
        this.heartbeat = heartbeat;
        this.timeSplitId = timeSplitId;
        this.lengthSplitId = lengthSplitId;
        this.strideFreQuency = strideFreQuency;
    }
}
