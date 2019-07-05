package com.redefine.welike.business.message.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.business.message.management.bean.NotificationBase;

/**
 * Created by liwenbo on 2018/3/10.
 */
@Deprecated
public class BaseMessageViewHolder extends BaseRecyclerViewHolder<NotificationBase> {

    public BaseMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, NotificationBase feedBase) {
        super.bindViews(adapter, feedBase);
    }
}
