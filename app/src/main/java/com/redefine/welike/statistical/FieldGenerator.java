package com.redefine.welike.statistical;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.redefine.commonui.h5.PublicParamProvider;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.ChannelHelper;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.statistical.utils.GoogleUtil;
import com.redefine.welike.statistical.utils.SessionUtil;
import com.ta.utdid2.device.UTDevice;

import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pub.devrel.easypermissions.EasyPermissions;

public class FieldGenerator {


    private static JSONObject fieldMap = new JSONObject();

    {
        final String channel = CommonHelper.getChannel(MyApplication.getAppContext());
        fieldMap.put(EventConstants.KEY_OS, PublicParamProvider.INSTANCE.getOsAndroid());
        fieldMap.put(EventConstants.KEY_OS_VER, Build.VERSION.RELEASE);
        fieldMap.put(EventConstants.KEY_SDK_VER, String.valueOf(Build.VERSION.SDK_INT));

        fieldMap.put(EventConstants.KEY_TZ, TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));

        fieldMap.put(EventConstants.KEY_CHANNEL, channel);
        fieldMap.put(EventConstants.KEY_MODEL, Build.MODEL);
        fieldMap.put(EventConstants.KEY_VENDOR, Build.BRAND);
        fieldMap.put(EventConstants.KEY_APP_KEY, null);
        fieldMap.put(EventConstants.KEY_IDFA, null);

        fieldMap.put(EventConstants.KEY_MARKET_SOURCE, null);
        fieldMap.put(EventConstants.KEY_APPFLYERS_SOURCE, null);
        fieldMap.put(EventConstants.KEY_UID_SOURCE, null);
        fieldMap.put(EventConstants.KEY_CHANNEL_SOURCE, null);

        fieldMap.put(EventConstants.KEY_IP, null);

        fieldMap.put(EventConstants.KEY_OPEN_SOURCE, null);
        fieldMap.put(EventConstants.KEY_TEST_AREA, null);
        fieldMap.put(EventConstants.KEY_LOG_EXTRA, null);
    }

    FieldGenerator(Context context) {
        init(context);
    }

    private void init(Context context) {
        GoogleUtil.INSTANCE.getGAID(context, new Function1<String, Unit>() {
            @Override
            public Unit invoke(String info) {
                fieldMap.put(EventConstants.KEY_GAID, info);
                return null;
            }
        });
        ABTest.INSTANCE.registerListener(new Function1<JSONObject, Unit>() {
            @Override
            public Unit invoke(JSONObject s) {
                fieldMap.put(EventConstants.KEY_ABT, s);
                return null;
            }
        });
        fieldMap.put(EventConstants.KEY_DEVICE_ID, CommonHelper.getDeviceId(context));
        fieldMap.put(EventConstants.KEY_UTDID, UTDevice.getUtdid(context));

        fieldMap.put(EventConstants.KEY_VERSION_NAME, CommonHelper.getAppVersionName(context));
        fieldMap.put(EventConstants.KEY_VERSION_CODE, CommonHelper.getAppVersion(context));

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        boolean googleServiceAvailable = resultCode == ConnectionResult.SUCCESS;
        fieldMap.put(EventConstants.KEY_GOOGLE_FRAME, googleServiceAvailable ? 1 : 0);
        fieldMap.put(EventConstants.KEY_ISP, CommonHelper.getIsp(context));

        Locale locale = context.getResources().getConfiguration().locale;
        fieldMap.put(EventConstants.KEY_LOCALE, locale.getLanguage());
        fieldMap.put(EventConstants.KEY_COUNTRY, locale.getCountry());

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        fieldMap.put(EventConstants.KEY_RESOLUTION, displayMetrics.heightPixels + "*" + displayMetrics.widthPixels);
        fieldMap.put(EventConstants.KEY_DPI, displayMetrics.densityDpi);

        fieldMap.put(EventConstants.KEY_NET, NetWorkUtil.getNetworkTypeName(context));
        fieldMap.put(EventConstants.KEY_VIDMATE_CHANNEL_TAG, ChannelHelper.getTags(context));

        fieldMap.put(EventConstants.KEY_MAC, "");
        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            try {
                Task<Location> lastLocation = LocationServices.getFusedLocationProviderClient(context).getLastLocation();
                fieldMap.put(EventConstants.KEY_LNG, lastLocation.getResult().getLongitude());
                fieldMap.put(EventConstants.KEY_LAT, lastLocation.getResult().getLatitude());
            } catch (Exception e) {
                //do nothing
            }
        }
    }

    public static JSONObject generateCommon() {
        return fieldMap;
    }

    public static JSONObject generate(Context context) {
        JSONObject runtimeField = new JSONObject();
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            runtimeField.put(EventConstants.KEY_UID, account.getUid());
            runtimeField.put(EventConstants.KEY_NICK_NAME, account.getNickName());
        }
        runtimeField.put(EventConstants.KEY_IS_LOGIN, AccountManager.getInstance().isLogin());
        runtimeField.put(EventConstants.KEY_LA, LanguageSupportManager.getInstance().getCurrentMenuLanguageType());
        runtimeField.put(EventConstants.KEY_LAC, LanguageSupportManager.getInstance().getCurrentContentLanguageType());
        runtimeField.put(EventConstants.KEY_LAD, LanguageSupportManager.getSystemLanguage(context));
        runtimeField.put(EventConstants.KEY_SESSION_ID, SessionUtil.getSession());
        runtimeField.put(EventConstants.KEY_CTIME, System.currentTimeMillis());
        return runtimeField;
    }
}
