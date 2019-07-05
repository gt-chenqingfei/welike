package com.redefine.commonui.h5;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.redefine.foundation.utils.CommonHelper;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;

import java.util.Locale;

/**
 * Created by nianguowang on 2018/5/18
 */
public enum PublicParamProvider {

    INSTANCE;

    public static final String OS_ANDROID = "android";

    public static final String CHANNEL_GP = "gp";

    public static final String ENV_WELIKE = "welike";

//    public static final String META_DATA_CHANNEL_KEY = "Builtin_Channel";

    private Context mContext;

    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }

        mContext = context.getApplicationContext();
    }

    public String getChannel() {
        if (mContext == null) {
            throw new RuntimeException("You should call init() first!");
        }
//        ApplicationInfo info;
//        try {
//            info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return CHANNEL_GP;
//        }
//        if (info == null || info.metaData == null) {
//            return CHANNEL_GP;
//        }
        return CommonHelper.getChannel(mContext);
    }

    public String getOsAndroid() {
        return OS_ANDROID;
    }

    public String getAppVersion() {
        if (mContext == null) {
            throw new RuntimeException("You should call init() first!");
        }
        return CommonHelper.getAppVersionName(mContext);
    }

    public String getLanguage() {
        return LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
    }

    public String getCountry() {
        if (mContext == null) {
            throw new RuntimeException("You should call init() first!");
        }
        Locale locale = mContext.getResources().getConfiguration().locale;
        return locale.getCountry();
    }

    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getEnvWelike() {
        return ENV_WELIKE;
    }

    public String getDeviceId() {
        return CommonHelper.getDeviceId(mContext);
    }

    public String getLogin() {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null &&
                account.getCompleteLevel() >= Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE &&
                account.isLogin()) {

            if (account.getStatus() == Account.ACCOUNT_HALF)
                return "3";

            else if (account.getStatus() == Account.ACCOUNT_DEACTIVATE)
                return "2";
            else if (account.getStatus() == Account.ACCOUNT_BLOCKED)
                return "4";
            else
                return "1";
        } else {
            return "0";
        }
    }


}
