package cn.hwh.sports.entity.net;

/**
 * Created by Raul on 2015/11/15.
 * 手机号码是否已注册
 */
public class PhoneNumIsRegisterRE {

    public static final int EXIST = 1;
    public static final int NOT_EXIST = 0;

    public int isExist;
    public int isBound;

    public PhoneNumIsRegisterRE() {
    }

    public PhoneNumIsRegisterRE(int isExist) {
        super();
        this.isExist = isExist;
    }

    public PhoneNumIsRegisterRE(int isExist, int isBound) {
        this.isExist = isExist;
        this.isBound = isBound;
    }

    public int getIsExist() {
        return isExist;
    }

    public void setIsExist(int isExist) {
        this.isExist = isExist;
    }

    public int getIsBound() {
        return isBound;
    }

    public void setIsBound(int isBound) {
        this.isBound = isBound;
    }
}
