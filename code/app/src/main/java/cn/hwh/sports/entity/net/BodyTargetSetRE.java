package cn.hwh.sports.entity.net;

/**
 * Created by Administrator on 2016/11/22.
 */

public class BodyTargetSetRE {


    /**
     * weight_target : {"setuptime":"2016-11-20 15:18:17","weight":70.5}
     * fatrate_target : {"setuptime":"2016-11-20 15:18:17","fatrate":20.2}
     */

    private WeightTargetBean weight_target;
    private FatrateTargetBean fatrate_target;

    public WeightTargetBean getWeight_target() {
        return weight_target;
    }

    public void setWeight_target(WeightTargetBean weight_target) {
        this.weight_target = weight_target;
    }

    public FatrateTargetBean getFatrate_target() {
        return fatrate_target;
    }

    public void setFatrate_target(FatrateTargetBean fatrate_target) {
        this.fatrate_target = fatrate_target;
    }

    public static class WeightTargetBean {
        /**
         * setuptime : 2016-11-20 15:18:17
         * weight : 70.5
         */

        private String setuptime;
        private float weight;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }

    public static class FatrateTargetBean {
        /**
         * setuptime : 2016-11-20 15:18:17
         * fatrate : 20.2
         */

        private String setuptime;
        private float fatrate;

        public String getSetuptime() {
            return setuptime;
        }

        public void setSetuptime(String setuptime) {
            this.setuptime = setuptime;
        }

        public float getFatrate() {
            return fatrate;
        }

        public void setFatrate(float fatrate) {
            this.fatrate = fatrate;
        }
    }
}
