package com.redefine.welike.business.user.management.util;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.common.VipUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by nianguowang on 2018/8/17
 */
public class UserGradeRedirectHandler {

    public void onRedirect() {
        String certifyAddress = VipUtil.getUserGradeAddress();
        if (!TextUtils.isEmpty(certifyAddress)) {
            Uri parse = Uri.parse(certifyAddress);
            Uri.Builder builder = new Uri.Builder();
            Uri url = builder.scheme(parse.getScheme())
                    .authority(parse.getAuthority())
                    .path(parse.getPath())
                    .encodedFragment(parse.getEncodedFragment())
                    .appendQueryParameter("welike_params", "dntocoavchlaovdelofr")
                    .build();

            Bundle bundle = new Bundle();
            bundle.putString("url", url.toString());
            bundle.putString(WebViewConstant.KEY_FROM, DefaultUrlRedirectHandler.FROM_INFLUENCER);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
        }
    }
}
