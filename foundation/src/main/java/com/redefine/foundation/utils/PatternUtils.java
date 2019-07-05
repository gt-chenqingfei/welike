package com.redefine.foundation.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * 正则表达式
 * Created by gongguan on 2018/1/5.
 */
public class PatternUtils {
    //身份证
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
    //邮箱
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    //数字
    private static final String REGEX_NUMBER = "^[0-9]*$";
    //URL
    private static final String REGEX_URL = "(http|https)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
    private static final String REGEX_URL_WITHOUT_HTTP = "^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$";

    public PatternUtils() {
    }

    public static Boolean isPhoneNum(String phoneNum) {
        return !TextUtils.isEmpty(phoneNum) && TextUtils.getTrimmedLength(phoneNum) >= 10;
    }

    public static Boolean isIndiaPhoneNum(String phoneNum) {
        return !TextUtils.isEmpty(phoneNum) && TextUtils.getTrimmedLength(phoneNum) >= 10;
    }

    public static Boolean isNum(String num) {
        return Pattern.matches(REGEX_NUMBER, num);
    }

    public static Boolean isEmail(String emailAddr) {
        return Pattern.matches(REGEX_EMAIL, emailAddr);
    }

    public static Boolean isIDcard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url) || Pattern.matches(REGEX_URL_WITHOUT_HTTP, url);
    }

}
