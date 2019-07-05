package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.business.im.model.CardInfo;
import com.redefine.welike.business.im.ui.ChatMessage;

/**
 * Created by wangnianguo on 2018/6/15.
 */

public class ChatCardPostViewHolder extends BaseChatCardViewHolder {

    private final TextView mTitleView;
    private final ImageView mMask;

    public ChatCardPostViewHolder(SESSION mSession, View inflate, OnChatCardClickListener listener) {
        super(mSession, inflate, listener);
        mTitleView = itemView.findViewById(R.id.chat_card_username);
        mMask = itemView.findViewById(R.id.im_mask);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final ChatMessage message) {
        super.bindViews(adapter, message);
        CardInfo cardInfo = message.getCardInfo();
        mTitleView.setText(cardInfo.getTitle());
        int subType = cardInfo.getSubType();
        if(subType == CardInfo.SUB_TYPE_TEXT) {
            mMask.setVisibility(View.VISIBLE);
            mMask.setImageResource(R.drawable.ic_im_mask_text);
        } else if (subType == CardInfo.SUB_TYPE_VOTE) {
            mMask.setVisibility(View.VISIBLE);
            mMask.setImageResource(R.drawable.ic_im_mask_topic);
        } else if (subType == CardInfo.SUB_TYPE_VIDEO) {
            mMask.setVisibility(View.VISIBLE);
            mMask.setImageResource(R.drawable.ic_im_mask_video);
        } else {
            mMask.setVisibility(View.GONE);
        }
    }
}
