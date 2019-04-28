package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2017/4/22.
 */

public class UpdateRE {

    public int versionCode;
    public String versionName;
    public String url;
    public String information;
    public String firmware_protocol_version;

    public UpdateRE() {
    }

    public UpdateRE(int versionCode, String versionName,
                    String url, String information, String firmware_protocol_version) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.url = url;
        this.information = information;
        this.firmware_protocol_version = firmware_protocol_version;
    }
}
