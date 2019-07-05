package com.redefine.welike.statistical.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;

import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.foundation.utils.VersionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.statistical.Config;

/**
 * Created by nianguowang on 2018/5/3
 */
public class ConditionUtil {

    private ConditionUtil() {}

    public static boolean canUpload() {
        return true;
//        LogUtil.d("wng", "memoryUsage : " + memoryUsage() + " , batteryLeft : " + batteryLeft());
//        return memoryUsage() < Config.CONDITION_MEMORY
//                && batteryLeft() > Config.CONDITION_BATTERY
//                && NetWorkUtil.isNetWorkConnected(MyApplication.getAppContext());
    }

    public static float cpuUsage() {
        return 0;
    }

    public static float memoryUsage() {
        try {
            ActivityManager am = (ActivityManager)MyApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long avail = mi.availMem;
            long total = mi.totalMem;
            if(avail != 0 && total != 0) {
                return ((float)total - (float)avail) / (float)total;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int batteryLeft() {
        try {
            if(VersionUtil.isUpperOrEqual5_0()) {
                BatteryManager batteryManager=(BatteryManager) MyApplication.getAppContext().getSystemService(Context.BATTERY_SERVICE);
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 100;
    }

    public static int getAmountSleepTime() {
        NetWorkUtil.NetworkType networkType = NetWorkUtil.getNetworkType(MyApplication.getAppContext());
        if(networkType == NetWorkUtil.NetworkType.NETWORK_WIFI) {
            return Config.UPLOAD_DURATION_AMOUNT_WIFI;
        } else {
            return Config.UPLOAD_DURATION_AMOUNT;
        }
    }

}
