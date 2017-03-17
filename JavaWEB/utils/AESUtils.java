package com.im_hero.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by Jason on 2017/2/28.
 * AES 对称加密工具类
 */
public final class AESUtils {
    public static final String ALGORITHM = "AES";
    public static final String CHARSET = "UTF-8";

    private static byte[] iv = new byte[] {				//算法参数
            -12,35,-25,65,
            45,-87,95,-22,
            -15,45,-55,66,
            32,-11,84,-55,
    };

    /**
     * 创建一个 AES 加密解密器
     * @param key 密钥
     */
    public static AESCipher create(String key) {
        try {
            return create(key.getBytes(CHARSET), iv);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new Error();
        }
    }

    /**
     * 创建一个 AES 加密解密器
     * @param key 密钥
     * @param iv 算法参数
     */
    public static AESCipher create(byte[] key, byte[] iv) {
        try {
            SecretKey mKey;
            IvParameterSpec mParamSpec;
            Cipher mCipher;

            // 为指定算法生成一个密钥生成器对象。
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            // 使用用户提供的随机源初始化此密钥生成器，使其具有确定的密钥长度。
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(key);
            keyGenerator.init(128, sr);

            // 使用KeyGenerator生成（对称）密钥。
            mKey = keyGenerator.generateKey();
            // 使用iv中的字节作为IV来构造一个 算法参数。
            mParamSpec = new IvParameterSpec(iv);
            // 生成一个实现指定转换的 Cipher 对象
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            return new AESCipher(mCipher, mKey, mParamSpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES 加密解密类，默认使用 UTF-8 编码，注意：对字符串加密，解密时密文是16进制字符串
     */
    public static final class AESCipher{

        private Cipher cipher;
        private SecretKey secretKey;
        private IvParameterSpec ivParameterSpec;

        private AESCipher(Cipher cipher, SecretKey secretKey, IvParameterSpec ivParameterSpec) {
            this.cipher = cipher;
            this.secretKey = secretKey;
            this.ivParameterSpec = ivParameterSpec;
        }

        public void setCipher(Cipher cipher) {
            if(cipher == null) return;
            this.cipher = cipher;
        }

        public void setSecretKey(SecretKey secretKey) {
            if(secretKey == null) return;
            this.secretKey = secretKey;
        }

        public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
            if(ivParameterSpec == null) return;
            this.ivParameterSpec = ivParameterSpec;
        }

        public Cipher getCipher() {
            return cipher;
        }

        public SecretKey getSecretKey() {
            return secretKey;
        }

        public IvParameterSpec getIvParameterSpec() {
            return ivParameterSpec;
        }

        /**
         * 加密
         * @param plainText 明文
         * @return 密文的16进制字符
         */
        public String encrypt(String plainText) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
                return HexString.bytes2HexString(cipher.doFinal(plainText.getBytes(CHARSET)));
            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException
                    | BadPaddingException
                    | UnsupportedEncodingException
                    | IllegalBlockSizeException e) {
                e.printStackTrace();
                return "";
            }
        }

        /**
         * 直接对文件进行加密
         * @param plainIn 明文文件
         * @param cypherOut 密文文件
         * @return 是否成功生成密文文件
         */
        public boolean encryptFile(File plainIn, File cypherOut) throws IOException {
            if (!cypherOut.exists() && !cypherOut.createNewFile()) {
                throw new IOException("密文文件创建失败");
            }
            FileInputStream fis = new FileInputStream(plainIn);
            return encryptStream(fis, new FileOutputStream(cypherOut));
        }

        /**
         * 直接对 IO流 进行加密，输入流读取出来的仍然是明文，输出的才是密文
         * @param plainIn 明文输入流
         * @param cypherOut 密文输出流
         * @return 是否成功读写玩
         */
        public boolean encryptStream(InputStream plainIn, OutputStream cypherOut) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
                CipherOutputStream cos = new CipherOutputStream(cypherOut, cipher);

                byte[] buffer = new byte[8192];
                int len;
                while ((len = plainIn.read(buffer)) != -1) {
                    cos.write(buffer, 0, len);
                    cos.flush();
                }
                plainIn.close();
                cos.close();
                return true;
            } catch (IOException
                    | InvalidAlgorithmParameterException
                    | InvalidKeyException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * 解密
         * @param cypherText 密文的16进制字符
         * @return 明文
         */
        public String decrypt(String cypherText) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                return new String(cipher.doFinal(HexString.hexString2Bytes(cypherText)), CHARSET);
            } catch (InvalidKeyException
                    | IllegalBlockSizeException
                    | UnsupportedEncodingException
                    | BadPaddingException
                    | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
                return "";
            }
        }

        /**
         * 直接对密文文件进行解密
         * @param cypherIn 密文文件
         * @param plainOut 明文文件
         * @return 是否成功生成密文文件
         */
        public boolean decryptFile(File cypherIn, File plainOut) throws IOException {
            if (!plainOut.exists() && !plainOut.createNewFile()) {
                throw new IOException("明文文件创建失败");
            }
            FileInputStream fis = new FileInputStream(cypherIn);
            return decryptStream(fis, new FileOutputStream(plainOut));
        }

        /**
         * 直接对 IO流 进行加密，输入流读取出来的仍然是密文，输出的才是明文
         * @param cypherIn 密文输入流
         * @param plainOut 明文文输出流
         * @return 是否成功读写完
         */
        public boolean decryptStream(InputStream cypherIn, OutputStream plainOut) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                CipherOutputStream cos = new CipherOutputStream(plainOut, cipher);

                byte[] buffer = new byte[8192];
                int len;
                while ((len = cypherIn.read(buffer)) != -1) {
                    cos.write(buffer, 0, len);
                    cos.flush();
                }
                cypherIn.close();
                cos.close();
                return true;
            } catch (IOException
                    | InvalidAlgorithmParameterException
                    | InvalidKeyException e) {
                e.printStackTrace();
                return false;
            }
        }

    }

//    public static void main(String[] args) {
//        AESUtils.AESCipher cipher = AESUtils.create("c3331d08ce22dff651241693ab5147bb11cf3f436f612499938706b0715cc01d");
//        String cypherText = cipher.encrypt("事件多发事件多发事件多发事件多发事件多发事件多发事件多发事件多发");
//
//        System.out.println("长度：" + cypherText.length()+ "， 密文：" + cypherText);
//
//        byte[] cypher = HexString.hexString2Bytes(cypherText);
//        System.out.println("长度：" + cypher.length + "， 数据：" + Arrays.toString(cypher));
//
//        System.out.println("明文：" + cipher.decrypt(cypherText));
//
//
//        try {
//            System.out.println("加密文件：" + cipher.encryptFile(new File("D:\\测试加密明文.txt"), new File("D:\\测试加密密文.txt")));
//            System.out.println("解密文件：" + cipher.decryptFile(new File("D:\\测试加密密文.txt"), new File("D:\\测试加密密文解密后.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private AESUtils(){}
}
