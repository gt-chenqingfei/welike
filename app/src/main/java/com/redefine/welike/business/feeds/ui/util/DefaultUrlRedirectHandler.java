package com.redefine.welike.business.feeds.ui.util;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.commonui.h5.UrlParamAdder;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.event.AvoidRouteDispatcher;
import com.redefine.welike.event.RouteDispatcher;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultUrlRedirectHandler {

    public static final String FROM_BANNER = "banner";
    public static final String FROM_TOP_USER = "topUser";
    public static final String FROM_SHAKE = "shake";
    public static final String FROM_SPLASH = "splash";
    public static final String FROM_INFLUENCER = "influencer";
    public static final String FROM_TASK = "task";
    public static final String FROM_IM = "im";
    public static final String FROM_PUBLISH = "publish";
    public static final String FROM_PROFILE = "profile";
    public static final String FROM_AD = "AD";
    public static final String FROM_ACTIVE = "Active";
    public static final String FROM_FEEDBACK = "feedback";

    @StringDef({FROM_BANNER, FROM_TOP_USER, FROM_SHAKE, FROM_SPLASH, FROM_INFLUENCER, FROM_TASK, FROM_IM, FROM_PUBLISH, FROM_PROFILE,FROM_AD,FROM_ACTIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WebViewFrom {
    }

    private final String mFrom;
    private final Context mContext;
    private Map<String, String> mToAddedParams;

    public DefaultUrlRedirectHandler(Context context, @WebViewFrom String from) {
        mContext = context;
        mFrom = from;
        mToAddedParams = new LinkedHashMap<>();
        mToAddedParams.put("from", from);
    }

    public void onUrlRedirect(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Uri uri = Uri.parse(url);
        if (TextUtils.isEmpty(uri.getHost())) {
            // invalid  uri
            Bundle bundle = new Bundle();
            bundle.putString(WebViewConstant.KEY_URL, URLCenter.getHost() + url);
            bundle.putString(WebViewConstant.KEY_FROM, mFrom);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
        } else {
            if (url.startsWith(RouteConstant.WELIKE_SCHEME)) {

                Account account = AccountManager.getInstance().getAccount();
                String newUrl = new UrlParamAdder(mToAddedParams).addParam(url);
                if (account == null || account.isLogin()) {
                    AvoidRouteDispatcher.avoidRouteDispatcher(newUrl);
                } else {
                    RouteDispatcher.routeDispatcher(newUrl, mContext);
                }
//                ARouter.getInstance().build(Uri.parse(url)).navigation(mContext);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(WebViewConstant.KEY_URL, url);
                bundle.putString(WebViewConstant.KEY_FROM, mFrom);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
            }
        }
    }
}
