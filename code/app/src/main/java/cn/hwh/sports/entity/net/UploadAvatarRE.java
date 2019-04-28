package cn.hwh.sports.entity.net;

/**
 * Created by Raul on 2015/11/16.
 * 获取上传头像地址
 */
public class UploadAvatarRE {


    /**
     * url : http://7xk0si.com1.z0.glb.clouddn.com/2015-11-16_56497111b44cd.png
     */

    public String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public UploadAvatarRE() {

    }

    public UploadAvatarRE(String url) {
        super();
        this.url = url;
    }
}
