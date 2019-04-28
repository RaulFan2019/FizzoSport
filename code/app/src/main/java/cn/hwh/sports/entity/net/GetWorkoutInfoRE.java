package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/21.
 */

public class GetWorkoutInfoRE {

    /**
     * id : 4168665
     * name : 2016-11-08 10:51:00
     * starttime : 2016-11-08 10:51:00
     * duration : 300
     * type : 3
     * resource : 6
     * status : 2
     * users_id : 14962
     * nickname : zhaoyuan11
     * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-11-11_58252a3db7605.jpg
     * gender : 1
     * birthdate : 1992-04-07
     * storename : 123测试门店
     * workoutnumber : 2
     * week_target_days : 5
     * week_exercised_days : 2
     * maxheartrate : 122
     * minheartrate : 54
     * avgheartrate : 106
     * program_name : 自由训练
     * program_id : 0
     * target_avg_effort : 0
     * calorie : 22
     * stepcount : 0
     * avg_effort : 54
     * effort_point : 5
     * conformity : 0
     * hz_zones : [{"hr_zone":0,"minutes":1,"effort_point":0},{"hr_zone":1,"minutes":3,"effort_point":3},{"hr_zone":2,"minutes":1,"effort_point":2},{"hr_zone":3,"minutes":0,"effort_point":0},{"hr_zone":4,"minutes":0,"effort_point":0},{"hr_zone":5,"minutes":0,"effort_point":0}]
     * splits : [{"timeoffset":0,"duration":60,"avgheartrate":91,"id":0,"avg_effort":46,"hr_zone":0,"effort_point":0,"calorie":3},{"timeoffset":60,"duration":60,"avgheartrate":117,"id":1,"avg_effort":60,"hr_zone":2,"effort_point":2,"calorie":5},{"timeoffset":120,"duration":60,"avgheartrate":110,"id":2,"avg_effort":56,"hr_zone":1,"effort_point":1,"calorie":4},{"timeoffset":180,"duration":60,"avgheartrate":109,"id":3,"avg_effort":56,"hr_zone":1,"effort_point":1,"calorie":4},{"timeoffset":240,"duration":60,"avgheartrate":104,"id":4,"avg_effort":53,"hr_zone":1,"effort_point":1,"calorie":4}]
     */

    public long id;
    public String name;
    public String starttime;
    public int duration;
    public int type;
    public int resource;
    public int status;
    public int users_id;
    public String nickname;
    public String avatar;
    public int gender;
    public String birthdate;
    public String storename;
    public int workoutnumber;
    public int week_target_days;
    public int week_exercised_days;
    public int maxheartrate;
    public int minheartrate;
    public int avgheartrate;
    public String program_name;
    public int program_id;
    public int target_avg_effort;
    public int calorie;
    public int stepcount;
    public int avg_effort;
    public int effort_point;
    public int conformity;
    public int planned_goal;//锻炼目标类型
    public int planned_duration;//锻炼目标时间
    /**
     * hr_zone : 0
     * minutes : 1
     * effort_point : 0
     */

    public List<HzZonesEntity> hz_zones;
    /**
     * timeoffset : 0
     * duration : 60
     * avgheartrate : 91
     * id : 0
     * avg_effort : 46
     * hr_zone : 0
     * effort_point : 0
     * calorie : 3
     */

    public List<SplitsEntity> splits;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public int getWorkoutnumber() {
        return workoutnumber;
    }

    public void setWorkoutnumber(int workoutnumber) {
        this.workoutnumber = workoutnumber;
    }

    public int getWeek_target_days() {
        return week_target_days;
    }

    public void setWeek_target_days(int week_target_days) {
        this.week_target_days = week_target_days;
    }

    public int getWeek_exercised_days() {
        return week_exercised_days;
    }

    public void setWeek_exercised_days(int week_exercised_days) {
        this.week_exercised_days = week_exercised_days;
    }

    public int getMaxheartrate() {
        return maxheartrate;
    }

    public void setMaxheartrate(int maxheartrate) {
        this.maxheartrate = maxheartrate;
    }

    public int getMinheartrate() {
        return minheartrate;
    }

    public void setMinheartrate(int minheartrate) {
        this.minheartrate = minheartrate;
    }

    public int getAvgheartrate() {
        return avgheartrate;
    }

    public void setAvgheartrate(int avgheartrate) {
        this.avgheartrate = avgheartrate;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public int getProgram_id() {
        return program_id;
    }

    public void setProgram_id(int program_id) {
        this.program_id = program_id;
    }

    public int getTarget_avg_effort() {
        return target_avg_effort;
    }

    public void setTarget_avg_effort(int target_avg_effort) {
        this.target_avg_effort = target_avg_effort;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getStepcount() {
        return stepcount;
    }

    public void setStepcount(int stepcount) {
        this.stepcount = stepcount;
    }

    public int getAvg_effort() {
        return avg_effort;
    }

    public void setAvg_effort(int avg_effort) {
        this.avg_effort = avg_effort;
    }

    public int getEffort_point() {
        return effort_point;
    }

    public void setEffort_point(int effort_point) {
        this.effort_point = effort_point;
    }

    public int getConformity() {
        return conformity;
    }

    public void setConformity(int conformity) {
        this.conformity = conformity;
    }

    public List<HzZonesEntity> getHz_zones() {
        return hz_zones;
    }

    public void setHz_zones(List<HzZonesEntity> hz_zones) {
        this.hz_zones = hz_zones;
    }

    public List<SplitsEntity> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitsEntity> splits) {
        this.splits = splits;
    }

    public static class HzZonesEntity {
        public int hr_zone;
        public int minutes;
        public int effort_point;

        public int getHr_zone() {
            return hr_zone;
        }

        public void setHr_zone(int hr_zone) {
            this.hr_zone = hr_zone;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }
    }

    public static class SplitsEntity {
        public int timeoffset;
        public int duration;
        public int avgheartrate;
        public int id;
        public int avg_effort;
        public int hr_zone;
        public int effort_point;
        public int calorie;

        public int getTimeoffset() {
            return timeoffset;
        }

        public void setTimeoffset(int timeoffset) {
            this.timeoffset = timeoffset;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getAvgheartrate() {
            return avgheartrate;
        }

        public void setAvgheartrate(int avgheartrate) {
            this.avgheartrate = avgheartrate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAvg_effort() {
            return avg_effort;
        }

        public void setAvg_effort(int avg_effort) {
            this.avg_effort = avg_effort;
        }

        public int getHr_zone() {
            return hr_zone;
        }

        public void setHr_zone(int hr_zone) {
            this.hr_zone = hr_zone;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }
    }
}
