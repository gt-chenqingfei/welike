package com.redefine.welike.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.util.UriUtil;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.SearchEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Set;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class RouteDispatcher implements IRouteDispatcher {


    private final Context context;

    public RouteDispatcher(Context pageStackManager) {
        context = pageStackManager;
    }

    @Override
    public void handleRouteMessage(Intent intent) {
        if (intent == null) {
            return;
        }
        String u = intent.getStringExtra(ARouter.RAW_URI);
        intent.putExtra(ARouter.RAW_URI, "");
        if (TextUtils.isEmpty(u) && intent.getExtras() == null) {
            return;
        }

        Bundle bundle = new Bundle();
        String pageName;

        pageName = parserData(u, bundle);

        if (pageName == null) return;

        routeDispatcher(pageName, bundle, context);
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



    public static void routeDispatcher(String u, Context context) {
        if (TextUtils.isEmpty(u)) {
            return;
        }
        Bundle bundle = new Bundle();
        String pageName;

        pageName = parserData(u, bundle);

        if (pageName == null) return;

        routeDispatcher(pageName, bundle, context);
    }


    public static void routeDispatcher(String pageName, Bundle bundle, Context context) {

        if (bundle.containsKey(RouteConstant.ROUTE_KEY_POP_TO_ROOT_PAGE)) {
            if (!bundle.getString(RouteConstant.ROUTE_KEY_POP_TO_ROOT_PAGE, RouteConstant.ROUTE_VALUE_ZERO).equals(RouteConstant.ROUTE_VALUE_ZERO)) {
                ARouter.getInstance().build(RouteConstant.MAIN_ROUTE_PATH).navigation(context);
            }
        }

        if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_PUBLISH)) {
            PublishPostStarter.INSTANCE.startActivityFromOutShare(context, bundle);
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_FORWARD)) {
            PublishForwardStarter.INSTANCE.startActivity4PostFromVidmate(context, bundle);
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_MAIN_HOME)) {
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
            try {
                SearchEventManager.INSTANCE.setTopic_name(bundle.getString(TopicConstant.BUNDLE_KEY_TOPIC_NAME));
                SearchEventManager.INSTANCE.setFrom_page(EventConstants.SEARCH_FROM_PAGE_ROUTE);
                SearchEventManager.INSTANCE.report2();
            } catch (Exception e) {
                //do nothing
            }
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_ACTIVITY_REGISTER)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_REGISTER_ACTIVITY, bundle));
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
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_ACTIVITY_VERIFY)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_PAGE, bundle));
        } else if (TextUtils.equals(pageName, RouteConstant.ROUTE_PAGE_RESULT)) {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SEARCH_RESULT_EVENT, bundle));
        } else {
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MAIN_HOME, bundle));
        }
    }


    @Nullable
    private static String parserData(String u, Bundle bundle) {
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

    /**
     * 验证路由链接是否合法
     */
    public static boolean validUri(Uri uri) {
        if (uri == null) return false;
        if (TextUtils.isEmpty(uri.toString())) return false;

        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme)) return false;
        if (!TextUtils.equals(scheme, "welike")) return false;

        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) return false;
        if (!TextUtils.equals(host, "com.redefine.welike")) return false;

        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) return false;
        String[] parts = path.substring(1, path.length() - 1).split("/");
        if (parts.length == 2) {
            return true;
        }
        return false;
    }

}
