package com.redefine.foundation.utils;

import android.util.Log;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class LogUtil {

    public static final boolean DEBUG = true;
    private static final String TAG = "LogUtil";

    public static void e(String message) {
        e(TAG, message);
    }

    public static void d(String message) {
        d(TAG, message);
    }

    public static void i(String message) {
        i(TAG, message);
    }

    public static void w(String message) {
        w(TAG, message);
    }

    public static void v(String message) {
        v(TAG, message);
    }

    public static void printToFile(String message) {
        printToFile(TAG, message);
    }

    public static void e(String TAG, String message) {
        if (!DEBUG) {
            return ;
        }
        Log.e(TAG, message);
    }

    public static void d(String TAG, String message) {
        if (!DEBUG) {
            return ;
        }
        Log.d(TAG, message);
    }

    public static void i(String TAG, String message) {
        if (!DEBUG) {
            return ;
        }
        Log.i(TAG, message);
    }

    public static void w(String TAG, String message) {
        if (!DEBUG) {
            return ;
        }
        Log.w(TAG, message);
    }

    public static void v(String TAG, String message) {
        if (!DEBUG) {
            return ;
        }
        Log.v(TAG, message);
    }

    public static void printToFile(String TAG, String message) {
        if (!DEBUG) {
            return ;
        }
        // 统计日志点
    }

    public static void exception(Exception e) {
        e(e.getMessage());
    }
}
