package com.redefine.welike.base.track;

//import android.app.Application;
//import android.text.TextUtils;
//import android.util.Log;

//import com.google.android.gms.analytics.ExceptionReporter;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;
//import com.redefine.foundation.language.LocalizationManager;
//import com.redefine.foundation.utils.CommonHelper;
//import com.redefine.welike.base.BuildConfig;
//import com.redefine.welike.base.R;
//import com.redefine.welike.base.profile.AccountManager;

/**
 * Created by liwenbo on 2018/3/11.
 */

public class TrackerUtil {
//    private static final String TRACKER_ID = "UA-115041627-1";
//    private static GoogleAnalytics googleAnalytics;
//    private static Tracker sCrashTracker;
//    private static Tracker sPageTracker;
//    private static Tracker sEventTracker;
//    private static Application sContext;
//    public static boolean MODE = true;
//
//    public static void init(Application application,Boolean debug) {
//        sContext = application;
//        googleAnalytics = GoogleAnalytics.getInstance(application);
//        googleAnalytics.setLocalDispatchPeriod(30);
//        googleAnalytics.setDryRun(debug);
//        MODE = debug;
////        if (isDebugType()) {
////            googleAnalytics.setDryRun(true);
////        } else {
////            googleAnalytics.setDryRun(false);
////        }
//
////        Thread.UncaughtExceptionHandler uncaughtHandler = new ExceptionReporter(initCrashTracker(application), Thread.getDefaultUncaughtExceptionHandler(), application);
////        Thread.setDefaultUncaughtExceptionHandler(uncaughtHandler);
//    }
//
//    public static synchronized Tracker initCrashTracker(Application application) {
//        if (sCrashTracker == null) {
//            sCrashTracker = googleAnalytics.newTracker(TRACKER_ID);
//            sCrashTracker.setAppName(application.getResources().getString(R.string.app_name));
//            sCrashTracker.setAppVersion(CommonHelper.getAppVersionName(application));
//            sCrashTracker.enableExceptionReporting(!MODE);
//
////            if (isDebugType()) {
////            } else {
////                sCrashTracker.enableExceptionReporting(true);
////            }
//            sCrashTracker.enableAutoActivityTracking(false);
//        }
//        return sCrashTracker;
//    }
//
//    public static synchronized Tracker getPageTracker() {
//        if (sPageTracker == null) {
//            sPageTracker = googleAnalytics.newTracker(R.xml.analytics_page);
//            sPageTracker.setAppName(sContext.getResources().getString(R.string.app_name));
//            sPageTracker.setAppVersion(CommonHelper.getAppVersionName(sContext));
//            sPageTracker.set("isLoginIn", String.valueOf(AccountManager.getInstance().isLogin()));
//            sPageTracker.set("language", LocalizationManager.getInstance().getCurrentLanguage());
//            sPageTracker.enableExceptionReporting(false);
//            sPageTracker.enableAutoActivityTracking(true);
//        }
//        return sPageTracker;
//    }
//
//    public static synchronized Tracker getEventTracker() {
//        if (sEventTracker == null) {
//            sEventTracker = googleAnalytics.newTracker(R.xml.analytics_event);
//            sEventTracker.setAppName(sContext.getResources().getString(R.string.app_name));
//            sEventTracker.setAppVersion(CommonHelper.getAppVersionName(sContext));
//            sEventTracker.set("isLoginIn", String.valueOf(AccountManager.getInstance().isLogin()));
//            sEventTracker.set("language", LocalizationManager.getInstance().getCurrentLanguage());
//            sEventTracker.enableExceptionReporting(false);
//            sEventTracker.enableAutoActivityTracking(false);
//        }
//        return sEventTracker;
//    }

//    public static boolean isDebugType() {
//        return !TextUtils.equals(BuildConfig.BUILD_TYPE, "release");
//    }

}
