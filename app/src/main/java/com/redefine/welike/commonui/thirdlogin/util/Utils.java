package com.redefine.welike.commonui.thirdlogin.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by nianguowang on 2018/7/19
 */
public class Utils {

    private Utils() {}

    public static boolean isTruecallerInstalled(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.truecaller", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
}
