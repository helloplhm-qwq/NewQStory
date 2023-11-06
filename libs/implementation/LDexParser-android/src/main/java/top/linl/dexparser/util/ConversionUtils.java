package top.linl.dexparser.util;

/**
 * @author suzhelan
 * time 2023.9.29
 */
public class ConversionUtils {
    /**
     * 转4字节对齐的uint
     */
    public static int byteToUnsignedInt(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return 0;
        return (bytes[3] & 0XFF) << 24
                | (bytes[2] & 0xFF) << 16
                | (bytes[1] & 0xFF) << 8
                | bytes[0] & 0xFF;
    }

    public static long byte2Int(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return 0;
        return (long) (bytes[3] & 0XFF) << 24
                | (bytes[2] & 0xFF) << 16
                | (bytes[1] & 0xFF) << 8
                | bytes[0] & 0xFF;
    }

    public static int bytesToUnsignedShort(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return 0;
        return ((bytes[0] & 0xff) |
                ((bytes[1] & 0xff)) << 8);
    }

    /**
     * 字节数组转无符号字节数组
     */
    public static byte[] bytesToUnsignedBytes(byte[] src) {
        byte[] bytes = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            bytes[i] = (byte) (src[i] & 0xff);
        }
        return bytes;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * 从数组读取uleb128
     */
    public static int readULeb128(byte[] src, int off) {
        int result = 0;
        int count = 0;
        int cur = src[off] &= (byte) 0xff;
        do {
            result |= (cur & 0x7f) << count * 7;
            count++;
            off++;
        } while ((cur & 0x80) == 128 //128
                && count < 5);//uleb均在五位以内
        return result;
    }


}
