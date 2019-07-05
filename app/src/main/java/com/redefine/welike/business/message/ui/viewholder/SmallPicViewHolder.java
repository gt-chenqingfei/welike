package com.redefine.welike.business.message.ui.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.message.management.bean.BusinessNotification;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.ui.page.MessageBoxActivity;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.activity.SchemeFilterActivity;
import com.redefine.welike.commonui.framework.PageStackManager;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.statistical.manager.IMEventManager;

public class SmallPicViewHolder extends BaseRecyclerViewHolder<NotificationBase> {
    private SimpleDraweeView header;
    private TextView title;
    private TextView content;
    private TextView time;
    private SimpleDraweeView pushPic;
    private View pushLayout;

    public SmallPicViewHolder(View itemView) {
        super(itemView);
        pushLayout = itemView.findViewById(R.id.push_layout);
        header = itemView.findViewById(R.id.notification_push_item_head);
        title = itemView.findViewById(R.id.notification_push_item_title);
        content = itemView.findViewById(R.id.notification_push_item_content);
        time = itemView.findViewById(R.id.notification_push_item_time);
        pushPic = itemView.findViewById(R.id.notification_push_item_icon);

    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final NotificationBase data) {
        super.bindViews(adapter, data);
        if (data instanceof BusinessNotification) {
            final BusinessNotification notification = (BusinessNotification) data;
            title.setText(notification.getPushTile());
            content.setText(notification.getPushText());
            time.setText(DateTimeUtil.INSTANCE.getMessageReceiveTime(time.getResources(), notification.getTime()));
            pushPic.setImageURI(notification.getPushIcon());
            pushLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent jumpIntent = new Intent(pushLayout.getContext(), SchemeFilterActivity.class);
                    jumpIntent.putExtra(ARouter.RAW_URI,notification.getForwordUrl());
                    PageStackManager mPageStackManager = new PageStackManager((MessageBoxActivity)(pushLayout.getContext()));
                    RouteDispatcher routeDispatcher = new RouteDispatcher(pushLayout.getContext());
                    routeDispatcher.handleRouteMessage(jumpIntent);
                    IMEventManager.INSTANCE.report8(((BusinessNotification) data).getBatchId());
                }
            });
        }
    }
}
