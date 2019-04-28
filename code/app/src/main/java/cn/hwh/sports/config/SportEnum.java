package cn.hwh.sports.config;

/**
 * Created by Raul.Fan on 2016/11/24.
 */

public class SportEnum {

    /**
     * 跑步状态
     */
    public class SportStatus {
        public static final int RUNNING = 1;
        public static final int FINISH = 2;
    }


    /**
     * 运动类型
     */
    public class EffortType {
        public static final int FREE_INDOOR = 3;//无器械
        public static final int RUNNING_OUTDOOR = 1;//室外跑步
        public static final int RUNNING_INDOOR = 4;//室内跑步
        public static final int LOU_TI = 5;//楼梯机
        public static final int DAN_CHE = 6;//动感单车
        public static final int TUO_YUAN = 7;//椭圆机
        public static final int HUA_CHUAN = 8;//划船
        public static final int XIAO_QI_XIE = 9;//小器械
        public static final int TIAO_SHENG = 10;//跳绳
    }

    /**
     * 运动目标
     */
    public class TargetType {
        public static final int DEFAULT = 0;
        public static final int FAT = 1;//减脂
        public static final int STRONGER = 2;//增强心肺
    }

    /**
     * 数据来源
     */
    public class resource {
        public static final int APP = 1;
        public static final int WATCH = 5;
        public static final int PC = 6;
    }

    /**
     * 是否是新的锻炼记录
     */
    public class NewWorkout {
        public static final int NEW = 1;
        public static final int OLD = 0;
    }

    /**
     * 是否是新的锻炼记录
     */
    public class IsWorkoutEnd {
        public static final int YES = 1;
        public static final int NO = 0;
    }


    /**
     * 分段类型
     */
    public class splitType {
        public static final int LENGTH = 1;
        public static final int TIME = 2;
    }
}
