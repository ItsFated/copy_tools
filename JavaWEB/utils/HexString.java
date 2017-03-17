package com.im_hero.encryption;

/**
 * Created by Jason on 2017/2/28.
 * 字节数组转十六进制字符串，十六进制字符串转字节数组
 */
public final class HexString {
    /**
     * 十六进制字符串
     */
    public static final char[] HEX = new char[]{
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'};

    /**
     * 将字节数组转换成16进制字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        final int len = bytes.length;
        char[] chars = new char[len << 1];
        byte b;
        for (int i = 0; i < len; i++) {
            b = bytes[i];
            chars[i << 1] = HEX[(b & 0xF0) >> 4];
            chars[(i << 1) + 1] = HEX[b & 0xF];
        }
        return String.valueOf(chars);
    }

    /**
     * 将 16 进制字符转换为字节数组，注意：只能是大写字符
     */
    public static byte[] hexString2Bytes(String s) {
        final int len = s.length();
        if ((len & 0x1) == 0) {
            byte[] bytes = new byte[len >> 1];
            for (int i = 0; i < len; i += 2) {
                bytes[i >> 1] = (byte) ((hexCharToDigit(s.charAt(i)) << 4) | hexCharToDigit(s.charAt(i + 1)));
            }
            return bytes;
        }
        throw new RuntimeException("The length must be a multiple of 2. error length: " + len);
    }

    /**
     * 将十六进制字符转换成数字
     */
    public static int hexCharToDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return 10 + c - 'a';
        } else if (c >= 'A' && c <= 'F'){
            return 10 + c - 'A';
        }
        throw new IllegalArgumentException("the character '" + c + "' isn't a hex character.");
    }

//    public static void main(String[] args) {
//        long startTime, endTime;
//        startTime = System.currentTimeMillis();
//        for(int i = 0;i < 99999999; i++){
//            Arrays.binarySearch(HEX, '0');
//        }
//        endTime = System.currentTimeMillis();
//        System.out.println("TimeSpent:" + (endTime  - startTime));
//        startTime = System.currentTimeMillis();
//        for(int i = 0;i < 99999999; i++){
//            hexCharToDigit('0');
//        }
//        endTime = System.currentTimeMillis();
//        System.out.println("TimeSpent:" + (endTime  - startTime));
//    }

    private HexString(){}
}
