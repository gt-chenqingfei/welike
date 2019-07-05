package com.redefine.foundation.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by liubin on 2018/1/5.
 */

public class CommonHelper {
    private static String sid = null;
    private static final String install_key = "welike_install";

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAndroidID(Context context) {
        String androidID = "";
//        try {
//            androidID = Settings.Secure.getString(
//                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        } catch (Exception e) {
//        }
        return androidID;
    }

    public static String generateUUID() {
        String p = UUID.randomUUID().toString();
        return p.replace("-", "");
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getDeviceId(Context context) {

//        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String imei = "";
//        if (tm != null && !TextUtils.isEmpty(tm.getDeviceId())) {
//            imei = tm.getDeviceId().toString();
//        }
        String md5IMEI = null;
        if (!TextUtils.isEmpty(imei)) {
            try {
                md5IMEI = MD5Helper.md5(imei);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String sid = sid(context);
        String md5Sid = null;
        if (!TextUtils.isEmpty(sid)) {
            try {
                md5Sid = MD5Helper.md5(sid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StringBuilder s = new StringBuilder();
        if (!TextUtils.isEmpty(md5IMEI)) {
            s.append(md5IMEI);
        }
        if (!TextUtils.isEmpty(md5Sid)) {
            s.append(md5Sid);
        }

        String ss = s.toString();
        String ssMD5 = null;
        if (!TextUtils.isEmpty(ss)) {
            try {
                ssMD5 = MD5Helper.md5(ss);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ssMD5;
    }

    private synchronized static String sid(Context context) {
        if (TextUtils.isEmpty(sid)) {
            File installation = new File(context.getFilesDir(), install_key);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sid = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sid;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static String getChannel(Context context) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (Throwable e) {
            e.printStackTrace();
            return "gp";
        }
        if (info == null || info.metaData == null) {
            return "gp";
        }
//        val channel: String by lazy { WalleChannelReader.getChannel(MyApplication.getApp()) ?: "" }
        return info.metaData.getString("Builtin_Channel");
//        return ChannelHelper.INSTANCE.getChannel(context);
    }

    public static int getAppType(Context context) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            return 0;
        }
        if (info == null || info.metaData == null) {
            return 0;
        }
        return info.metaData.getInt("welike.app.type", 0);
    }

    public static String getUrlExpand(Context context) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            return "welike";
        }
        if (info == null || info.metaData == null) {
            return "welike";
        }
        return info.metaData.getString("Builtin_URL_EXPAND");
    }

    public static String GAID = "";

    public static String getIsp(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getNetworkOperatorName();
        }
        return "";
    }
}
