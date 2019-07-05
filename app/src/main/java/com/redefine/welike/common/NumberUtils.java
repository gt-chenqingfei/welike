package com.redefine.welike.common;

public class NumberUtils {


    public static String getBrowseNum(long num) {

        if (num > 10000) {
            StringBuilder numberBuilder = new StringBuilder();
            numberBuilder.append(num / 1000);
            numberBuilder.append(".");
            numberBuilder.append(num % 1000 / 100);
            numberBuilder.append("k");
            return numberBuilder.toString();
        } else return String.valueOf(num);

    }


}
