package com.redefine.foundation.utils;

import android.os.Build;

/**
 * Created by MR on 2018/1/26.
 */

public class VersionUtil {

    public static boolean isLower4_4() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLowerOrEqual4_4() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isUpperOrEqual5_0() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isLower5_0() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isUpperOrEquql4_2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
