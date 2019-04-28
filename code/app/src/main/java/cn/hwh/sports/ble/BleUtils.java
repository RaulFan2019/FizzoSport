package cn.hwh.sports.ble;

/**
 * Created by Administrator on 2016/8/28.
 */
public class BleUtils {

    /**
     * byte 数组转化成16进制的字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(final byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取原始数据
     *
     * @param src
     * @return
     */
    public static String parseOriginalData(final byte[] src) {
        String original = "";
        //计算滤光
        long light = ((((long) src[2] & 0xff) << 16)
                | (((long) src[1] & 0xff) << 8)
                | (((long) src[0] & 0xff) << 0));
        original += light + ",";
        //计算X轴
        short x = (short) (((short) src[3] & 0xFF)
                | (((short) src[4] & 0xFF) << 8));
        original += x + ",";
        //计算Y轴
        short y = (short) (((short) src[5] & 0xFF)
                | (((short) src[6] & 0xFF) << 8));
        original += y + ",";
        //计算Z轴
        short z = (short) (((short) src[7] & 0xFF)
                | (((short) src[8] & 0xFF) << 8));
        original += z + ";";

        //计算滤光
        light = ((((long) src[11] & 0xff) << 16)
                | (((long) src[10] & 0xff) << 8)
                | (((long) src[9] & 0xff) << 0));
        original += light + ",";
        //计算X轴
        x = (short) (((short) src[12] & 0xFF)
                | (((short) src[13] & 0xFF) << 8));
        original += x + ",";
        //计算Y轴
        y = (short) (((short) src[14] & 0xFF)
                | (((short) src[15] & 0xFF) << 8));
        original += y + ",";
        //计算Z轴
        z = (short) (((short) src[16] & 0xFF)
                | (((short) src[17] & 0xFF) << 8));
        original += z + ";";
        //校验位
//        int jiaoyan = ((src[19] & 0xFF));
//        original += "[" + jiaoyan + "]";
//        Log.e(TAG, "hexString:" + bytesToHexString(src));
//        Log.e(TAG, "original:" + original + "[" + jiaoyan + "]");
        return original;
    }

    public static int crc16(byte[] buffer, final int type) {
        int crc = 0;
        int length = buffer.length;
        if (type == BleConfig.TYPE_SOFTWARE) {
            length = length - 1;
        }

        for (int i = 4, size = length; i < size; i++) {
            crc = crc(crc, buffer[i]);
//            Log.v(TAG, "crc:" + crc);
        }
//        Log.v(TAG,"crc:" +crc);
        crc = crc(crc, (byte) 0);
//        Log.v(TAG,"crc:" +crc);
        crc = crc(crc, (byte) 0);
//        Log.v(TAG, "crc:" + crc);
        return crc;
    }

    public static int crc(int crc, byte val) {
        int poly = 0x1021;
        for (byte cnt = 0; cnt < 8; cnt++, val <<= 1) {
            byte msb = (byte) (((crc & 0x8000) != 0) ? 1 : 0);
            crc <<= 1;
            if ((val & 0x80) != 0) {
                crc |= 0x0001;
            }
            if (msb != 0) {
                crc ^= poly;
            }
        }
        return crc & 0xFFFF;
    }

    /**
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * @param b
     * @return
     */
    public static long byteToLong(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        long n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }
}
