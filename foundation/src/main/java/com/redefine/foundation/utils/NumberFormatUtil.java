package com.redefine.foundation.utils;


/**
 * 格式化数字
 * Created by nianguowang on 2018/8/9
 */
public class NumberFormatUtil {
    private NumberFormatUtil() {}

    public static String formatNumber(long number) {
        if (number <= 0) {
            return "0";
        } else if (number < 9999L) {
            return String.valueOf(number);
        } else if (number < 9999999L) {
            long count = number / 1000L;
            return count + "k";
        } else if (number < 9999999999L) {
            long count = number / 1000000L;
            return count + "m";
        } else {
            return "9999m";
        }
    }

    public static String formatNumber(int number) {
        if (number <= 0) {
            return "0";
        } else if (number < 9999L) {
            return String.valueOf(number);
        } else if (number < 9999999L) {
            long count = number / 1000L;
            return count + "k";
        } else {
            return "9999m";
        }
    }
}
