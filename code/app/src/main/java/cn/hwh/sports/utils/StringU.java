package cn.hwh.sports.utils;

import android.content.Context;
import android.nfc.FormatException;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hwh.sports.R;
import cn.hwh.sports.activity.settings.FizzoDeviceSettingActivity;
import cn.hwh.sports.config.AppEnum;

/**
 * Created by Raul on 2015/11/11.
 * 字符串相关工具
 */
public class StringU {


    /**
     * 验证邀请码
     *
     * @param context
     * @param invite
     * @return
     */
    public static String checkInvite(final Context context, final String invite) {
        //手机号码不能为空
        if (TextUtils.isEmpty(invite)) {
            return context.getResources().getString(R.string.Error_Check_Invite_Empty);
        }
        if (invite.length() != 4) {
            return context.getResources().getString(R.string.Error_Check_Invite_Length);
        }
        String regex = "[a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]";
        if (!invite.matches(regex)) {
            return context.getResources().getString(R.string.Error_Check_Invite_Error);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }

    /**
     * 检查手机号码是否正确
     *
     * @param context  上下文
     * @param phoneNum 手机号码
     * @return
     */
    public static String checkPhoneNumInputError(final Context context,
                                                 final String phoneNum
                                                 //, final String mCountryIso
    ) {
        //手机号码不能为空
        if (TextUtils.isEmpty(phoneNum)) {
            return context.getResources().getString(R.string.Error_Check_Phonenum_Empty);
        }
        if (phoneNum.length() < 11) {
            return context.getResources().getString(R.string.Error_Check_Phonenum_Illegal);
        }

        //若是中国的电话号码
//        if (mCountryIso.toUpperCase().equals("CN") && !isMobileNO(phoneNum)) {
//            return context.getResources().getString(R.string.Error_Check_Phonenum_Illegal);
//        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }

    /**
     * 检查密码是否符合规范
     *
     * @param context  上下文
     * @param password 密码
     * @return
     */
    public static String checkPasswordInputError(final Context context, final String password) {
        //密码不能为空
        if (TextUtils.isEmpty(password)) {
            return context.getResources().getString(R.string.Error_Check_Password_Empty);
        }
        if (password.length() > 16 || password.length() < 6) {
            return context.getResources().getString(R.string.Error_Check_Password_Length);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    /**
     * 检查验证码字符串是否正确
     *
     * @param code
     * @return
     */
    public static String checkVCCodeError(final Context context, final String code) {
        //验证码不能为空
        if (TextUtils.isEmpty(code)) {
            return context.getResources().getString(R.string.Error_Check_VcCode_Empty);
        }
        if (code.length() != 4) {
            return context.getResources().getString(R.string.Error_Check_VcCode_Length);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }

    /**
     * 检查昵称
     *
     * @param context
     * @param nickname
     * @return
     */
    public static String checkNickNameError(final Context context, final String nickname) {
        //昵称不能为空
        if (TextUtils.isEmpty(nickname)) {
            return context.getResources().getString(R.string.Error_Check_Nickname_Empty);
        }
        if (nickname.length() > 10) {
            return context.getResources().getString(R.string.Error_Check_Nickname_Length);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }

    /**
     * 检查邮箱
     *
     * @param context
     * @param email
     * @return
     */
    public static String checkEmailError(final Context context, final String email) {
        //邮箱不能为空
        if (TextUtils.isEmpty(email)) {
            return context.getResources().getString(R.string.Error_Check_Email_Empty);
        }
        if (!isEmail(email)) {
            return context.getResources().getString(R.string.Error_Check_Email_Error);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    /**
     * 检查输入心率错误
     * @param context
     * @param hrStr
     * @param editType
     * @return
     */
    public static String checkInputHrError(final Context context, final String hrStr, final int editType) {
        if (TextUtils.isEmpty(hrStr)) {
            return context.getResources().getString(R.string.Error_Check_hr_Empty);
        }
        try {
            int hr = Integer.parseInt(hrStr);
            if (hr > 220 || hr < 40) {
                return context.getResources().getString(R.string.Error_Check_hr_Error);
            }
            return AppEnum.DEFAULT_CHECK_ERROR;
        } catch (NumberFormatException e) {
            return context.getResources().getString(R.string.Error_Check_hr_format);
        }
    }

    /**
     * 检查最高心率是否正确
     *
     * @param context
     * @param hrStr
     * @return
     */
    public static String checkMaxHrError(final Context context, final String hrStr) {
        if (TextUtils.isEmpty(hrStr)) {
            return context.getResources().getString(R.string.Error_Check_max_hr_Empty);
        }
        try {
            int hr = Integer.parseInt(hrStr);
            if (hr < 120 || hr > 240) {
                return context.getResources().getString(R.string.Error_Check_max_hr_Error);
            }
            return AppEnum.DEFAULT_CHECK_ERROR;
        } catch (NumberFormatException e) {
            return context.getResources().getString(R.string.Error_Check_max_hr_format);
        }
    }

    /**
     * 检查最高心率是否正确
     *
     * @param context
     * @param hrStr
     * @return
     */
    public static String checkRestHrError(final Context context, final String hrStr) {
        if (TextUtils.isEmpty(hrStr)) {
            return context.getResources().getString(R.string.Error_Check_rest_hr_Empty);
        }
        try {
            int hr = Integer.parseInt(hrStr);
            if (hr < 60 || hr > 120) {
                return context.getResources().getString(R.string.Error_Check_rest_hr_Error);
            }
            return AppEnum.DEFAULT_CHECK_ERROR;
        } catch (NumberFormatException e) {
            return context.getResources().getString(R.string.Error_Check_rest_hr_format);
        }
    }

    /**
     * 检查昵称
     *
     * @param context
     * @param nickname
     * @return
     */
    public static String checkTrueNameError(final Context context, final String nickname) {
        //昵称不能为空
        if (TextUtils.isEmpty(nickname)) {
            return context.getResources().getString(R.string.Error_Check_True_Name_Empty);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    /**
     * 检查输入目标步数
     *
     * @param context
     * @param stepStr
     * @return
     */
    public static String checkTargetStepError(final Context context, final String stepStr) {
        if (TextUtils.isEmpty(stepStr)) {
            return context.getResources().getString(R.string.Error_Check_Step_Empty);
        }
        try {
            int step = Integer.valueOf(stepStr);
            if (step == 0) {
                return context.getResources().getString(R.string.Error_Check_Target_Zero);
            }
        } catch (NumberFormatException ex) {
            return context.getResources().getString(R.string.Error_Check_Target_Format);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    public static String checkTargetLengthError(final Context context, final String lengthStr) {
        if (TextUtils.isEmpty(lengthStr)) {
            return context.getResources().getString(R.string.Error_Check_Length_Empty);
        }
        try {
            float length = Float.valueOf(lengthStr);
            if (length == 0) {
                return context.getResources().getString(R.string.Error_Check_Target_Zero);
            } else if (length > 300) {
                return "距离不能超过300公里";
            }
        } catch (NumberFormatException ex) {
            return context.getResources().getString(R.string.Error_Check_Target_Format);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }

    public static String checkRunningLengthError(final Context context, final String lengthStr) {
        if (TextUtils.isEmpty(lengthStr)) {
            return context.getResources().getString(R.string.Error_Check_Length_Empty);
        }
        try {
            float length = Float.valueOf(lengthStr);

        } catch (NumberFormatException ex) {
            return context.getResources().getString(R.string.Error_Check_Target_Format);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }

    /**
     * 检查输入的卡路里
     *
     * @param context
     * @param lengthStr
     * @return
     */
    public static String checkTargetCalorieError(final Context context, final String lengthStr) {
        if (TextUtils.isEmpty(lengthStr)) {
            return context.getResources().getString(R.string.Error_Check_Calorie_Empty);
        }
        try {
            int calorie = Integer.valueOf(lengthStr);
            if (calorie == 0) {
                return context.getResources().getString(R.string.Error_Check_Target_Zero);
            }
        } catch (NumberFormatException ex) {
            return context.getResources().getString(R.string.Error_Check_Target_Format);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    /**
     * 检查目标点数
     *
     * @param context
     * @param pointStr
     * @return
     */
    public static String checkTargetPointError(final Context context, final String pointStr) {
        if (TextUtils.isEmpty(pointStr)) {
            return context.getResources().getString(R.string.Error_Check_Point_Empty);
        }
        try {
            int calorie = Integer.valueOf(pointStr);
            if (calorie == 0) {
                return context.getResources().getString(R.string.Error_Check_Target_Zero);
            }
        } catch (NumberFormatException ex) {
            return context.getResources().getString(R.string.Error_Check_Target_Format);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    /**
     * 检查输入目标活跃时间
     *
     * @param context
     * @param sportTimeStr
     * @return
     */
    public static String checkTargetSportTimeError(final Context context, final String sportTimeStr) {
        if (TextUtils.isEmpty(sportTimeStr)) {
            return context.getResources().getString(R.string.Error_Check_Sport_Time_Empty);
        }
        try {
            int sportTime = Integer.valueOf(sportTimeStr);
            if (sportTime == 0) {
                return context.getResources().getString(R.string.Error_Check_Target_Zero);
            }
        } catch (NumberFormatException ex) {
            return context.getResources().getString(R.string.Error_Check_Target_Format);
        }
        return AppEnum.DEFAULT_CHECK_ERROR;
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(final String mobiles) {
        /*SS
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
//        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobiles))
//            return false;
//        else
//            return mobiles.matches(telRegex);
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        return true;
    }

    /**
     * Email检查
     *
     * @param email
     * @return
     */
    private static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))" +
                "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 数字格式化，1234 -> 1,234
     *
     * @param num
     * @return
     */
    public static String numFormat(double num) {
        DecimalFormat lDecimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        lDecimalFormat.setGroupingSize(3);
        return lDecimalFormat.format(num);
    }


    /**
     * 根据整数位确定小数位数
     *
     * @param num
     * @return
     */
    public static String formatFloatNum(float num) {
        //将值先取整
        //   int totalLength = (int) (num * 100);
        String totalResult = (int) (num / 1000) + "";
        //判断去整后
        if (num != 0) {
            if (totalResult.length() < 2) {
                BigDecimal b = new BigDecimal(num / 1000);
                totalResult = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "";
            } else if (totalResult.length() <= 3) {
                BigDecimal b = new BigDecimal(num / 1000);
                totalResult = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "";
            } else if (totalResult.length() >= 4) {
                totalResult = (int) (num / 1000 / 10000) + "";
                if (totalResult.length() == 1) {
                    BigDecimal b = new BigDecimal(num / 1000 / 10000);
                    totalResult = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "万";
                } else if (totalResult.length() == 2) {
                    BigDecimal b = new BigDecimal(num / 1000 / 10000);
                    totalResult = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "万";
                } else {
                    totalResult += "万";
                }
            }
        } else {
            totalResult = "0";
        }
        return totalResult;
    }

    public static boolean firmwareNeedUpdate(final String oldVer, final String newVer){
        String[] oldStr = oldVer.split("[.\\n]");
        String[] newStr = newVer.split("[.\\n]");
        if (Integer.parseInt(oldStr[0]) <  Integer.parseInt(newStr[0])){
            return true;
        }
        if (Integer.parseInt(oldStr[0]) >  Integer.parseInt(newStr[0])){
            return false;
        }

        if (Integer.parseInt(oldStr[1]) <  Integer.parseInt(newStr[1])){
            return true;
        }
        if (Integer.parseInt(oldStr[1]) >  Integer.parseInt(newStr[1])){
            return false;
        }

        if (Integer.parseInt(oldStr[2]) <  Integer.parseInt(newStr[2])){
            return true;
        }
        return false;
    }


    /**
     * 根据手表的版本决定是否必须升级
     * @param oldVer
     * @param newVer
     * @return
     */
    public static boolean mustUpdateForSupportWatch(final String oldVer, final String newVer){
        String[] oldStr = oldVer.split("[.\\n]");
        String[] newStr = newVer.split("[.\\n]");
        if (Integer.parseInt(oldStr[0]) <  Integer.parseInt(newStr[0])){
            return true;
        }
        if (Integer.parseInt(oldStr[0]) >  Integer.parseInt(newStr[0])){
            return false;
        }
        if (Integer.parseInt(oldStr[1]) <  Integer.parseInt(newStr[1])){
            return true;
        }
        return false;
    }
}
