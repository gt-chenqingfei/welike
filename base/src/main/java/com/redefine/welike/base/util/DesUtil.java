package com.redefine.welike.base.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author redefine honlin
 * @Date on 2018/10/30
 * @Description
 */
public class DesUtil {

    /**
     * 加密
     *
     * @param content
     *            待加密内容
     * @param key
     *            加密的密钥
     * @return
     */
    public static byte[] encrypt(String content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);
            byte[] result = cipher.doFinal(content.getBytes());
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将byte数组转换为16进制值表示的字符串
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception
     */
    public static String bin2hex(byte[] bytes) throws Exception {
        int bytesLength = bytes.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(bytesLength * 2);
        for (int i = 0; i < bytesLength; i++) {
            int intTmp = bytes[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 解密
     *
     * @param content
     *            待解密内容
     * @param key
     *            解密的密钥
     * @return
     */
    public static String decrypt(byte[] content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secureKey, random);
            byte[] result = cipher.doFinal(content);
            return new String(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


}
