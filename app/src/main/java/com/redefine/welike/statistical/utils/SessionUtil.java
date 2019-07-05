package com.redefine.welike.statistical.utils;

import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by nianguowang on 2018/5/28
 */
public class SessionUtil {

    private static final long SESSION_EFFECTIVE_TIME = 30 * 60 * 1000;
    private static String sSession = "";
    private static long sSessionGenerateTime = 0;

    private SessionUtil() {}

    public static String getSession() {
        if(TextUtils.isEmpty(sSession) || sSessionGenerateTime == 0) {
            generateSession();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if(currentTimeMillis - sSessionGenerateTime > SESSION_EFFECTIVE_TIME) {
                generateSession();
            }
        }
        return sSession;
    }

    private static void generateSession() {
        sSession = UUID.randomUUID().toString();
        sSessionGenerateTime = System.currentTimeMillis();
    }
}
