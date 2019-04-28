package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2017/4/13.
 */

public class LoginWithWxRE {


    /**
     * id : 14767
     * name : 13546643591
     * weight : 0
     * height : 170
     * age : 25
     * registerdate : 2016-04-11 06:22:17
     * nickname : 侯哥
     * avatar : http://wx.qlogo.cn/mmopen/TTQibyKjrickxxUX7ia2PwiasxprOopGblTQaDCdPspibf6wyIiaQLPmBkB0yfd7XdZBxBsTicF06Qr9UsTGU0ibPW5niaHYibua52icbsZ/0
     * gender : 1
     * birthdate : 1980-01-01
     * updatetime : 2016-04-11 06:22:52
     * location : 310100
     * locationprovince : 山西
     * locationcity : 晋中
     * livingroomvisits : 3
     * livingroomwatchminutes : 0
     * clockcount : 3
     * qrcode : 123Go://userid/14767
     * passwordisblank : 1
     * mobile : 13546643591
     * weixinnickname : 侯哥
     * thirdpartyaccount : 1
     * sessionid : 3007d24d5d94220a21895d4e5bd10d97372209b9
     * isnewuser : 0
     * maxhr : 182
     * resthr : 60
     * targethrlow : 121
     * targethrhigh : 163
     * alerthr : 163
     * vo2max : 46.4
     * hrdevice : {"name":"","serialno":"","macaddr":""}
     */

    public int id;
    public String name;
    public float weight;
    public int height;
    public int age;
    public String registerdate;
    public String nickname;
    public String avatar;
    public int gender;
    public String birthdate;
    public String updatetime;
    public String location;
    public String locationprovince;
    public String locationcity;
    public String livingroomvisits;
    public int livingroomwatchminutes;
    public String clockcount;
    public String qrcode;
    public int passwordisblank;
    public String mobile;
    public String weixinnickname;
    public String thirdpartyaccount;
    public String sessionid;
    public int isnewuser;
    public int maxhr;
    public int resthr;
    public int targethrlow;
    public int targethrhigh;
    public int alerthr;
    public double vo2max;
    /**
     * name : 
     * serialno : 
     * macaddr : 
     */

    public HrdeviceEntity hrdevice;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(String registerdate) {
        this.registerdate = registerdate;
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


    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
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

    public String getLivingroomvisits() {
        return livingroomvisits;
    }

    public void setLivingroomvisits(String livingroomvisits) {
        this.livingroomvisits = livingroomvisits;
    }

    public int getLivingroomwatchminutes() {
        return livingroomwatchminutes;
    }

    public void setLivingroomwatchminutes(int livingroomwatchminutes) {
        this.livingroomwatchminutes = livingroomwatchminutes;
    }

    public String getClockcount() {
        return clockcount;
    }

    public void setClockcount(String clockcount) {
        this.clockcount = clockcount;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getPasswordisblank() {
        return passwordisblank;
    }

    public void setPasswordisblank(int passwordisblank) {
        this.passwordisblank = passwordisblank;
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

    public String getThirdpartyaccount() {
        return thirdpartyaccount;
    }

    public void setThirdpartyaccount(String thirdpartyaccount) {
        this.thirdpartyaccount = thirdpartyaccount;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public int getIsnewuser() {
        return isnewuser;
    }

    public void setIsnewuser(int isnewuser) {
        this.isnewuser = isnewuser;
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

    public double getVo2max() {
        return vo2max;
    }

    public void setVo2max(double vo2max) {
        this.vo2max = vo2max;
    }

    public HrdeviceEntity getHrdevice() {
        return hrdevice;
    }

    public void setHrdevice(HrdeviceEntity hrdevice) {
        this.hrdevice = hrdevice;
    }

    public static class HrdeviceEntity {
        public String name;
        public String serialno;
        public String macaddr;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSerialno() {
            return serialno;
        }

        public void setSerialno(String serialno) {
            this.serialno = serialno;
        }

        public String getMacaddr() {
            return macaddr;
        }

        public void setMacaddr(String macaddr) {
            this.macaddr = macaddr;
        }
    }
}
