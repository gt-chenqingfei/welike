package com.redefine.welike.event;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.util.UriUtil;
import com.redefine.welike.business.user.ui.constant.UserConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Set;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class BrowseSchemeDispatcher implements IBrowseSchemeDispatcher {


    private final IPageStackManager mPageStackManager;

    public BrowseSchemeDispatcher(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public void handleRouteMessage(String scheme) {
        if (TextUtils.isEmpty(scheme)) {
            return;
        }
        Bundle bundle = new Bundle();
        String pageName;
        Uri uri = Uri.parse(scheme);
        Map<String, String> resultMap = UriUtil.splitQueryParametersDecode(uri);
        pageName = resultMap.get(RouteConstant.PAGE_NAME);
        if (TextUtils.isEmpty(pageName)) {
            return;
        }
        Set<Map.Entry<String, String>> entrySet = resultMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        if (bundle.containsKey(RouteConstant.ROUTE_KEY_POP_TO_ROOT_PAGE)) {
            if (!bundle.getString(RouteConstant.ROUTE_KEY_POP_TO_ROOT_PAGE, RouteConstant.ROUTE_VALUE_ZERO).equals(RouteConstant.ROUTE_VALUE_ZERO)) {
                mPageStackManager.popToRootPage();
            }
        }

        if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_MAIN_HOME)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MAIN_HOME, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_WEBVIEW)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_CHOICE_INTEREST)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHOICE_INTEREST_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_FEED_LIST)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_COMMON_FEED_LIST, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_EDIT_USER_INFO)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MINE_USER_PERSONAL_INFO, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_SHARE)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SHARE_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_USER_PROFILE)) {
            bundle.putString(UserConstant.UID, bundle.getString(RouteConstant.ROUTE_KEY_USER_ID));
            bundle.putBoolean(UserConstant.IS_EXPANDED, true);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_POST_DETAIL)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_TOPIC_LANDING)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_LBS_LANDING)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_LOCATION_NEAR_BY_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_MY_PROFILE)) {
            bundle.putString(UserConstant.UID, AccountManager.getInstance().getAccount().getUid());
            bundle.putBoolean(UserConstant.IS_EXPANDED, true);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_MAIN_MESSAGE)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MAIN_MESSAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_MAIN_DISCOVER)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_DISCOVER_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_MAIN_MINE)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MAIN_MINE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_FOLLOW)) {
            bundle.putString(UserConstant.UID, AccountManager.getInstance().getAccount().getUid());
            bundle.putString(UserConstant.NICK_NAME, AccountManager.getInstance().getAccount().getNickName());
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_FOLLOW_EVENT, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_IM)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PAGE_IM, bundle));
        } else {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MAIN_HOME, bundle));
        }
    }

}
