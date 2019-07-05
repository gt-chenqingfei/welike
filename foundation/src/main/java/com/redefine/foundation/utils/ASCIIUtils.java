package com.redefine.foundation.utils;

/**
 * @author redefine honlin
 * @Date on 2018/12/5
 * @Description
 */
public class ASCIIUtils {


    public static String getParams(String content, int num) {

        char[] pams = content.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char a : pams) {
            stringBuilder.append((char) (a + num));
        }
        return stringBuilder.toString();

    }

}
