package cn.hwh.sports.entity.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Raul.Fan on 2017/2/22.
 */

public class GetWorkoutDetailInfoRE {


    /**
     * id : 4168665
     * name : 2016-11-08 10:51:00
     * starttime : 2016-11-08 10:51:00
     * duration : 300
     * type : 3
     * planned_goal : 0
     * planned_duration : 0
     * resource : 6
     * status : 2
     * users_id : 14962
     * nickname : zhaoyuan11
     * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-11-11_58252a3db7605.jpg
     * gender : 1
     * birthdate : 1992-04-07
     * storename :       FIZZO         测试门店    
     * workoutnumber : 2
     * week_target_days : 5
     * week_exercised_days : 1
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
     * length : 0
     * maxheight : 0
     * minheight : 0
     * maxpace : 0
     * minpace : 0
     * avgpace : 0
     * staticmap : 
     * hz_zones : [{"hr_zone":0,"minutes":1,"effort_point":0},{"hr_zone":1,"minutes":3,"effort_point":3},{"hr_zone":2,"minutes":1,"effort_point":2},{"hr_zone":3,"minutes":0,"effort_point":0},{"hr_zone":4,"minutes":0,"effort_point":0},{"hr_zone":5,"minutes":0,"effort_point":0}]
     * splits : [{"timeoffset":0,"duration":60,"avgheartrate":91,"id":0,"avg_effort":46,"hr_zone":0,"effort_point":0,"calorie":3},{"timeoffset":60,"duration":60,"avgheartrate":117,"id":1,"avg_effort":60,"hr_zone":2,"effort_point":2,"calorie":5},{"timeoffset":120,"duration":60,"avgheartrate":110,"id":2,"avg_effort":56,"hr_zone":1,"effort_point":1,"calorie":4},{"timeoffset":180,"duration":60,"avgheartrate":109,"id":3,"avg_effort":56,"hr_zone":1,"effort_point":1,"calorie":4},{"timeoffset":240,"duration":60,"avgheartrate":104,"id":4,"avg_effort":53,"hr_zone":1,"effort_point":1,"calorie":4}]
     * kmsplits : []
     * bpms : [{"timeoffset":17,"bpm":54,"stridefrequency":0},{"timeoffset":19,"bpm":58,"stridefrequency":0}]
     *
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
    public int length;
    public int maxheight;
    public int minheight;
    public int maxpace;
    public int minpace;
    public int avgpace;
    public String staticmap;
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
    public List<KmsplitsEntity> kmsplits;
    /**
     * timeoffset : 17
     * bpm : 54
     * stridefrequency : 0
     */

    public List<BpmsEntity> bpms;
    public List<SessionEntity> session;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getPlanned_goal() {
        return planned_goal;
    }

    public void setPlanned_goal(int planned_goal) {
        this.planned_goal = planned_goal;
    }

    public int getPlanned_duration() {
        return planned_duration;
    }

    public void setPlanned_duration(int planned_duration) {
        this.planned_duration = planned_duration;
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

    public List<SplitsEntity> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitsEntity> splits) {
        this.splits = splits;
    }

    public List<KmsplitsEntity> getKmsplits() {
        return kmsplits;
    }

    public void setKmsplits(List<KmsplitsEntity> kmsplits) {
        this.kmsplits = kmsplits;
    }

    public List<BpmsEntity> getBpms() {
        return bpms;
    }

    public void setBpms(List<BpmsEntity> bpms) {
        this.bpms = bpms;
    }

    public List<SessionEntity> getSession() {
        return session;
    }

    public void setSession(List<SessionEntity> session) {
        this.session = session;
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

    public static class BpmsEntity {
        public int timeoffset;
        public int bpm;
        public int stridefrequency;

        public int getTimeoffset() {
            return timeoffset;
        }

        public void setTimeoffset(int timeoffset) {
            this.timeoffset = timeoffset;
        }

        public int getBpm() {
            return bpm;
        }

        public void setBpm(int bpm) {
            this.bpm = bpm;
        }

        public int getStridefrequency() {
            return stridefrequency;
        }

        public void setStridefrequency(int stridefrequency) {
            this.stridefrequency = stridefrequency;
        }
    }

    public static class SessionEntity {
        /**
         * starttime : 2017-06-04 14:20:00
         * lap : 0
         * duration : 0
         * length : 0
         * status : 1
         * locationdata : [{"latitude":"31.443459","longitude":"121.110355","haccuracy":"5","altitude":"11.475891","vaccuracy":"4","speed":"0","bpm":"123","timeoffset":"0"},{"latitude":"31.443479","longitude":"121.110392","haccuracy":"25","altitude":"11.523132","vaccuracy":"4","speed":"0","bpm":"127","timeoffset":"2"},{"latitude":"31.443506","longitude":"121.110431","haccuracy":"25","altitude":"11.629395","vaccuracy":"3","speed":"1000","bpm":"130","timeoffset":"4"}]
         */

        public String starttime;
        public int lap;
        public int duration;
        public float length;
        public int status;
        public List<LocationdataEntity> locationdata;

        public int getLap() {
            return lap;
        }

        public void setLap(int lap) {
            this.lap = lap;
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

        public float getLength() {
            return length;
        }

        public void setLength(float length) {
            this.length = length;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<LocationdataEntity> getLocationdata() {
            return locationdata;
        }

        public void setLocationdata(List<LocationdataEntity> locationdata) {
            this.locationdata = locationdata;
        }

        public static class LocationdataEntity {
            /**
             * latitude : 31.443459
             * longitude : 121.110355
             * haccuracy : 5
             * altitude : 11.475891
             * vaccuracy : 4
             * speed : 0
             * bpm : 123
             * timeoffset : 0
             */

            public double latitude;
            public double longitude;
            public float haccuracy;
            public double altitude;
            public float vaccuracy;
            public int speed;
            public int bpm;
            public long timeoffset;

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public float getHaccuracy() {
                return haccuracy;
            }

            public void setHaccuracy(float haccuracy) {
                this.haccuracy = haccuracy;
            }

            public double getAltitude() {
                return altitude;
            }

            public void setAltitude(double altitude) {
                this.altitude = altitude;
            }

            public float getVaccuracy() {
                return vaccuracy;
            }

            public void setVaccuracy(float vaccuracy) {
                this.vaccuracy = vaccuracy;
            }

            public int getSpeed() {
                return speed;
            }

            public void setSpeed(int speed) {
                this.speed = speed;
            }

            public int getBpm() {
                return bpm;
            }

            public void setBpm(int bpm) {
                this.bpm = bpm;
            }

            public long getTimeoffset() {
                return timeoffset;
            }

            public void setTimeoffset(long timeoffset) {
                this.timeoffset = timeoffset;
            }
        }
    }

    public static class KmsplitsEntity {
        /**
         * timeoffset : 0
         * length : 1000
         * duration : 346
         * avgheartrate : 0
         * minaltitude : 0
         * maxaltitude : 227.7
         * id : 0
         * latitude : 31.22060031467
         * longitude : 121.52538411458
         */

        public int timeoffset;
        public float length;
        public long duration;
        public int avgheartrate;
        public double minaltitude;
        public double maxaltitude;
        public int id;
        public double latitude;
        public double longitude;

        public int getTimeoffset() {
            return timeoffset;
        }

        public void setTimeoffset(int timeoffset) {
            this.timeoffset = timeoffset;
        }

        public float getLength() {
            return length;
        }

        public void setLength(float length) {
            this.length = length;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public int getAvgheartrate() {
            return avgheartrate;
        }

        public void setAvgheartrate(int avgheartrate) {
            this.avgheartrate = avgheartrate;
        }

        public double getMinaltitude() {
            return minaltitude;
        }

        public void setMinaltitude(double minaltitude) {
            this.minaltitude = minaltitude;
        }

        public double getMaxaltitude() {
            return maxaltitude;
        }

        public void setMaxaltitude(double maxaltitude) {
            this.maxaltitude = maxaltitude;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
