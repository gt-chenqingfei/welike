package com.redefine.foundation.language;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by liwenbo on 2018/2/7.
 */

public class LocalizationManager {


    public static final String LANGUAGE_TYPE_ENG = "en";
    public static final String LANGUAGE_TYPE_HINDI = "hi";
    public static final String LANGUAGE_TYPE_GUJARATI = "gu";
    public static final String LANGUAGE_TYPE_PUNJABI = "pa";
    public static final String LANGUAGE_TYPE_KANNADA = "kn";
    public static final String LANGUAGE_TYPE_BENGALI = "bn";
    public static final String LANGUAGE_TYPE_MARATHI = "mr";
    public static final String LANGUAGE_TYPE_TELUGU = "te";
    public static final String LANGUAGE_TYPE_MALAYALAM = "ml";

    public static final ArrayList<String> allLanguage = new ArrayList<>();
    public static final String[] all = {LANGUAGE_TYPE_ENG,LANGUAGE_TYPE_HINDI, LANGUAGE_TYPE_GUJARATI,
            LANGUAGE_TYPE_PUNJABI, LANGUAGE_TYPE_KANNADA,
            LANGUAGE_TYPE_BENGALI, LANGUAGE_TYPE_MARATHI,
            LANGUAGE_TYPE_TELUGU, LANGUAGE_TYPE_MALAYALAM};


    private static LocalizationManager instance;

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
            allLanguage.add(LANGUAGE_TYPE_HINDI);
            allLanguage.add(LANGUAGE_TYPE_GUJARATI);
            allLanguage.add(LANGUAGE_TYPE_PUNJABI);
            allLanguage.add(LANGUAGE_TYPE_KANNADA);
            allLanguage.add(LANGUAGE_TYPE_BENGALI);
            allLanguage.add(LANGUAGE_TYPE_MARATHI);
            allLanguage.add(LANGUAGE_TYPE_TELUGU);
            allLanguage.add(LANGUAGE_TYPE_MALAYALAM);
        }
        return instance;
//        return LocalizationManagerHolder.INSTANCE;
    }

    public static void init(Context app, String language, ILanguageSettingProvider provider) {
        getInstance().mAppContext = app;
        getInstance().mLanguageSettingProvider = provider;
        getInstance().setCurrentLanguage(language);
    }

    public static final String SYSTEM_LANGUAGE_TYPE_HINDI = "hi_IN";
    public static final String SYSTEM_LANGUAGE_TYPE_HINDI2 = "hi";
    private Context mAppContext;
    private ILanguageSettingProvider mLanguageSettingProvider;
    private String mCurrentLanguage = LANGUAGE_TYPE_ENG;
//    private Set<ILanguageChangeObserver> mLanguageChangeObservers = new HashSet<>();

    private void setCurrentLanguage(String language) {
        mCurrentLanguage = TextUtils.isEmpty(language) ? getSystemLanguage() : language;
    }

    public static Context getAppContext() {
        return getInstance().mAppContext;
    }

    public void setmAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    private String getSystemLanguage() {
        Configuration config = mAppContext.getResources().getConfiguration();
        Locale sysLocale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }
        if (sysLocale == null) {
            return LANGUAGE_TYPE_ENG;
        }
        String language = sysLocale.getLanguage();
        if (TextUtils.isEmpty(language)) {
            language = LANGUAGE_TYPE_ENG;
        } else {
            language = filterSystemLanguage(language);
        }
        return language;
    }

    private String filterSystemLanguage(String language) {
        if (language.equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
            return LANGUAGE_TYPE_ENG;
        } else if (language.equalsIgnoreCase(SYSTEM_LANGUAGE_TYPE_HINDI) ||
                language.equalsIgnoreCase(SYSTEM_LANGUAGE_TYPE_HINDI2)) {
            return LANGUAGE_TYPE_HINDI;
        }
        return LANGUAGE_TYPE_ENG;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }


    public String getCurrentLanguage() {
        return mCurrentLanguage;
    }

    public String getEnglishLanguage() {
        return LANGUAGE_TYPE_ENG;
    }

    public void changeLanguage(Context context, String language) {
        if (!TextUtils.equals(mCurrentLanguage, language)) {
            mCurrentLanguage = language;
            ResourceParser.clearCache();
//            for (ILanguageChangeObserver observer : mLanguageChangeObservers) {
//                observer.onLanguageChange(mCurrentLanguage);
//            }
        }
        if (mLanguageSettingProvider != null) {
            mLanguageSettingProvider.doSetSettingLanguage(language);
        }

//        updateResource(context, getSetLanguageLocale(language));
        updateResource(context.getApplicationContext(), getSetLanguageLocale(language));


    }

//    public void registerLanguageChangeObserver(ILanguageChangeObserver observer) {
//        mLanguageChangeObservers.add(observer);
//    }
//
//    public void unRegisterLanguageChangeObserver(ILanguageChangeObserver observer) {
//        mLanguageChangeObservers.remove(observer);
//    }


//------------------------------------------------------------------------------------//

    private static Locale enLocale = new Locale("en", "en");
    private static Locale hiLocale = new Locale("hi", "hi");
    private static Locale guLocale = new Locale("gu", "gu");
    private static Locale paLocale = new Locale("pa", "pa");
    private static Locale knLocale = new Locale("kn", "kn");
    private static Locale bnLocale = new Locale("bn", "bn");
    private static Locale maLocale = new Locale("mr", "mr");
    private static Locale teLocale = new Locale("te", "te");
    private static Locale mlLocale = new Locale("ml", "ml");

    private static Locale getSetLanguageLocale(String language) {
//        mCurrentLanguage = TextUtils.isEmpty(language) ? getSystemLanguage() : language;

        if (TextUtils.isEmpty(language)) {
            return enLocale;
        }

//        if (TextUtils.equals(language, LANGUAGE_TYPE_ENG))
//            return enLocale;

        if (TextUtils.equals(language, LANGUAGE_TYPE_HINDI))

            return hiLocale;

        if (TextUtils.equals(language, LANGUAGE_TYPE_GUJARATI))

            return guLocale;

        if (TextUtils.equals(language, LANGUAGE_TYPE_BENGALI))

            return bnLocale;

        if (TextUtils.equals(language, LANGUAGE_TYPE_KANNADA))

            return knLocale;

        if (TextUtils.equals(language, LANGUAGE_TYPE_PUNJABI))

            return paLocale;
        if (TextUtils.equals(language, LANGUAGE_TYPE_MARATHI))

            return maLocale;
        if (TextUtils.equals(language, LANGUAGE_TYPE_TELUGU))

            return teLocale;
        if (TextUtils.equals(language, LANGUAGE_TYPE_MALAYALAM))

            return mlLocale;
        return enLocale;


    }


    public static Context wrapContext(Context context) {
//        val config = context.resources.configuration
//        config.locale = current
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            context.createConfigurationContext(config)
//        } else {
//            //TODO("VERSION.SDK_INT < JELLY_BEAN_MR1")
//            context
//        }

        String language = getInstance().mCurrentLanguage;

        if (TextUtils.isEmpty(language)) {
            language = getInstance().getSystemLanguage();
        }

        return wrapResource(context, getSetLanguageLocale(language));
    }


    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }


    private static Context wrapResource(Context context, Locale locale) {

//        Locale.setDefault(locale);

        Resources res = context.getResources();

        Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                res.getConfiguration().setLocales(new LocaleList(locale));
//            }
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }


    private static Context updateResource(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();

        res.getConfiguration().locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            res.getConfiguration().setLocale(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                res.getConfiguration().setLocales(new LocaleList(locale));
            }
        }
        context.getResources().updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
        return context;
    }

}