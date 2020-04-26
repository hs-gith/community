package com.hs.until;


import java.util.StringTokenizer;

public class StringUtil {

    /**
     * 补零
     *
     * @param num 零的个数
     * @return String 有零组成的字符串
     * @author huxinyu  2018/11/2
     */
    public static String getZero(int num) {
        StringBuilder str = new StringBuilder();
        while (str.length() < num) {
            str.append(0);
        }
        return str.toString();
    }

    private static final char[] DIGITS_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    private static String hexString = "0123456789ABCDEF";

    public static int[] StringtoInt(String str) {
        int[] ret = new int[str.length()];
        StringTokenizer toKenizer = new StringTokenizer(str, ",");
        int i = 0;
        while (toKenizer.hasMoreElements()) {
            ret[i++] = Integer.valueOf(toKenizer.nextToken());
        }
        return ret;
    }

    /**
     * byte转十六进制（String表示）
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
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
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * 字符串转十六进制字符串
     *
     * @param data
     * @return
     */
    public static String encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return new String(out);
    }

    public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("字符个数应该为偶数");
        }
        byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f |= toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }


    /**
     * 字符串转int数组
     *
     * @param str {1,2,3}
     * @return
     */
    public static int[] toIntArrays(String str) {
        String[] s = str.split(",");
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    /**
     * 字符数组转int数组
     *
     * @param
     * @return
     */
    public static int[] toIntArrays(String[] s) {
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    /**
     * 转化十六进制编码为字符串
     *
     * @param s
     * @return
     */
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     *
     * @param str
     * @return
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否为16进制
     *
     * @param str
     * @return
     */

    public static Boolean isHexStr(String str) {
        String regex = "^[A-Fa-f0-9]+$";
        return str.matches(regex);
    }

    public static void main(String[] args) {
        System.out.println(encodeHex("01ZdQitYL".getBytes()).toUpperCase());
        System.out.println(toStringHex(encodeHex("01ZdQitYL".getBytes()).toUpperCase()));
        String s = "323232323245Faafaa";
        System.out.println("是否为16进制字符串" + isHexStr(s));
    }
}
