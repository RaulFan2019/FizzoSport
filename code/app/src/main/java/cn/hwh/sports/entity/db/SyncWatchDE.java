package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2017/2/6.
 */
@Table(name = "syncWatch")
public class SyncWatchDE {

    @Column(name = "id", isId = true)
    public long id;

    @Column(name = "info")
    public String info;//上传数据的内容
    @Column(name = "userId")
    public int userId;
    @Column(name = "workoutStartTime")
    public String workoutStartTime;//workoutStartTime

    public SyncWatchDE() {
    }
}
