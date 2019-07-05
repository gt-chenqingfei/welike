package com.redefine.welike.statistical.manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by nianguowang on 2018/5/4
 */
public enum InstallManager {

    INSTANCE;

    private static final String GP_INSTALL_RECEIVER = "com.android.vending.INSTALL_REFERRER";

    private static final String INSTALL_APP_SP = "install_manager";
    private static final String LAST_VERSION = "last_version";

    private static final int INSTALL_TYPE_NEW = 1;
    private static final int INSTALL_TYPE_UPDATE = 2;

    private static final int INSTALL_SOURCE_APK = 1;
    private static final int INSTALL_SOURCE_GP = 2;

    private GpInstallReceiver mReceiver;
    private int installType;
    private int installSource = INSTALL_SOURCE_APK;

    /**
     * 从sp里获取之前存的版本号，如果该版本号为-1，那么认为这是新装。
     * 如果不为-1，从PackageInfo中获取当前版本号，并将其与sp里获取的进行比对：
     *      如果当前版本号大于之前存的版本号，那么认为是一次升级安装。
     * @param context
     */
    public void checkNewInstall(Context context) {

        //获取上一次存的版本号
        SharedPreferences sp = context.getSharedPreferences(INSTALL_APP_SP, Context.MODE_PRIVATE);
        int lastVersion = sp.getInt(LAST_VERSION, -1);

        //获取APP当前的版本号
        int versionCode = -1;
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(lastVersion == -1) {
            installType = INSTALL_TYPE_NEW;
            sendEvent();
            sp.edit().putInt(LAST_VERSION, versionCode).apply();
        } else {
            if(lastVersion < versionCode) {
                installType = INSTALL_TYPE_UPDATE;
                sendEvent();
                sp.edit().putInt(LAST_VERSION, versionCode).apply();
            }
        }

    }

    private void sendEvent() {
        EventLog.InstallApp.report1(installType, installSource);
        EventLog1.InstallApp.report1(installType, installSource);
    }

    public void registerReceiver(Context context) {
        if(context == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GP_INSTALL_RECEIVER);
        if(mReceiver == null) {
            mReceiver = new GpInstallReceiver();
        }
        context.registerReceiver(mReceiver, intentFilter);
    }

    public void unRegisterReceiver(Context context) {
        if(mReceiver != null && context != null) {
            context.unregisterReceiver(mReceiver);
        }
    }

    class GpInstallReceiver extends CampaignTrackingReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) {
                return;
            }
            String action = intent.getAction();
            if(TextUtils.equals(action, GP_INSTALL_RECEIVER)) {
                installSource = INSTALL_SOURCE_GP;
            }
        }
    }

}
