package com.im_hero.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Jason on 2017/3/1.
 * RSA 非对称加密工具类
 */
public final class RSAUtils {
    public static final String ALGORITHM = "RSA";
    public static final String CHARSET = "UTF-8";

    /**
     * 创建一个带有新的密钥对的加密器
     * @return 加密器
     */
    public static RSACipher create(int keySize) {
        if (keySize % 1024 != 0) throw new IllegalArgumentException("Key Size 必须要是 1024 的倍数");
        try {
            // 密钥对生成器
            KeyPairGenerator  keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keySize);
            // 密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // 创建加密器
            return new RSACipher(Cipher.getInstance(ALGORITHM), (RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据现有密钥对创建一个加密器
     * @param publicKey 公钥
     * @param privateKey 私钥
     * @return 加密器
     */
    public static RSACipher create(byte[] publicKey, byte[] privateKey) {
        try {
            // 密钥工厂，用于创建密钥
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 创建公钥
            RSAPublicKey keyPublic = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            // 创建私钥
            RSAPrivateKey keyPrivate = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            // 创建加密器
            return new RSACipher(Cipher.getInstance(ALGORITHM), keyPublic, keyPrivate);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 切割 Byte 数组
     * @param bytes 一维 Byte 数组
     * @param len 切割参数，每段的 Byte 个数
     * @return 切割结果
     */
    public static byte[][] splitByteArray(final byte[] bytes, int len) {
        final int sourceLen = bytes.length,                         // 原 Byte 数组的长度
                resultLen = (int) Math.ceil(sourceLen * 1F / len),  // 被切割后的二维数组的长度
                lastLen = sourceLen % len,                          // 最后一段的长度
                forLoop = resultLen - 1;

        // 原数组长度为 0
        if (resultLen < 1) return new byte[][]{bytes};

        byte[][] result = new byte[resultLen][];
        int pos;
        byte[] arr;

        // 将最后一段之前的数组先分割
        for (int i = 0; i < forLoop; i++) {
            pos = i * len;
            arr = new byte[len];
            System.arraycopy(bytes, pos, arr, 0, len);
            result[i] = arr;
        }

        // 给最后一段赋值
        len = lastLen == 0 ? len : lastLen;
        arr = new byte[len];
        System.arraycopy(bytes, len * forLoop, arr, 0, len);
        result[forLoop] = arr;

        return result;
    }

    /**
     * 将二维数组连接成为一维数组
     *
     * @param bytes     需要被连接的二维数组
     * @param resultLen 结果的长度（如果不知道结果应该为多少，请输入-1）
     * @return 结果以为数组
     */
    public static byte[] concatByteArray(byte[][] bytes, int resultLen) {
        int len = 0;
        byte[] result;
        if (resultLen < 0) {
            for (byte[] aByte : bytes) {
                len += aByte.length;
            }
            result = new byte[len];
        } else {
            result = new byte[resultLen];
        }
        byte[] b;
        len = 0;
        for (byte[] aByte : bytes) {
            b = aByte;
            System.arraycopy(b, 0, result, len, b.length);
            len += b.length;
        }
        return result;
    }

    /**
     * RSA 加密解密类，默认使用 UTF-8 编码，注意：对字符串加密，解密时密文是16进制字符串
     */
    public static final class RSACipher {
        private Cipher cipher;
        private RSAPublicKey publicKey;
        private RSAPrivateKey privateKey;

        private RSACipher(Cipher cipher, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
            this.cipher = cipher;
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public Cipher getCipher() {
            return cipher;
        }

        public void setCipher(Cipher cipher) {
            this.cipher = cipher;
        }

        public void setPublicKey(RSAPublicKey publicKey) {
            this.publicKey = publicKey;
        }

        public void setPrivateKey(RSAPrivateKey privateKey) {
            this.privateKey = privateKey;
        }

        /**
         * 公钥
         */
        public PublicKey getPublicKey() {
            return publicKey;
        }

        /**
         * 私钥
         */
        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        /**
         * 使用 RSA 加密
         * @param plainText 明文
         * @param key 加密密钥
         * @return 密文的16进制字符
         */
        public String encrypt(String plainText, Key key){
            try {
                return HexString.bytes2HexString(encrypt(plainText.getBytes(CHARSET), key));
            } catch (InvalidKeyException
                    | BadPaddingException
                    | IllegalBlockSizeException
                    | IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        /**
         * 使用 RSA 解密
         * @param cypherText 密文的16进制字符
         * @param key 解密密钥
         * @return 明文
         */
        public String decrypt(String cypherText, Key key){
            try {
                return new String(decrypt(HexString.hexString2Bytes(cypherText), key), CHARSET);
            } catch (InvalidKeyException
                    | BadPaddingException
                    | IllegalBlockSizeException
                    | IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        /**
         * 使用 RSA 加密
         * @param plainText 明文
         * @param key 加密密钥
         * @return 密文
         */
        public byte[] encrypt(byte[] plainText, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
            RSAKey rsaKey;
            if (key instanceof RSAKey) {
                rsaKey = (RSAKey) key;
                cipher.init(Cipher.ENCRYPT_MODE, key);
                ByteArrayInputStream bais = new ByteArrayInputStream(plainText);

                int len,/* 密钥模长 */modulusLen = rsaKey.getModulus().bitLength() / 8,
                        /* 数据模长 */dataModulusLen = modulusLen - 11;

                // 通过数据模长和密钥模长计算结果的长度
                byte[] result = new byte[modulusLen * ((int) Math.ceil(plainText.length * 1D / dataModulusLen))],
                        buffer = new byte[dataModulusLen],
                        temp;
                for (int i = 0; (len = bais.read(buffer)) > 0; i++){
                    temp = cipher.doFinal(buffer, 0, len);
                    System.arraycopy(temp, 0, result, i * modulusLen, temp.length);
                }
                return result;
            }
            return new byte[0];
        }

        /**
         * 使用 RSA 解密
         * @param cypherText 密文
         * @param key 解密密钥
         * @return 明文
         */
        public byte[] decrypt(byte[] cypherText, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
            RSAKey rsaKey;
            if (key instanceof RSAKey) {
                rsaKey = (RSAKey) key;
                cipher.init(Cipher.DECRYPT_MODE, key);
                // 模长
                int len, modulusLen = rsaKey.getModulus().bitLength() / 8;
                ByteArrayInputStream bais = new ByteArrayInputStream(cypherText);
                byte[][] result = new byte[cypherText.length / modulusLen][];
                byte[] buffer = new byte[modulusLen];
                int resultLen = 0;
                for (int i = 0; (len = bais.read(buffer)) > 0; i++) {
                    result[i] = cipher.doFinal(buffer, 0, len);
                    resultLen += result[i].length;
                }
                return concatByteArray(result, resultLen);
            }
            return new byte[0];
        }

    }
//    测试代码
//    public static final String LARGE_TEST = "我没有间断减肥方法解放军分了大量进口反季节斯蒂芬垃圾啊手捧随机附件三可是当飞机拉萨司法当局扣留第三方看见了分洒进来看"
//            + "我没有间断减肥方法解放军分了大量进口反季节斯蒂芬垃圾啊手捧随机附件三可是当飞机拉萨司法当局扣留第三方看见了分洒进来看"
//            + "LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST "
//            + "LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST "
//            + "LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST "
//            + "LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST "
//            + "LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST "
//            + "LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST LARGE_TEST ";
//    public static final String SMALL_TEST = "42c0d1811ebfbcd3ee1b6a102beab5e1163a13d89c5285357f58e032ca3854bd894a895c25be8c4cf3112c8c12897f0618aa712a1985b88888888";
//
//    public static void main(String[] args) throws Exception {
//        System.out.println(SMALL_TEST.length());
//
//        RSACipher cipher = create(1024);
//        String cypherText = cipher.encrypt(SMALL_TEST, cipher.getPrivateKey());
//        String plainText = cipher.decrypt(cypherText, cipher.getPublicKey());
//        System.out.println("密文 长度："+cypherText.length() + "， 数据：" + cypherText);
//        System.out.println("明文 长度："+plainText.length() + "， 数据：" + plainText + "， 相等：" + plainText.equals(SMALL_TEST));
//
//        Cipher cipher1 = Cipher.getInstance("RSA");
//        cipher1.init(Cipher.ENCRYPT_MODE, cipher.getPrivateKey());
//        String cypherText1 = HexString.bytes2HexString(cipher1.doFinal(SMALL_TEST.getBytes(CHARSET)));
//        cipher1.init(Cipher.DECRYPT_MODE, cipher.getPublicKey());
//        String plainText1 = new String(cipher1.doFinal(HexString.hexString2Bytes(cypherText)), CHARSET);
//        System.out.println("密文 长度："+ cypherText1.length() + "， 数据：" + cypherText1 + "， 相等：" + cypherText1.equals(cypherText));
//        System.out.println("明文 长度："+plainText1.length() + "， 数据：" + plainText1 + "，相等：" + plainText1.equals(plainText));
//    }

    private RSAUtils(){}
}
