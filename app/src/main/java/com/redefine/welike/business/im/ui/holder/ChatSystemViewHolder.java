package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.im.ui.ChatMessage;

/**
 * Created by liwenbo on 2018/2/13.
 */

public class ChatSystemViewHolder extends BaseRecyclerViewHolder<ChatMessage> {
    private final TextView mSystemText;

    public ChatSystemViewHolder(View itemView) {
        super(itemView);
        mSystemText = itemView.findViewById(R.id.im_chat_system);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, ChatMessage messageBase) {
        super.bindViews(adapter, messageBase);
        mSystemText.setText(messageBase.getMessage().getText());
    }
}
