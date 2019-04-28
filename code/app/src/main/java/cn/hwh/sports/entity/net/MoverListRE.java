package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/11/30.
 */

public class MoverListRE {


    /**
     * id : 14932
     * name : 12300169098
     * nickname : B169098
     * avatar :
     * gender : 1
     * setupdate : 2016-11-28
     */

    public List<MoversEntity> movers;

    public List<MoversEntity> getMovers() {
        return movers;
    }

    public void setMovers(List<MoversEntity> movers) {
        this.movers = movers;
    }

    public static class MoversEntity {
        public int id;
        public String name;
        public String nickname;
        public String avatar;
        public int gender;
        public String setupdate;

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

        public String getSetupdate() {
            return setupdate;
        }

        public void setSetupdate(String setupdate) {
            this.setupdate = setupdate;
        }
    }
}
