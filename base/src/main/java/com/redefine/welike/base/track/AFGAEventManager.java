package com.redefine.welike.base.track;

import android.content.Context;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.HitBuilders;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.base.profile.AccountManager;

import java.util.HashMap;

/**
 * Created by nianguowang on 2018/7/3
 */
public class AFGAEventManager {

    private Context mContext;

    private AFGAEventManager(){}

    private static AFGAEventManager sInstance = new AFGAEventManager();

    public static AFGAEventManager getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void sendEvent(String event) {
//        TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
//                .setCategory(TrackerConstant.EVENT_EV_CT)
//                .setAction(event)
//                .build());
        AppsFlyerLib.getInstance().trackEvent(mContext, event, new HashMap<String, Object>());
        LogUtil.d("wng", "AFGAEventManager : " + event);
    }

    public void sendAFEvent(String event) {
        AppsFlyerLib.getInstance().trackEvent(mContext, event, new HashMap<String, Object>());
        LogUtil.d("wng", "AppsFlyer event : " + event);
    }

    public void sendEventWithLabel(String event, String label) {
//        TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
//                .setCategory(TrackerConstant.EVENT_EV_CT)
//                .setAction(event)
//                .setLabel(label).build());
        HashMap<String, Object> map = new HashMap<>();
        map.put("label", label);
        map.put("isLogin", AccountManager.getInstance().isLoginComplete());
        map.put("language", LocalizationManager.getInstance().getCurrentLanguage());
        AppsFlyerLib.getInstance().trackEvent(mContext, event, map);
        LogUtil.d("wng", "AFGAEventManager : event -> " + event + ", label -> " + label);
    }
}
