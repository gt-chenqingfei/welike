package com.redefine.welike.hive;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.redefine.commonui.share.request.ReportRequest;
import com.redefine.foundation.utils.ChannelHelper;
import com.redefine.welike.AppConfig;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.common.Life;
import com.redefine.welike.common.LifeListener;
import com.redefine.welike.statistical.EventLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import static com.redefine.welike.kext.SPExtsKt.spFireOnce;

/**
 * Created by daining on 2018/4/9.
 * <p>
 * https://app.appsflyer.com/com.redefine.welike?pid=share&c=facebook&clickid=1&af_sub1=xxx&af_sub2=yyy
 * <p>
 * welike://com.redefine.welike/track?c=facebook&clickid=1&af_sub1=xxx&af_sub2=yyy
 */

public class AppsFlyerManager {
    final public static String EVENT_REGISTER = "EVENT_REGISTER";
    final public static String EVENT_OPEN = "EVENT_OPEN";
    final public static String EVENT_OPEN_7D = "EVENT_OPEN_7D";
    final public static String EVENT_POST = "EVENT_POST";
    final public static String EVENT_LIKE = "EVENT_LIKE";
    final public static String EVENT_COMMENT = "EVENT_COMMENT";
    final public static String EVENT_UPLOAD_AVATAR = "EVENT_UPLOAD_AVATAR";
    final public static String EVENT_FOLLOW = "EVENT_FOLLOW";
    final public static String EVENT_MESSAGE = "EVENT_MESSAGE";
    final public static String EVENT_FORWARD = "EVENT_FORWARD";
    final public static String EVENT_SIGN = "EVENT_SIGN";


    @StringDef({EVENT_REGISTER, EVENT_OPEN, EVENT_OPEN_7D, EVENT_POST, EVENT_LIKE, EVENT_COMMENT, EVENT_UPLOAD_AVATAR, EVENT_FOLLOW, EVENT_MESSAGE, EVENT_FORWARD, EVENT_SIGN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    private static AppsFlyerManager instance;

    private SharedPreferences sp;

    public static synchronized AppsFlyerManager getInstance() {
        if (instance == null) {
            instance = new AppsFlyerManager();
        }
        return instance;
    }

    public AppsFlyerManager() {

    }

    public void init(final Application application) {
//        Stetho.initializeWithDefaults(application);
        sp = application.getSharedPreferences("Referrer", Context.MODE_PRIVATE);
        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {
                for (String attrName : map.keySet()) {
                    Log.w("DDAI", "AppsFlyerManager.attribute: " + attrName + " = " + map.get(attrName));
                }
                String isFBs = map.get("is_fb");
                boolean isFB = (isFBs != null && isFBs.equalsIgnoreCase("true"));
                if (isFB) {
                    //FB 归因信息
                    handleFacebookAD(map, application);
                } else {
                    String share = map.get("media_source");
                    if (!TextUtils.isEmpty(share) && share.equalsIgnoreCase("share")) {
                        //保存分享回流
                        String from = map.get("af_status");
                        String mediaSource = map.get("media_source");
                        String aid = map.get("advertising_id");
                        String arg1 = map.get("af_sub1");
                        String arg2 = map.get("af_sub2");
                        String arg3 = map.get("af_sub3");
                        String set = map.get("af_adset");
                        String campaign = map.get("campaign");

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("from", from == null ? "" : from);
                            jsonObject.put("source", set == null ? "" : set);
                            jsonObject.put("device", DeviceTool.getAndroidID(MyApplication.getAppContext()));
                            jsonObject.put("post_id", arg2 == null ? "" : arg2);
                            jsonObject.put("shard_uid", arg1 == null ? "" : arg1);
                            jsonObject.put("media_source", mediaSource == null ? "" : mediaSource);
                            jsonObject.put("advertising_id", aid == null ? "" : aid);
                            jsonObject.put("uri", arg3 == null ? "" : arg3);
                            jsonObject.put("channel", campaign == null ? "" : campaign);
                            sp.edit().putString("record", jsonObject.toString()).apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //判断gp 归因数据
                        handleGoogleAD(map, application);
                    }
                }
            }

            @Override
            public void onInstallConversionFailure(String s) {
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        };

        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().init(AppConfig.AF_DEV_KEY, conversionDataListener);
        AppsFlyerLib.getInstance().startTracking(application);

        Life.regListener(new LifeListener() {
            @Override
            public void onFire(int event) {
                if (event == Life.LIFE_REGISTER_FINISH) {
                    addEvent(EVENT_REGISTER);
                }
            }
        });
    }

    private static void handleFacebookAD(Map<String, String> map, Context context) {
        String fb_campaign_id = map.get("campaign_id");
        String fb_adset = map.get("adset");
        String fb_adset_id = map.get("adset_id");
        String fb_ad_id = map.get("ad_id");
        String adgroup = map.get("adgroup");
        try {
            String[] splited = fb_adset.split("__");
            String tag = "";
            String adTag = "";
            if (splited != null && splited.length > 0) {
                tag = splited[0];
            }
            String[] adGroups = adgroup.split("__");
            if (adGroups != null && adGroups.length > 0) {
                adTag = adGroups[0];
            }
            ChannelHelper.saveAF(tag, adTag, context);
            EventLog.UnLogin.report18(fb_campaign_id, fb_adset, fb_adset_id, adgroup, fb_ad_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleGoogleAD(Map<String, String> map, Context context) {
        String campaign = map.get("campaign");
        try {
            if (!TextUtils.isEmpty(campaign) && campaign.contains("__google")) {
                String[] words = campaign.split("__");
                String adSet = "";
                String adGroup = "";
                if (words.length > 1) {
                    adSet = words[1];
                }
                if (words.length > 2) {
                    adGroup = words[2];
                }
                ChannelHelper.saveAF(adSet, adGroup, context);
                EventLog.UnLogin.report18("", adSet, "", adGroup, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void report() {
        Account account = AccountManager.getInstance().getAccount();
        String record = sp.getString("record", "");
        if (!TextUtils.isEmpty(record)) {
            try {
                JSONObject jo = new JSONObject(record);
                if (TextUtils.isEmpty(jo.getString("shard_uid"))) {
                    return;
                }
                jo.put("link_uid", account == null ? "" : account.getUid());
                new ReportRequest(MyApplication.getAppContext(), jo).req(new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                    }

                    @Override
                    public void onSuccess(BaseRequest request, com.alibaba.fastjson.JSONObject result) throws Exception {
                    }
                });

            } catch (Exception e) {
            }
        }
    }

    public String getReferrerId() {
        String referrerId = "";
        String record = sp.getString("record", "");
        try {
            JSONObject jo = new JSONObject(record);
            referrerId = jo.getString("shard_uid");
        } catch (Exception e) {

        }
        return referrerId;
    }

    public String getReferrerUri() {
        String uri = "";
        String record = sp.getString("record", "");
        try {
            JSONObject jo = new JSONObject(record);
            uri = jo.getString("uri");
            jo.put("uri", "");
            sp.edit().putString("record", jo.toString()).apply();
        } catch (Exception e) {

        }
        return uri;
    }

    public static void addEvent(@Event String event) {
        Context context = MyApplication.getAppContext();
        AppsFlyerLib.getInstance().trackEvent(context, event, new HashMap<String, Object>());
    }

    private final String CHECK_OPEN = "CHECK_OPEN";
    private final String CHECK_OPEN_7_DAY = "CHECK_OPEN_7_DAY";

    /**
     * 检查 第二天打开
     */
    public void checkOpen(Context context) {
        Date start = new Date(getInstallTime(context));
        Date end = new Date(System.currentTimeMillis());
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        int between = endCalendar.get(Calendar.DAY_OF_YEAR) - startCalendar.get(Calendar.DAY_OF_YEAR);
        if (between == 1) {
            spFireOnce(sp, CHECK_OPEN, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    addEvent(AppsFlyerManager.EVENT_OPEN);
                    return null;
                }
            });
        }
        if (between == 7) {
            spFireOnce(sp, CHECK_OPEN_7_DAY, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    addEvent(EVENT_OPEN_7D);
                    return null;
                }
            });
        }


    }


    private static long getInstallTime(Context context) {
        long time = 0;
        try {
            time = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).firstInstallTime;
        } catch (Exception e) {
        }
        return time;
    }


}