package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.business.im.ui.ChatMessage;

/**
 * Created by wangnianguo on 2018/6/15.
 */

public class ChatCardProfileViewHolder extends BaseChatCardViewHolder {

    private final TextView mTitleView;

    public ChatCardProfileViewHolder(SESSION mSession, View inflate, OnChatCardClickListener listener) {
        super(mSession, inflate, listener);
        mTitleView = itemView.findViewById(R.id.chat_card_username);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final ChatMessage message) {
        super.bindViews(adapter, message);
        mTitleView.setText(message.getCardInfo().getTitle());
    }
}
