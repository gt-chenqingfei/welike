package com.redefine.welike.business.startup.management.constant;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.business.startup.management.bean.LanguageBean;

import java.util.ArrayList;

/**
 * @author redefine honlin
 * @Date on 2018/10/26
 * @Description
 */
public class LanguageConstant {

    public static ArrayList<LanguageBean> initData() {

        ArrayList<LanguageBean> languageBeans = new ArrayList<>();

        languageBeans.add(new LanguageBean(R.drawable.ic_language_hindi, R.drawable.icon_active_language_hi, 0, LocalizationManager.LANGUAGE_TYPE_HINDI, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_HINDI), true));

        languageBeans.add(new LanguageBean(R.drawable.ic_language_gujarati, R.drawable.icon_active_language_gu, 0, LocalizationManager.LANGUAGE_TYPE_GUJARATI, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_GUJARATI), true));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_bengali, R.drawable.icon_active_language_be, 0, LocalizationManager.LANGUAGE_TYPE_BENGALI, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_BENGALI), true));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_punjabi, R.drawable.icon_active_language_pu, 0, LocalizationManager.LANGUAGE_TYPE_PUNJABI, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_PUNJABI), true));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_marathi, R.drawable.icon_active_language_ma, 0, LocalizationManager.LANGUAGE_TYPE_MARATHI, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_MARATHI), true));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_telugu, R.drawable.icon_active_language_te, 0, LocalizationManager.LANGUAGE_TYPE_TELUGU, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_TELUGU), true));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_malayalam, R.drawable.icon_active_language_ml, 0, LocalizationManager.LANGUAGE_TYPE_MALAYALAM, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_MALAYALAM), true));

        languageBeans.add(new LanguageBean(R.drawable.ic_language_kannada, R.drawable.icon_active_language_ka, R.drawable.icon_un_active_language_ka, LocalizationManager.LANGUAGE_TYPE_KANNADA, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_KANNADA), false));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_tamil, R.drawable.icon_active_language_ta, R.drawable.icon_un_active_language_ta, "Tamil", false, false));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_bhojpuri, R.drawable.icon_active_language_bh, R.drawable.icon_un_active_language_bh, "Bhojpuri", false, false));
        languageBeans.add(new LanguageBean(R.drawable.ic_language_odisha, R.drawable.icon_active_language_od, R.drawable.icon_un_active_language_od, "Odisha", false, false));

        languageBeans.add(new LanguageBean(R.drawable.ic_language_english, R.drawable.icon_active_language_en, 0, LocalizationManager.LANGUAGE_TYPE_ENG, LanguageSupportManager.getInstance().currentLanguageChoosed(LocalizationManager.LANGUAGE_TYPE_ENG), true));

        return languageBeans;

    }


    @DrawableRes
    public static int getLanguageSrc(String language) {

        if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_HINDI)) {
            return R.drawable.ic_language_hi_icon;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_GUJARATI)) {
            return R.drawable.language_gu_icon;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_BENGALI)) {
            return R.drawable.language_be_icon;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_KANNADA)) {
            return R.drawable.language_ka_iocn;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_PUNJABI)) {
            return R.drawable.language_pu_icon;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_MARATHI)) {
            return R.drawable.language_ma_icon;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_MALAYALAM)) {
            return R.drawable.ic_language_ml_icon;
        } else if (TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_TELUGU)) {
            return R.drawable.ic_language_te_icon;
        } else {
            return R.drawable.ic_language_en_icon;
        }
    }


    /**
     * discover 排行榜的入口
     * 目前只支持 一种语言
     *
     * @param language
     * @return
     */
    public static String getLanguageSrcWithLottie(String language) {
        return "ranking_top/data.json";
    }

}
