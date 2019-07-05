package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.im.ui.ChatMessage;
import com.redefine.welike.common.util.DateTimeUtil;

/**
 * Created by liwenbo on 2018/2/13.
 */

public class SectionViewHolder extends BaseRecyclerViewHolder<ChatMessage> {
    private final TextView mSectionTime;

    public SectionViewHolder(View itemView) {
        super(itemView);
        mSectionTime = itemView.findViewById(R.id.im_time_section_time);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, ChatMessage messageBase) {
        super.bindViews(adapter, messageBase);
        mSectionTime.setText(DateTimeUtil.INSTANCE.formatImChatTime(itemView.getResources(), messageBase.getMessage().getTime()));
    }
}
