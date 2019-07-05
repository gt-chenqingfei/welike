package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.ChatPicUrlLoader;
import com.redefine.commonui.widget.FrescoProgressDrawable;
import com.redefine.im.room.MESSAGE;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.im.ui.ChatMessage;
import com.redefine.welike.commonui.photoselector.PhotoSelector;

/**
 * Created by liwenbo on 2018/2/6.
 */

public class ChatPicViewHolder extends BaseChatViewHolder {

    private final SimpleDraweeView mPicView;

    public ChatPicViewHolder(SESSION mSession, View inflate) {
        super(mSession, inflate);
        mPicView = itemView.findViewById(R.id.chat_pic_view);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final ChatMessage chatMessage) {
        super.bindViews(adapter, chatMessage);
        final MESSAGE picMessage = chatMessage.getMessage();

        String uid = AccountManager.getInstance().getAccount().getUid();
        if(!TextUtils.equals(uid, picMessage.getSenderUid())) {
            GenericDraweeHierarchy hierarchy = mPicView.getHierarchy();
            hierarchy.setProgressBarImage(new FrescoProgressDrawable(itemView.getContext()));
        }
        if (!TextUtils.isEmpty(picMessage.getPic())) {
            mPicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoSelector.previewSinglePic(itemView.getContext(), picMessage.getSenderNick(), picMessage.getPic());
                }
            });
            ChatPicUrlLoader.getInstance().loadChatPicUrl(mPicView, picMessage.getPic());
        } else if (!TextUtils.isEmpty(picMessage.getFileName())) {
            mPicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoSelector.previewSinglePic(itemView.getContext(), picMessage.getSenderNick(), picMessage.getPic());
                }
            });
            ChatPicUrlLoader.getInstance().loadChatPicFile(mPicView, picMessage.getFileName());
        }

    }
}
