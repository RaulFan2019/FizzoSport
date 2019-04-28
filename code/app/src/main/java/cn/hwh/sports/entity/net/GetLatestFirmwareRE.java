package cn.hwh.sports.entity.net;

/**
 * Created by Raul.Fan on 2017/1/6.
 */

public class GetLatestFirmwareRE {


    /**
     * id : 2
     * name : 0.8.3
     * description : 算法更新了参数。
     * ftpurl : /home/sftp/firware/2017-01-05/1483617643_oadvS332VitaOTA.zip
     * size : 97526
     * date : 2017-01-05 20:00:43
     * md5sum : bf5d200c3d77458650f87496073d02af
     * type : 2
     */

    public int id;
    public String name;
    public String description;
    public String ftpurl;
    public int size;
    public String date;
    public String md5sum;
    public int type;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFtpurl() {
        return ftpurl;
    }

    public void setFtpurl(String ftpurl) {
        this.ftpurl = ftpurl;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
