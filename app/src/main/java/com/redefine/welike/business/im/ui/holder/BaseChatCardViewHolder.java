package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.business.im.model.CardInfo;
import com.redefine.welike.business.im.ui.ChatMessage;

/**
 * Created by wangnianguo on 2018/6/15.
 */

public class BaseChatCardViewHolder extends BaseChatViewHolder {

    private View mCardContainer;
    protected SimpleDraweeView mCardPic;
    private TextView mCardContent;
    private OnChatCardClickListener mListener;

    public BaseChatCardViewHolder(SESSION mSession, View inflate, OnChatCardClickListener listener) {
        super(mSession, inflate);
        mListener = listener;
        mCardPic = itemView.findViewById(R.id.chat_card_image);
        mCardContent = itemView.findViewById(R.id.chat_card_content);
        mCardContainer = itemView.findViewById(R.id.chat_card_container);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, ChatMessage chatMessage) {
        super.bindViews(adapter, chatMessage);
        final CardInfo cardInfo = chatMessage.getCardInfo();
        if(cardInfo != null) {
            if(!TextUtils.isEmpty(cardInfo.getImageUrl())) {
                mCardPic.setImageURI(cardInfo.getImageUrl());
            } else {
                mCardPic.setImageResource(R.drawable.shape_im_card_bg);
            }
            mCardContent.setText(cardInfo.getContent());
            mCardContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.onChatCardClick(v, cardInfo);
                    }
                }
            });
        }
    }

    public interface OnChatCardClickListener {
        void onChatCardClick(View v, CardInfo cardInfo);
    }
}
