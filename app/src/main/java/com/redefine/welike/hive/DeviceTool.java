package com.redefine.welike.hive;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by daining on 2018/4/10.
 */

public class DeviceTool {

//    @SuppressLint("HardwareIds")
//    public static String getIMEI(Context context) {
//        String value = null;
//        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (manager != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                value = manager.getImei();
//            } else {
//                value = manager.getDeviceId();
//            }
//        } else {
//        }
//        return TextUtils.isEmpty(value) ? "" : value;
//
//    }


    public static String getAndroidID(Context context) {
        String value = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return TextUtils.isEmpty(value) ? "" : value;
    }
}
