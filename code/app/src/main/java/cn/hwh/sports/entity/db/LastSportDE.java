package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2017/4/19.
 */
@Table(name = "lastSport")
public class LastSportDE {

    @Column(name = "lastSportInfoId",isId = true, autoGen = false)
    public long lastSportInfoId;
    @Column(name = "userId")
    public int userId;
    @Column(name = "lastSportId")
    public int lastSportId;
    @Column(name = "lastSportType")
    public int lastSportType;
    @Column(name = "lastSportName")
    public String lastSportName;
    @Column(name = "lastSportStartTime")
    public String lastSportStartTime;
    @Column(name = "lastSportDuration")
    public int lastSportDuration;

    public LastSportDE() {
    }

    public LastSportDE(long lastSportInfoId, int userId, int lastSportId, int lastSportType,
                       String lastSportName, String lastSportStartTime, int lastSportDuration) {
        this.lastSportInfoId = lastSportInfoId;
        this.userId = userId;
        this.lastSportId = lastSportId;
        this.lastSportType = lastSportType;
        this.lastSportName = lastSportName;
        this.lastSportStartTime = lastSportStartTime;
        this.lastSportDuration = lastSportDuration;
    }
}
