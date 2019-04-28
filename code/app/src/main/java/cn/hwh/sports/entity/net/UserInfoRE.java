package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Administrator on 2016/8/3.
 */
public class UserInfoRE {

    public int id;//用户ID
    public String name;//用户名
    public String nickname;//昵称
    public float weight;//体重
    public int height;//身高
    public int age;//年龄
    public String registerdate;//注册
    public String avatar;//头像
    public int gender;//性别
    public String birthdate;//生日
    public Object updatetime;//保留字段
    public String location;//位置编码
    public String locationprovince;//省
    public String locationcity;//市
    public String sessionid;
    public String mobile;  //手机号码
    public String weixinnickname;//微信昵称
    public int passwordisblank;//密码是否为空
    public String qrcode;//二维码
    public int thirdpartyaccount;//是否绑定第三方微信
    public int maxhr;
    public int resthr;
    public float vo2max;
    public int targethrlow;//靶心率下限
    public int targethrhigh;//靶心率上限
    public int alerthr;//警报心率
    public int finishedworkout;//已完成的锻炼总数
    public int monthexerciseddays;//本月锻炼天数
    public bodyTargetEntity characteristic_target;

    public List<Roles> roles;

    public int getId() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(String registerdate) {
        this.registerdate = registerdate;
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

    public Object getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Object updatetime) {
        this.updatetime = updatetime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationprovince() {
        return locationprovince;
    }

    public void setLocationprovince(String locationprovince) {
        this.locationprovince = locationprovince;
    }

    public String getLocationcity() {
        return locationcity;
    }

    public void setLocationcity(String locationcity) {
        this.locationcity = locationcity;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWeixinnickname() {
        return weixinnickname;
    }

    public void setWeixinnickname(String weixinnickname) {
        this.weixinnickname = weixinnickname;
    }

    public int getPasswordisblank() {
        return passwordisblank;
    }

    public void setPasswordisblank(int passwordisblank) {
        this.passwordisblank = passwordisblank;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getThirdpartyaccount() {
        return thirdpartyaccount;
    }

    public void setThirdpartyaccount(int thirdpartyaccount) {
        this.thirdpartyaccount = thirdpartyaccount;
    }

    public int getMaxhr() {
        return maxhr;
    }

    public void setMaxhr(int maxhr) {
        this.maxhr = maxhr;
    }

    public int getResthr() {
        return resthr;
    }

    public void setResthr(int resthr) {
        this.resthr = resthr;
    }

    public HrdeviceEntity getHrdevice() {
        return hrdevice;
    }

    public void setHrdevice(HrdeviceEntity hrdevice) {
        this.hrdevice = hrdevice;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public float getVo2max() {
        return vo2max;
    }

    public void setVo2max(float vo2max) {
        this.vo2max = vo2max;
    }

    public int getTargethrlow() {
        return targethrlow;
    }

    public void setTargethrlow(int targethrlow) {
        this.targethrlow = targethrlow;
    }

    public int getTargethrhigh() {
        return targethrhigh;
    }

    public void setTargethrhigh(int targethrhigh) {
        this.targethrhigh = targethrhigh;
    }

    public int getAlerthr() {
        return alerthr;
    }

    public void setAlerthr(int alerthr) {
        this.alerthr = alerthr;
    }

    public daily_target getDaily_target() {
        return daily_target;
    }

    public void setDaily_target(daily_target daily_target) {
        this.daily_target = daily_target;
    }

    public int getFinishedworkout() {
        return finishedworkout;
    }

    public void setFinishedworkout(int finishedworkout) {
        this.finishedworkout = finishedworkout;
    }

    public int getMonthexerciseddays() {
        return monthexerciseddays;
    }

    public void setMonthexerciseddays(int monthexerciseddays) {
        this.monthexerciseddays = monthexerciseddays;
    }

    /**
     * name : 123sport0001
     * macaddr : A0:E6:F8:2F:3D:47
     */

    public HrdeviceEntity hrdevice;

    public static class HrdeviceEntity {
        public String name;
        public String macaddr;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMacaddr() {
            return macaddr;
        }

        public void setMacaddr(String macaddr) {
            this.macaddr = macaddr;
        }
    }

    public static class bodyTargetEntity{
        public float weight;
        public float fatrate;

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public float getFatrate() {
            return fatrate;
        }

        public void setFatrate(float fatrate) {
            this.fatrate = fatrate;
        }
    }

    public bodyTargetEntity getCharacteristic_target() {
        return characteristic_target;
    }

    public void setCharacteristic_target(bodyTargetEntity characteristic_target) {
        this.characteristic_target = characteristic_target;
    }

    public static class Roles{
        public int role;
        public String rolename;

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public String getRolename() {
            return rolename;
        }

        public void setRolename(String rolename) {
            this.rolename = rolename;
        }
    }

    public daily_target daily_target;

    public static class daily_target{

        /**
         * stepcount : 10000
         * length : 5000
         * calorie : 300
         * effort_point : 90
         * exercise_minutes : 30
         * sleep_minutes : 480
         */

        public int stepcount;
        public int length;
        public int calorie;
        public int effort_point;
        public int exercise_minutes;
        public int sleep_minutes;

        public int getStepcount() {
            return stepcount;
        }

        public void setStepcount(int stepcount) {
            this.stepcount = stepcount;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }

        public int getExercise_minutes() {
            return exercise_minutes;
        }

        public void setExercise_minutes(int exercise_minutes) {
            this.exercise_minutes = exercise_minutes;
        }

        public int getSleep_minutes() {
            return sleep_minutes;
        }

        public void setSleep_minutes(int sleep_minutes) {
            this.sleep_minutes = sleep_minutes;
        }
    }
}
