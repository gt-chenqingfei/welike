package com.redefine.welike.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.util.UriUtil;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.constant.UserConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Set;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class AvoidRouteDispatcher implements IRouteDispatcher {


    private final IPageStackManager mPageStackManager;

    public AvoidRouteDispatcher(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public boolean enableTo(Intent intent) {
        if (intent == null) {
            return false;
        }
        String u = intent.getStringExtra(ARouter.RAW_URI);
        if (TextUtils.isEmpty(u) && intent.getExtras() == null) {
            return false;
        }
        Bundle bundle = new Bundle();
        String pageName = parserData(u, bundle);

        if (pageName == null) return false;

        return true;
    }

    @Override
    public void handleRouteMessage(Intent intent) {
        if (intent == null) {
            return;
        }
        String u = intent.getStringExtra(ARouter.RAW_URI);
        intent.putExtra(ARouter.RAW_URI,"");
        if (TextUtils.isEmpty(u) && intent.getExtras() == null) {
            return;
        }
        Bundle bundle = new Bundle();
        String pageName;
        pageName = parserData(u, bundle);

        if (pageName == null) return;

        if (bundle.containsKey(RouteConstant.ROUTE_KEY_POP_TO_ROOT_PAGE)) {
            if (!bundle.getString(RouteConstant.ROUTE_KEY_POP_TO_ROOT_PAGE, RouteConstant.ROUTE_VALUE_ZERO).equals(RouteConstant.ROUTE_VALUE_ZERO)) {
                mPageStackManager.popToRootPage();
            }
        }

        avoidRouteDispatcher(pageName, bundle);
    }


    public static void avoidRouteDispatcher(String u) {
        if (TextUtils.isEmpty(u)) {
            return;
        }
        Bundle bundle = new Bundle();
        String pageName;
        pageName = parserData(u, bundle);
        if (pageName == null) return;

        AvoidRouteDispatcher.avoidRouteDispatcher(pageName, bundle);
    }

    @Nullable
    public static String parserData(String u, Bundle bundle) {
        String pageName;
        if (!TextUtils.isEmpty(u)) {
            Uri uri = Uri.parse(u);
            Map<String, String> resultMap = UriUtil.splitQueryParametersDecode(uri);
            pageName = resultMap.get(RouteConstant.PAGE_NAME);
            if (TextUtils.isEmpty(pageName)) {
                return null;
            }
            Set<Map.Entry<String, String>> entrySet = resultMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        } else {
            pageName = bundle.getString(RouteConstant.PAGE_NAME);
            if (TextUtils.isEmpty(pageName)) {
                return null;
            }
        }
        return pageName;
    }

    public static void avoidRouteDispatcher(String pageName, Bundle bundle) {

        if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_POST_DETAIL)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_TOPIC_LANDING)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_WEBVIEW)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_USER_PROFILE)) {
            bundle.putBoolean(UserConstant.IS_EXPANDED, true);
            bundle.putString(UserConstant.UID, bundle.getString(RouteConstant.ROUTE_KEY_USER_ID));
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));

        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_ACTIVITY_REGISTER)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_REGISTER_ACTIVITY, bundle));
        }
    }
}
