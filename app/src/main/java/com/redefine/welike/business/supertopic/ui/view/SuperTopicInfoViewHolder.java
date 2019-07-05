package com.redefine.welike.business.supertopic.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.supertopic.management.bean.SuperTopicInfoBean;

public class SuperTopicInfoViewHolder extends BaseRecyclerViewHolder<SuperTopicInfoBean> {
    private final TextView infoView;

    public SuperTopicInfoViewHolder(View rootView) {
        super(rootView);
        infoView = rootView.findViewById(R.id.super_topic_page_info);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, SuperTopicInfoBean infoBean) {
        super.bindViews(adapter, infoBean);
        infoView.setText(infoBean.getInfo());
    }
}
