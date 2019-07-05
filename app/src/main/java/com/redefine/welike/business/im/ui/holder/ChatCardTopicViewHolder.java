package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.business.im.model.CardInfo;
import com.redefine.welike.business.im.ui.ChatMessage;

/**
 * Created by wangnianguo on 2018/6/15.
 */

public class ChatCardTopicViewHolder extends BaseChatCardViewHolder {

    private final TextView mTitleView;
    private final TextView mNumber;

    public ChatCardTopicViewHolder(SESSION mSession, View inflate, OnChatCardClickListener listener) {
        super(mSession, inflate, listener);
        mTitleView = itemView.findViewById(R.id.chat_card_username);
        mNumber = itemView.findViewById(R.id.chat_card_number);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final ChatMessage message) {
        super.bindViews(adapter, message);
        CardInfo cardInfo = message.getCardInfo();
        mTitleView.setText(cardInfo.getTitle());
        mNumber.setText(formatNumber(cardInfo.getPostCount()));
        if(!TextUtils.isEmpty(cardInfo.getImageUrl())) {
            mCardPic.setImageURI(cardInfo.getImageUrl());
        } else {
            mCardPic.setImageResource(R.drawable.topic_landing_banner_default);
        }
    }

    private String formatNumber(long number) {
        if (number <= 0) {
            return "0";
        } else if (number < 9999L) {
            return String.valueOf(number);
        } else if (number < 9999999L) {
            long count = number / 1000L;
            return count + "K";
        } else if (number < 9999999999L) {
            long count = number / 1000000L;
            return count + "M";
        } else {
            return "9999M";
        }
    }
}
