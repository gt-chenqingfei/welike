package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.im.room.SESSION;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.RichTextView;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.welike.R;
import com.redefine.welike.business.im.ui.ChatMessage;

/**
 * Created by liwenbo on 2018/2/6.
 */

public class ChatTextViewHolder extends BaseChatViewHolder {

    public final RichTextView mTextView;

    public ChatTextViewHolder(SESSION mSession, View item) {
        super(mSession, item);
        mTextView = itemView.findViewById(R.id.chat_text_view);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, ChatMessage textMessage) {
        super.bindViews(adapter, textMessage);
        mTextView.getRichProcessor().setOnRichItemClickListener(new OnRichItemClickListener() {
            @Override
            public void onRichItemClick(RichItem richItem) {
                if (richItem.isLinkItem()) {
                    String target = TextUtils.isEmpty(richItem.target) ? richItem.source: richItem.target;
                    WebViewActivity.luanch(mTextView.getContext(), target);
                    InputMethodUtil.hideInputMethod(mTextView.getContext());
                }
            }
        });
        mTextView.getRichProcessor().setRichContent(textMessage.getMessage().getText(), null);
        mTextView.getRichProcessor().parseCommonLinks();
    }
}
