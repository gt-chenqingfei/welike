package com.redefine.foundation.utils;

import java.security.MessageDigest;

/**
 * Created by liubin on 2018/3/2.
 */

public class MD5Helper {

    public static String md5(String input) throws Exception {
        if (input == null) return null;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArray = input.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int)md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

}
