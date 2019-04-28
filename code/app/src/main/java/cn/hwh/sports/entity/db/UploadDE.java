package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/7/3.
 */
@Table(name = "upload")
public class UploadDE {

    @Column(name = "id", isId = true)
    public long id;

    @Column(name = "type")
    public int type;//上传内容类型
    @Column(name = "info")
    public String info;//上传数据的内容
    @Column(name = "userId")
    public int userId;
    @Column(name = "workoutStartTime")
    public String workoutStartTime;//本地workoutStartTime
    @Column(name = "deviceMac")
    public String deviceMac;//设备Mac地址
    @Column(name = "deviceName")
    public String deviceName;//设备名称

    public UploadDE() {
    }

}
