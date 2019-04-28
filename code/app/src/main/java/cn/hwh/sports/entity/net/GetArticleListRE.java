package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2017/3/9.
 */

public class GetArticleListRE {


    /**
     * id : 1
     * type : 4
     * url : http://mp.weixin.qq.com/s/uXGHg52FFGS28C84jmrKCQ
     * registertime : 2017-03-08 00:00:00
     */

    public int id;
    public int type;
    public String url;
    public String title;
    public String registertime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegistertime() {
        return registertime;
    }

    public void setRegistertime(String registertime) {
        this.registertime = registertime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
