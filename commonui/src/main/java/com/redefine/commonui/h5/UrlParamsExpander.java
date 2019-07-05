package com.redefine.commonui.h5;

import android.net.Uri;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.util.UriUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nianguowang on 2018/5/14
 */
public class UrlParamsExpander {

    private static final String URL_PARAM_WELIKE_PARAM = "welike_params";

    private static final String URL_PARAM_UID = "dn";
    private static final String URL_PARAM_TOKEN = "to";
    private static final String URL_PARAM_COUNTRY = "co";
    private static final String URL_PARAM_APP_VERSION = "av";//APP版本
    private static final String URL_PARAM_CHANNEL = "ch";
    private static final String URL_PARAM_LANGUAGE = "la";
    private static final String URL_PARAM_OS_VERSION = "ov";
    private static final String URL_PARAM_OS = "os";
    private static final String URL_PARAM_ENVIRONMENT = "en";
    private static final String URL_PARAM_DEVICE_ID = "de";
    private static final String URL_PARAM_LOGIN = "lo";
    private static final String URL_PARAM_FROM = "fr";

    private final WhiteListManager mWhiteListManager;
    private final Map<String, String> mSupportParamMap;

    public UrlParamsExpander() {
        mWhiteListManager = new WhiteListManager();
        mSupportParamMap = new HashMap<>();
        initSupportParams();
    }

    private void initSupportParams() {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            mSupportParamMap.put(URL_PARAM_UID, account.getUid());
            mSupportParamMap.put(URL_PARAM_TOKEN, account.getAccessToken());
        }
        mSupportParamMap.put(URL_PARAM_COUNTRY, PublicParamProvider.INSTANCE.getCountry());
        mSupportParamMap.put(URL_PARAM_APP_VERSION, PublicParamProvider.INSTANCE.getAppVersion());
        mSupportParamMap.put(URL_PARAM_CHANNEL, PublicParamProvider.INSTANCE.getChannel());
        mSupportParamMap.put(URL_PARAM_LANGUAGE, PublicParamProvider.INSTANCE.getLanguage());
        mSupportParamMap.put(URL_PARAM_OS_VERSION, PublicParamProvider.INSTANCE.getOsVersion());
        mSupportParamMap.put(URL_PARAM_OS, PublicParamProvider.INSTANCE.getOsAndroid());
        mSupportParamMap.put(URL_PARAM_ENVIRONMENT, PublicParamProvider.INSTANCE.getEnvWelike());
        mSupportParamMap.put(URL_PARAM_DEVICE_ID, PublicParamProvider.INSTANCE.getDeviceId());
        mSupportParamMap.put(URL_PARAM_LOGIN, PublicParamProvider.INSTANCE.getLogin());
    }

    public String expandParams(String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (!mWhiteListManager.inWhiteList(host)) {
            return url;
        }

        String welikeParams = uri.getQueryParameter(URL_PARAM_WELIKE_PARAM);
        List<String> params = parseWelikeParams(welikeParams);
        Map<String, String> expandParams = filterWelikeParams(params);
        return buildWelikeParams(uri, expandParams);
    }

    private String buildWelikeParams(Uri originalUri, Map<String, String> expandParams) {
        if (originalUri == null) {
            return null;
        }
        if (expandParams.isEmpty()) {
            return originalUri.toString();
        }

        Map<String, String> newMap = new LinkedHashMap<>();
        Map<String, String> originalMap = UriUtil.splitQueryParameters(originalUri);
        newMap.putAll(originalMap);
        newMap.putAll(expandParams);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(originalUri.getScheme())
                .authority(originalUri.getAuthority())
                .path(originalUri.getPath())
                .encodedFragment(originalUri.getFragment());
        Set<Map.Entry<String, String>> entries = newMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().toString();
    }

    private Map<String, String> filterWelikeParams(List<String> params) {
        if (CollectionUtil.isEmpty(params)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            if (mSupportParamMap.containsKey(param)) {
                map.put(param, mSupportParamMap.get(param));
            }
        }
        return map;
    }

    private List<String> parseWelikeParams(String welikeParams) {
        if (TextUtils.isEmpty(welikeParams)) {
            return Collections.emptyList();
        }

        List<String> params = new ArrayList<>();
        for (int i = 0; i < welikeParams.length(); i += 2) {
            if (i + 2 > welikeParams.length()) {
                break;
            }
            String param = welikeParams.substring(i, i + 2);
            params.add(param);
        }
        return params;
    }

}
