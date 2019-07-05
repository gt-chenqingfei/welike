package com.redefine.welike.business.feeds.ui.util;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.page.UserHostPage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by liwenbo on 2018/4/11.
 */

public class DefaultRichItemClickHandler implements OnRichItemClickListener {

    protected final Context mContext;

    public DefaultRichItemClickHandler(Context context) {
        mContext = context;
    }

    @Override
    public void onRichItemClick(RichItem richItem) {
        if (richItem.isLinkItem()) {
            String target = TextUtils.isEmpty(richItem.target) ? richItem.source: richItem.target;
            WebViewActivity.luanch(mContext, target);
        } else if (richItem.isAtItem()) {
            UserHostPage.launch(true, richItem.id);
        } else if (richItem.isTopicItem()) {
            if (TextUtils.isEmpty(richItem.id)) {
                return;
            }
            Bundle bundle = new Bundle();
            TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
            String target = TextUtils.isEmpty(richItem.display) ? richItem.source: richItem.display;
            bean.name = target;
            bean.id = richItem.id;
            bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
        } else if (richItem.isSuperTopicItem()) {
            Bundle bundle = new Bundle();
            bundle.putString(RouteConstant.ROUTE_KEY_SUPER_TOPIC_ID, richItem.id);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE, bundle));
        }
    }
}
