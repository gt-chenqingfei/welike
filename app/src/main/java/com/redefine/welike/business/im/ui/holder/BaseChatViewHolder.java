package com.redefine.welike.business.im.ui.holder;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.im.Constants;
import com.redefine.im.room.MESSAGE;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.ChatMessage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by liwenbo on 2018/2/6.
 */

public abstract class BaseChatViewHolder extends BaseRecyclerViewHolder<ChatMessage> {

    private final VipAvatar mUserHeader;
    private final View mMessageStateContainer;
    private final View mMessageStateLoading;
    private final View mMessageStateError;
    private final SESSION mSession;

    public BaseChatViewHolder(SESSION session, View itemView) {
        super(itemView);
        mSession = session;
        mUserHeader = itemView.findViewById(R.id.chat_user_header);
        mMessageStateContainer = itemView.findViewById(R.id.message_state_container);
        mMessageStateLoading = itemView.findViewById(R.id.message_state_loading);
        mMessageStateError = itemView.findViewById(R.id.message_state_error);
    }

    @CallSuper
    @Override
    public void bindViews(RecyclerView.Adapter adapter, final ChatMessage chatMessage) {
        super.bindViews(adapter, chatMessage);
        final MESSAGE message = chatMessage.getMessage();
        if (mUserHeader != null) {
            VipUtil.set(mUserHeader, message.getSenderHead(), 0);
            mUserHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodUtil.hideInputMethod(v.getContext());
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isExpanded", true);
                    bundle.putString("userId", message.getSenderUid());
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));
                }
            });
        }

        if (message.getStatus() == Constants.MESSAGE_STATUS_SENDING) {
            if (mMessageStateContainer != null) {
                mMessageStateContainer.setVisibility(View.VISIBLE);
            }
            if (mMessageStateLoading != null) {
                mMessageStateLoading.setVisibility(View.VISIBLE);
            }
            if (mMessageStateError != null) {
                mMessageStateError.setVisibility(View.INVISIBLE);
            }
        } else if (message.getStatus() == Constants.MESSAGE_STATUS_FAILED) {
            if (mMessageStateContainer != null) {
                mMessageStateContainer.setVisibility(View.VISIBLE);
            }
            if (mMessageStateLoading != null) {
                mMessageStateLoading.setVisibility(View.INVISIBLE);
            }
            if (mMessageStateError != null) {
                mMessageStateError.setVisibility(View.VISIBLE);
                mMessageStateError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IMHelper.INSTANCE.retry(mSession, message);
                    }
                });
            }
        } else {
            if (mMessageStateContainer != null) {
                mMessageStateContainer.setVisibility(View.INVISIBLE);
            }
        }
    }
}
