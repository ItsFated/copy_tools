package com.im_hero.encryption;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jason on 2017/3/1.
 * 常用的计算哈希值的工具类
 */
public final class MessageDigestUtils {

    /**
     * 计算文本的摘要值
     * @param plainText 明文
     * @param algorithm 摘要算法
     * @return 消息摘要
     */
    public static @NotNull String stringDigest(String plainText, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(plainText.getBytes());
            return HexString.bytes2HexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 计算文件的摘要值
     * @param file 目标文件
     * @param algorithm 摘要算法
     * @return 摘要值
     */
    public static @NotNull String fileDigest(File file, String algorithm) {
        try {
            return streamDigest(new FileInputStream(file), algorithm);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 计算IO流的摘要值
     * @param in 输入流
     * @param algorithm 摘要算法
     * @return 摘要值
     */
    public static @NotNull String streamDigest(InputStream in, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            in.close();
            return HexString.bytes2HexString(md.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

//    public static final String LARGE_TEST = "LARGE_TEST";
//    public static void main(String[] args) {
//        System.out.println(stringDigest(LARGE_TEST, "SHA-1"));
//    }

    private MessageDigestUtils(){}
}
