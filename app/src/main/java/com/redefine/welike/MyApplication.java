package com.redefine.welike;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.redefine.foundation.language.ILanguageSettingProvider;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.TopicCardInfo;
import com.redefine.welike.business.init.InitApp;
import com.redefine.welike.common.util.SignatureUtil;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.push.PushService;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by liubin on 2018/1/5.
 */

public class MyApplication extends MultiDexApplication {
    private static Context sContext;
    private static Application mApp;
    public static int appInitServiceCount=0;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        mApp = this;
        if (new SignatureUtil().check(this)) {
            System.out.println("Winter is coming!..!");
        } else {
            System.out.println("A Lannister always pays his debts!..!");
            return;
        }
        if (TextUtils.equals(getCurrentProcessName(this), getPackageName())) {
            InitApp.onCreateApplication(this);
            EventLog.UnLogin.report1();
            AppsFlyerManager.getInstance().init(this);
        }
    }

    public static Application getApp() {
        return mApp;
    }

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(final Context base) {

        LanguageSupportManager.getInstance().init(base);

        LocalizationManager.init(base, LanguageSupportManager.getInstance().getCurrentMenuLanguageType(), new ILanguageSettingProvider() {

            @Override
            public void doSetSettingLanguage(String language) {
                LanguageSupportManager.getInstance().setCurrentMainLanguageType(language, base);
                PushService.INSTANCE.subLanguageTopic();
            }

        });

        super.attachBaseContext(LocalizationManager.wrapContext(base));
        LocalizationManager.getInstance().setmAppContext(getBaseContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalizationManager.getInstance().setmAppContext(LocalizationManager.wrapContext(sContext));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        Log.e("DDAI", "Process = " + pid);
        try {
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (mActivityManager != null) {
                for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                        .getRunningAppProcesses()) {
                    if (appProcess.pid == pid) {
                        return appProcess.processName;
                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }
}
