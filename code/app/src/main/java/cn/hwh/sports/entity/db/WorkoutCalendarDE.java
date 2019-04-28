package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2017/5/12.
 */
@Table(name = "workoutCalendar")
public class WorkoutCalendarDE {

    @Column(name = "id", isId = true)
    public long id;
    @Column(name = "userId")
    public int userId;
    @Column(name = "day")
    public String day;
    @Column(name = "data")
    public String data;

    public WorkoutCalendarDE() {
    }

    public WorkoutCalendarDE(int userId, String day, String data) {
        this.userId = userId;
        this.day = day;
        this.data = data;
    }
}
