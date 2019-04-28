package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2017/1/3.
 */

public class GetWxShareWorkoutTextRE {


    /**
     * title : 落叶cc用Fizzo消耗了86大卡
     * text : 用时: 0:13:00
     消耗: 86大卡
     平均心率: 112
     * image : http://7xot4d.com1.z0.glb.clouddn.com/workout.png
     * link : http://www.123yd.cn/xiaomingwork/check_cookie_for_weixin_oauth_include.php?state=http%3A%2F%2Fwww.123yd.cn%2Ffizzoh5%2FtraineeFitDetailShare.html%3Fuserid%3D15041%26workoutid%3D4177751
     */

    public String title;
    public String text;
    public String image;
    public String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
