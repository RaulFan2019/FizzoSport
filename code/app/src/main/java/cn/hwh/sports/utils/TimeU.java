package cn.hwh.sports.utils;

import android.support.annotation.Nullable;
import android.text.Html;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/7/24.
 * 时间转换工具
 */
public class TimeU {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    public static final String FORMAT_TYPE_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TYPE_2 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_TYPE_3 = "yyyy-MM-dd";
    public static final String FORMAT_TYPE_4 = "MM月dd日 HH点mm分";
    public static final String FORMAT_TYPE_5 = "yyyy.MM.dd";
    public static final String FORMAT_TYPE_6 = "yyyy-MM";
    public static final String FORMAT_TYPE_7 = "HH:mm";
    public static final String FORMAT_TYPE_8 = "MM-dd HH:mm:ss";
    public static final String FORMAT_TYPE_9 = "HH:mm:ss";

    /**
     * 获取当前时间的String(yyyy-MM-dd HH:mm:ss)
     *
     * @return
     */
    public static String nowTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 获取当前时间的String(yyyy-MM-dd HH:mm:ss)
     *
     * @return
     */
    public static String logTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");// 设置日期格式
        return df.format(new Date()) + ":";// new Date()为获取当前系统时间
    }

    /**
     * 获取当前时间的String(yyyy-MM-dd HH:mm:ss)
     *
     * @return
     */
    public static String nowTime(final String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }


    /**
     * 获取当前时间的String(HH:mm)
     *
     * @return
     */
    public static String refreshTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String nowDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 获取delay前的日期
     *
     * @param delay
     * @return
     */
    public static String getDayByDelay(int delay) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_TYPE_3);// 设置日期格式
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.DATE, -delay); // 减delay
        Date lastDate = ca.getTime(); //结果
        return df.format(lastDate);
    }

    /**
     * 计算fromDay delay天前的日期
     *
     * @param fromDay
     * @param delay
     * @return
     */
    public static String getDayAtFromDay(String fromDay, int delay) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_TYPE_3);// 设置日期格式
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(formatStrToDate(fromDay, FORMAT_TYPE_3));
        ca.add(Calendar.DATE, -delay); //年份 减delay
        Date lastDate = ca.getTime(); //结果
        return df.format(lastDate);
    }

    /**
     * 根据日期获取星期
     *
     * @param dateStr
     * @return {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"}
     */
    public static String getWeekByDayString(String dateStr) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Date date = formatStrToDate(dateStr, FORMAT_TYPE_3);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay < 0) {
            weekDay = 0;
        }

        return weekOfDays[weekDay];
    }

    /**
     * 根据日期获取星期
     *
     * @param dateStr
     * @return {"周日", "周一", "周二", "周三", "周四", "周五", "周六"}
     */
    public static String getWeekByDayStr(String dateStr) {
        String[] weekOfDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Date date = formatStrToDate(dateStr, FORMAT_TYPE_3);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay < 0) {
            weekDay = 0;
        }

        return weekOfDays[weekDay];
    }

    /**
     * 获取星期几
     *
     * @param dateStr
     * @return int
     */
    public static int getWeekDay(String dateStr) {
        int[] weekOfDays = {7, 1, 2, 3, 4, 5, 6};
        Date date = formatStrToDate(dateStr, FORMAT_TYPE_3);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay < 0) {
            weekDay = 0;
        }

        return weekOfDays[weekDay];
    }

    /**
     * 获取2个起始时间之间的时间差
     * "yyyy-MM-dd HH:mm:ss"
     *
     * @param beginTimeStr
     * @param endTimeStr
     * @return 秒
     */
    public static long getTimeDiff(final String beginTimeStr, final String endTimeStr, final String format) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(format);
        long time = 0;
        try {
            Date beginDate = myFormatter.parse(beginTimeStr);
            Date endDate = myFormatter.parse(endTimeStr);

            time = (beginDate.getTime() - endDate.getTime()) / 1000;
        } catch (Exception e) {
            return time;
        }
        return time;
    }

    /**
     * String 日期比较
     *
     * @param startDateStr
     * @param endDateStr
     * @return int 如果compareTo返回0，表示两个日期相等，返回小于0的值，表示startDateStr在endDateStr之前，大于0表示startDateStr在endDateStr之后
     */
    public static int compareDate(final String startDateStr, final String endDateStr) {

        Date startDate = formatStrToDate(startDateStr, FORMAT_TYPE_2);
        Date endData = formatStrToDate(endDateStr, FORMAT_TYPE_2);
        return startDate.compareTo(endData);
    }

    /**
     * 获取运动区间时间
     *
     * @param startTime
     * @param duration
     * @return
     */
    public static String getZoneTime(final String startTime, final long duration) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(FORMAT_TYPE_1);
        try {
            Date beginDate = myFormatter.parse(startTime);
            long endTime = beginDate.getTime() + duration * 1000;
            Date endDate = new Date(endTime);
            SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm");
            return "锻炼时间: " + format.format(beginDate) + "至" + format.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 时间格式为yyyy-MM-dd 转换为date格式
     *
     * @param timeStr
     * @return
     */
    public static Date formatStrToDate(final String timeStr, final String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将时间转换为yyyy-MM-dd format 形式
     *
     * @param date
     * @return
     */
    public static String formatDateToStr(final Date date, final String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将字符串format格式转换为toFormat格式
     *
     * @param date
     * @param format
     * @param toFormat
     * @return
     */
    public static String formatDateStrShow(final String date, final String format, final String toFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
        return sdf.format(formatStrToDate(date, format));
    }

    /**
     * 秒换成时
     */
    public static String formatSecondsToShortHourTime(long Seconds) {
        // SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // formatter.setTimeZone(TimeZone.getTimeZone("GMT+00"));
        long hour = Seconds / 3600;
        long min = Seconds % 3600 / 60;
        long sec = Seconds % 60;
        String hourstr = hour + ":";
        String minstr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        String secstr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
        if (hour > 0) {
            return hourstr + minstr + ":" + secstr;
        } else {
            return minstr + ":" + secstr;
        }
    }

    /**
     * 秒换成时
     */
    public static String formatSecondsToLongHourTime(long Seconds) {
        // SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // formatter.setTimeZone(TimeZone.getTimeZone("GMT+00"));
        long hour = Seconds / 3600;
        long min = Seconds % 3600 / 60;
        long sec = Seconds % 60;
        String hourstr = ((hour < 10) ? ("0" + String.valueOf(hour)) : String.valueOf(hour));
        String minstr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        String secstr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
        return hourstr + ":"+ minstr + ":" + secstr;
    }

    /**
     * 秒换算成配速格式
     *
     * @return
     */
    public static String formatSecondsToPace(long Seconds) {
        if (Seconds > 3600) {
            return "- -";
        }
        long min = Seconds / 60;
        long sec = Seconds % 60;
        String minstr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        String secstr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
        return min + "'" + secstr + "''";
    }

    /**
     * 秒转 0时00分00秒格式
     *
     * @param Seconds
     * @return
     */
    public static String formatChineseDuration(long Seconds) {
        long hour = Seconds / 3600;
        long min = Seconds % 3600 / 60;
        long sec = Seconds % 60;
        String hourstr = hour + "小时";
        String minstr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        String secstr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
        if (hour > 0) {
            return hourstr + minstr + "分" + secstr + "秒";
        } else {
            return minstr + "分" + secstr + "秒";
        }
    }

    /**
     * 分转 0时00分 格式
     *
     * @param mins
     * @return
     */
    public static String formatChineseDurationByMin(long mins) {
        long hour = mins / 60;
        long min = mins % 60;
        String hourStr = hour + "小时";
        String minStr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        if (hour > 0) {
            return hourStr + minStr + "分钟";
        } else {
            return minStr + "分钟";
        }
    }

    /**
     * 生成列表上显示的时间字符串
     *
     * @param startTime
     * @param duration
     * @return
     */
    public static String formatListShowTime(final String startTime, final long duration) {
        Date curDate = new Date();
        String nowTime = nowTime();
        long splitTime = curDate.getTime() - formatStrToDate(startTime, FORMAT_TYPE_2).getTime() - duration;
        if (splitTime < ONE_HOUR) {
            return splitTime / ONE_MINUTE + "分钟前";
        } else if (splitTime < 6 * ONE_HOUR) {
            return splitTime / ONE_HOUR + "小时前";
        } else if (formatStrToDate(nowTime, FORMAT_TYPE_3) == formatStrToDate(startTime, FORMAT_TYPE_3)) {
            return "今天 " + startTime.substring(11, 16);
        } else if (formatStrToDate(nowTime, FORMAT_TYPE_3).getTime() - formatStrToDate(startTime, FORMAT_TYPE_3).getTime() == ONE_DAY) {
            return "昨天 " + startTime.substring(11, 16);
        } else {
            return startTime.substring(8, 10) + "日 " + startTime.substring(11, 16);
        }
    }


    /**
     * 生成同步时间字符串
     *
     * @param startTime
     * @return
     */
    public static String syncTime(final String startTime) {
        Date curDate = new Date();
        String nowTime = nowTime();
        long splitTime = curDate.getTime() - formatStrToDate(startTime, FORMAT_TYPE_2).getTime();
        if (splitTime < ONE_MINUTE) {
            return "刚刚";
        }
        if (splitTime < ONE_HOUR) {
            return splitTime / ONE_MINUTE + "分钟前";
        }
        if (splitTime < 6 * ONE_HOUR) {
            return splitTime / ONE_HOUR + "小时前";
        }
        if (formatStrToDate(nowTime, FORMAT_TYPE_3) == formatStrToDate(startTime, FORMAT_TYPE_3)) {
            return "今天 " + startTime.substring(11, 16);
        }
        if (formatStrToDate(nowTime, FORMAT_TYPE_3).getTime() - formatStrToDate(startTime, FORMAT_TYPE_3).getTime() == ONE_DAY) {
            return "昨天 " + startTime.substring(11, 16);
        }
        return startTime.substring(8, 10) + "日 " + startTime.substring(11, 16);

    }

    /**
     * 将时间转换为yyyy年MM月 String 形式
     *
     * @param date
     * @return
     */
    public static String formatMothString(final Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        return sdf.format(date);
    }

    /**
     * 将日期显示成指定文字格式
     *
     * @param showTime
     * @return 返回星期的格式是 星期一 、星期二
     */
    public static String formatMainShowDate(String showTime) {

        if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(showTime, FORMAT_TYPE_3).getTime() < ONE_DAY) {

            return "今天";
        } else if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(showTime, FORMAT_TYPE_3).getTime() < ONE_DAY * 7) {
            return getWeekByDayString(showTime);
        } else {
            return showTime;
        }
    }

    /**
     * 将日期显示成指定文字格式
     *
     * @param showTime
     * @return 返回星期的格式是 周一、周二
     */
    public static CharSequence formatItemShowDate(String showTime) {
        if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(showTime, FORMAT_TYPE_3).getTime() < ONE_DAY) {

            return Html.fromHtml("<b><font color=\"#2E2E2E\">今天</b>");
        } else if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(showTime, FORMAT_TYPE_3).getTime() < ONE_DAY * 7) {
            return getWeekByDayStr(showTime);
        } else {
            return formatDateStrShow(showTime, FORMAT_TYPE_3, "MM/dd");
        }
    }

    /**
     * 格式化日期显示
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String formatTimeScope(String startTime, String endTime) {
        if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(endTime, FORMAT_TYPE_3).getTime() < ONE_DAY * 7) {
            return "本周";
        } else if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(endTime, FORMAT_TYPE_3).getTime() < ONE_DAY * 14) {
            return "上周";
        } else if (formatDateStrShow(startTime, FORMAT_TYPE_3, "MM").equals(formatDateStrShow(endTime, FORMAT_TYPE_3, "MM"))) {
            return formatDateStrShow(startTime, FORMAT_TYPE_3, "MM月dd日") + "-" + formatDateStrShow(endTime, FORMAT_TYPE_3, "dd日");
        } else {
            return formatDateStrShow(startTime, FORMAT_TYPE_3, "MM月dd日") + "-" + formatDateStrShow(endTime, FORMAT_TYPE_3, "MM月dd日");
        }
    }

    /**
     * 显示WorkoutList 上的周信息
     *
     * @param weekStartTime
     * @return
     */
    public static String formatWorkoutListWeekDay(final String weekStartTime) {
        String weekEndTime = getDayAtFromDay(weekStartTime, -7);
        if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(weekStartTime, FORMAT_TYPE_3).getTime() < ONE_DAY * 7) {
            return "本周";
        } else if (formatStrToDate(nowDay(), FORMAT_TYPE_3).getTime() - formatStrToDate(weekStartTime, FORMAT_TYPE_3).getTime() < ONE_DAY * 14) {
            return "上周";
        } else if (formatDateStrShow(weekStartTime, FORMAT_TYPE_3, "MM").equals(formatDateStrShow(weekEndTime, FORMAT_TYPE_3, "MM"))) {
            return formatDateStrShow(weekStartTime, FORMAT_TYPE_3, "MM月dd日") + "-" + formatDateStrShow(weekEndTime, FORMAT_TYPE_3, "dd日");
        } else {
            return formatDateStrShow(weekStartTime, FORMAT_TYPE_3, "MM月dd日") + "-" + formatDateStrShow(weekEndTime, FORMAT_TYPE_3, "MM月dd日");
        }
    }

    /**
     * 获取WorkoutList 上显示的开始时间
     *
     * @param startTime
     * @return
     */
    public static String formatWorkoutListStartTime(final String startTime) {
        Date startDate = formatStrToDate(startTime, FORMAT_TYPE_1);
        return formatDateToStr(startDate, FORMAT_TYPE_5)
                + " " + getWeekByDayString(startTime) + " "
                + formatDateToStr(startDate, FORMAT_TYPE_7);
    }


    /**
     * 判断是否是今天的日期
     * @param startTime
     * @param format
     * @return
     */
    public static boolean isToday(final String startTime , final String format){
        Date date = formatStrToDate(startTime,format);
        String dateStr = formatDateToStr(date,TimeU.FORMAT_TYPE_5);
        String todayStr = formatDateToStr(new Date(),TimeU.FORMAT_TYPE_5);
        return dateStr.equals(todayStr);
    }

    /**
     * 星期转换成汉字
     *
     * @param weekday
     * @return
     */
    public static String intToWeekChinese(final int weekday) {
        switch (weekday) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "日";
        }
        return "";
    }
}
