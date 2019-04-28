package cn.hwh.sports.entity.model;

/**
 * Created by Raul.Fan on 2017/4/10.
 */

public class WxUserInfo {

    public String unionid;//微信对外ID
    public String screen_name;//用户昵称
    public String gender;//用户性别
    public String iconurl;//用户头像
    public String openid;//开放id

    public WxUserInfo(String unionid, String screen_name, String gender, String iconurl, String openid) {
        this.unionid = unionid;
        this.screen_name = screen_name;
        this.gender = gender;
        this.iconurl = iconurl;
        this.openid = openid;
    }
}
