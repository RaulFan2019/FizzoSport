package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/19.
 */

public class GetUserWorkoutListRE {


    /**
     * id : 4169072
     * starttime : 2016-12-16 16:21:00
     * duration : 180
     * type : 3
     * resource : 6
     * status : 2
     * userid : 10182
     * nickname : shenzhou
     * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-11-11_58256a3930762.jpg
     * gender : 1
     * birthdate : 1976-04-08
     * maxheartrate : 62
     * minheartrate : 43
     * avgheartrate : 56
     * calorie : 3
     * stepcount : 0
     * week_starttime : 2016-12-12 00:00:00
     * week_exercised_minutes : 759
     * name : 室内健身
     * avg_effort : 29
     * effort_point : 0
     * hz_zones : [{"hr_zone":0,"minutes":3,"effort_point":0},{"hr_zone":1,"minutes":0,"effort_point":0},{"hr_zone":2,"minutes":0,"effort_point":0},{"hr_zone":3,"minutes":0,"effort_point":0},{"hr_zone":4,"minutes":0,"effort_point":0},{"hr_zone":5,"minutes":0,"effort_point":0}]
     * length : 0
     * maxheight : 0
     * minheight : 0
     * maxpace : 0
     * minpace : 0
     * avgpace : 0
     * staticmap :
     */

    public List<WorkoutEntity> workout;

    public List<WorkoutEntity> getWorkout() {
        return workout;
    }

    public void setWorkout(List<WorkoutEntity> workout) {
        this.workout = workout;
    }

    public static class WorkoutEntity {
        public int id;//workout id
        public String starttime;//workout 开始时间
        public int duration;//持续时间
        public int type;//记录类型 1-跑步, 2-走路, 3-健身
        public int resource;//1-第五区App，5-心率表，6-心率接收终端
        public int status;//1-锻炼中,2-已结束
        public int userid;
        public String nickname;
        public String avatar;
        public int gender;
        public String birthdate;
        public int maxheartrate;
        public int minheartrate;
        public int avgheartrate;
        public int calorie;
        public int stepcount;
        public String week_starttime;//锻炼记录所在周开始时间
        public int week_exercised_minutes;//锻炼记录所在周的锻炼分钟数
        public String name;
        public int avg_effort;
        public int effort_point;
        public int length;
        public int maxheight;
        public int minheight;
        public int maxpace;
        public int minpace;
        public int avgpace;
        public String staticmap;
        /**
         * hr_zone : 0
         * minutes : 3
         * effort_point : 0
         */

        public List<HzZonesEntity> hz_zones;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
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

        public String getWeek_starttime() {
            return week_starttime;
        }

        public void setWeek_starttime(String week_starttime) {
            this.week_starttime = week_starttime;
        }

        public int getWeek_exercised_minutes() {
            return week_exercised_minutes;
        }

        public void setWeek_exercised_minutes(int week_exercised_minutes) {
            this.week_exercised_minutes = week_exercised_minutes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getMaxheight() {
            return maxheight;
        }

        public void setMaxheight(int maxheight) {
            this.maxheight = maxheight;
        }

        public int getMinheight() {
            return minheight;
        }

        public void setMinheight(int minheight) {
            this.minheight = minheight;
        }

        public int getMaxpace() {
            return maxpace;
        }

        public void setMaxpace(int maxpace) {
            this.maxpace = maxpace;
        }

        public int getMinpace() {
            return minpace;
        }

        public void setMinpace(int minpace) {
            this.minpace = minpace;
        }

        public int getAvgpace() {
            return avgpace;
        }

        public void setAvgpace(int avgpace) {
            this.avgpace = avgpace;
        }

        public String getStaticmap() {
            return staticmap;
        }

        public void setStaticmap(String staticmap) {
            this.staticmap = staticmap;
        }

        public List<HzZonesEntity> getHz_zones() {
            return hz_zones;
        }

        public void setHz_zones(List<HzZonesEntity> hz_zones) {
            this.hz_zones = hz_zones;
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
    }
}
