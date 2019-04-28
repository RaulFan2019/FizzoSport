package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2016/11/29.
 */
@Table(name = "workoutSync")
public class WorkoutSyncDE {

    @Column(name = "syncId", isId = true, autoGen = false)
    public long syncId;//同步ID

    @Column(name = "workoutStartTime")
    public String workoutStartTime;//本地sportStartTime;
    @Column(name = "serviceId")
    public long serviceId;//服务器ID
    @Column(name = "ownerId")
    public int ownerId;//所有者ID

    public WorkoutSyncDE() {

    }

    public WorkoutSyncDE(long syncId, String workoutStartTime, long serviceId,int ownerId) {
        this.syncId = syncId;
        this.workoutStartTime = workoutStartTime;
        this.serviceId = serviceId;
        this.ownerId = ownerId;
    }
}
