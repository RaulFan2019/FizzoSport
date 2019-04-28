package cn.hwh.sports.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/6/27.
 */
@Table(name = "user")
public class UserDE {

    @Column(name = "userId", isId = true, autoGen = false)
    public int userId;//存在服务器的用户ID
    @Column(name = "name")
    public String name;// 用户名称
    @Column(name = "sessionId")
    public String sessionId;//存在在服务器的该用户本次登录使用的session
    @Column(name = "weight")
    public float weight;// 体重(单位:公斤)
    @Column(name = "height")
    public int height;// 身高(单位:CM)
    @Column(name = "nickname")
    public String nickname;// 昵称
    @Column(name = "gender")
    public int gender;// 性别
    @Column(name = "birthday")
    public String birthday;// 生日
    @Column(name = "avatar")
    public String avatar;//头像地址
    @Column(name = "province")
    public String province;// 省名称
    @Column(name = "city")
    public String city;// 市名称
    @Column(name = "maxHr")
    public int maxHr;//最大心率
    @Column(name = "restHr")
    public int restHr;//静心心率
    @Column(name = "bleMac")
    public String bleMac;//设备mac地址
    @Column(name = "bleName")
    public String bleName;//心率设备名称
    @Column(name = "vo2max")
    public float vo2max;//最大摄氧量
    @Column(name = "registerDate")
    public String registerDate;
    @Column(name = "role")
    public int role;
    @Column(name = "targetStepCount")
    public int targetStepCount;//步数目标
    @Column(name = "targetLength")
    public int targetLength;//距离目标
    @Column(name = "targetCalorie")
    public int targetCalorie;//消耗目标
    @Column(name = "targetSportMinutes")
    public int targetSportMinutes;//运动时间目标
    @Column(name = "targetSleepMinutes")
    public int targetSleepMinutes;//睡眠时间目标
    @Column(name = "targetPoint")
    public int targetPoint;//目标锻炼点数
    @Column(name = "targetHrLow")
    public int targetHrLow;//靶心率下限
    @Column(name = "targetHrHigh")
    public int targetHrHigh;//靶心率上限
    @Column(name = "alertHr")
    public int alertHr;//报警心率
    @Column(name = "targetWeight")
    public float targetWeight;//目标体重
    @Column(name = "targetFatRate")
    public float targetFatRate;//目标体脂率
    @Column(name = "finishedWorkout")
    public int finishedWorkout;
    @Column(name = "workoutDayInMouth")
    public int workoutDayInMouth;

    public UserDE() {
    }

    public UserDE(int userId, String name, String sessionId, float weight, int height, String nickname,
                  int gender, String birthday, String avatar, String province, String city, int maxHr,
                  int restHr, String bleMac, String bleName, float vo2max, String registerDate,
                  int role, int targetStepCount, int targetLength, int targetPoint,
                  int targetCalorie, int targetSportMinutes, int targetSleepMinutes, int targetHrLow, int targetHrHigh,
                  int alertHr, float targetWeight, float targetFatRate,int finishedWorkout,int workoutDayInMouth) {
        this.userId = userId;
        this.name = name;
        this.sessionId = sessionId;
        this.weight = weight;
        this.height = height;
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.avatar = avatar;
        this.province = province;
        this.city = city;
        this.maxHr = maxHr;
        this.restHr = restHr;
        this.bleMac = bleMac;
        this.bleName = bleName;
        this.vo2max = vo2max;
        this.registerDate = registerDate;
        this.role = role;
        this.targetStepCount = targetStepCount;
        this.targetLength = targetLength;
        this.targetCalorie = targetCalorie;
        this.targetSportMinutes = targetSportMinutes;
        this.targetSleepMinutes = targetSleepMinutes;
        this.targetPoint = targetPoint;
        this.targetHrLow = targetHrLow;
        this.targetHrHigh = targetHrHigh;
        this.alertHr = alertHr;
        this.targetFatRate = targetFatRate;
        this.targetWeight = targetWeight;
        this.finishedWorkout = finishedWorkout;
        this.workoutDayInMouth = workoutDayInMouth;
    }
}
