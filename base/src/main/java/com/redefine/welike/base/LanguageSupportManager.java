package com.redefine.welike.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.redefine.foundation.language.LocalizationManager;

import java.util.Locale;

/**
 * Created by liubin on 2018/1/9.
 * <p>
 * change by honlin on 2018/10/16
 * get system language && menu language && content language
 */

public class LanguageSupportManager {

    private String currentMenuLanguageType;

    private String currentContentLanguageType;

    private String currentSystemLanguageType;

//    private static final String SYSTEM_LANGUAGE_TYPE_HINDI = "hi_IN";
//    private static final String SYSTEM_LANGUAGE_TYPE_HINDI2 = "hi";

    private static class LanguageSupportManagerHolder {
        public static LanguageSupportManager instance = new LanguageSupportManager();
    }

    public static LanguageSupportManager getInstance() {
        return LanguageSupportManagerHolder.instance;
    }

    public void init(Context context) {
        currentMenuLanguageType = SpManager.Setting.getCurrentMenuLanguageType(context);

        currentContentLanguageType = SpManager.Setting.getCurrentContentLanguageType(context);

        if (TextUtils.isEmpty(currentContentLanguageType)) {
            currentContentLanguageType = currentMenuLanguageType;
        }

        currentSystemLanguageType = getSystemLanguage(context);
    }

    public void setCurrentContentLanguageType(String currentContentLanguageType, Context context) {
        this.currentContentLanguageType = currentContentLanguageType;
        SpManager.Setting.setCurrentContentLanguageType(currentContentLanguageType, context);
    }

    public String getCurrentContentLanguageType() {
        return currentContentLanguageType;
    }

    public String getCurrentMenuLanguageType() {
        return currentMenuLanguageType;
    }

    public String getCurrentSystemLanguageType() {
        return currentSystemLanguageType;
    }

    public void setCurrentMainLanguageType(String currentMainLanguageType, Context context) {
        if (supportLanguageType(currentMainLanguageType)) {
            SpManager.Setting.setCurrentMenuLanguageType(currentMainLanguageType, context);
            SpManager.Setting.setCurrentContentLanguageType(currentMainLanguageType, context);
            this.currentMenuLanguageType = currentMainLanguageType;
            this.currentContentLanguageType = currentMainLanguageType;
        }
    }

    private static boolean supportLanguageType(String languageType) {
        if (TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_ENG) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_HINDI) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_GUJARATI) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_PUNJABI) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_KANNADA) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_MARATHI) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_MALAYALAM) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_TELUGU) ||
                TextUtils.equals(languageType, LocalizationManager.LANGUAGE_TYPE_BENGALI)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean currentLanguageChoosed(String languageType) {
        if (TextUtils.equals(languageType, getInstance().currentContentLanguageType)) {

            return true;

        }
        return false;

    }


    public static String getSystemLanguage(Context mContext) {
//        return currentSystemLanguageType;
//        Configuration config = mContext.getResources().getConfiguration();
        Locale sysLocale = Resources.getSystem().getConfiguration().locale;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            sysLocale = getSystemLocale(config);
//        } else {
//            sysLocale = getSystemLocaleLegacy(config);
//        }
        return sysLocale == null ? "null" : sysLocale.getLanguage();
//        if (sysLocale == null) {
//            return LocalizationManager.LANGUAGE_TYPE_ENG;
//        }
//        String language = sysLocale.getLanguage();
//        if (TextUtils.isEmpty(language)) {
//            language = LocalizationManager.LANGUAGE_TYPE_ENG;
//        } else {
//            language = filterSystemLanguage(language);
//        }
//        return language;
    }
//
//    private String filterSystemLanguage(String language) {
//        if (language.equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
//            return LocalizationManager.LANGUAGE_TYPE_ENG;
//        } else if (language.equalsIgnoreCase(SYSTEM_LANGUAGE_TYPE_HINDI) ||
//                language.equalsIgnoreCase(SYSTEM_LANGUAGE_TYPE_HINDI2)) {
//            return LocalizationManager.LANGUAGE_TYPE_HINDI;
//        } else if (language.equalsIgnoreCase(LocalizationManager.LANGUAGE_TYPE_GUJARATI)) {
//            return LocalizationManager.LANGUAGE_TYPE_GUJARATI;
//        } else if (language.equalsIgnoreCase(LocalizationManager.LANGUAGE_TYPE_PUNJABI)) {
//            return LocalizationManager.LANGUAGE_TYPE_PUNJABI;
//        } else if (language.equalsIgnoreCase(LocalizationManager.LANGUAGE_TYPE_KANNADA)) {
//            return LocalizationManager.LANGUAGE_TYPE_KANNADA;
//        } else if (language.equalsIgnoreCase(LocalizationManager.LANGUAGE_TYPE_BENGALI)) {
//            return LocalizationManager.LANGUAGE_TYPE_BENGALI;
//        }
//        return LocalizationManager.LANGUAGE_TYPE_ENG;
//    }
//
//    @SuppressWarnings("deprecation")
//    public static Locale getSystemLocaleLegacy(Configuration config) {
//        return config.locale;
//    }
//
//    @TargetApi(Build.VERSION_CODES.N)
//    public static Locale getSystemLocale(Configuration config) {
//        return config.getLocales().get(0);
//    }

}
