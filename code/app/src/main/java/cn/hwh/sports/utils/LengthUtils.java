package cn.hwh.sports.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/7/4.
 */
public class LengthUtils {

    /**
     * 米的距离变成公里显示
     *
     * @param num(米)
     * @return
     */
    public static final String formatLength(float num) {
        //将值先取整
        int totalLength = (int) (num * 100);
        String totalResult = (int) (num / 1000) + "";
        //判断去整后
        if (totalLength != 0) {
            if (totalResult.length() < 2) {
                BigDecimal b = new BigDecimal(num / 1000);
                totalResult = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "";
            } else if (totalResult.length() < 3) {
                BigDecimal b = new BigDecimal(num / 1000);
                totalResult = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "";
            } else if(totalResult.length() >4) {
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
        } else if (totalLength == 0) {
            totalResult = "0";
        }
        return totalResult;
    }
}
