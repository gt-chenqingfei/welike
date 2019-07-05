package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.model.MentionSessionModel;
import com.redefine.welike.business.im.model.SessionModel;
import com.redefine.welike.statistical.manager.IMEventManager;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class SessionMentionViewHolder extends BaseSessionItemViewHolder<SessionModel> {

    private final TextView mSessionName;
    private final SimpleDraweeView mSessionHeader;
    private final SwipeLayout mSwipeLayout;
    private final SessionItemViewHolder.IDeleteSessionCallback mCallback;
    private final String mMentionText;
    private final TextView mUnreadCount;

    public SessionMentionViewHolder(View itemView, SessionItemViewHolder.IDeleteSessionCallback listener) {
        super(itemView);
        mCallback = listener;
        mMentionText = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_mention_text");
        mSwipeLayout = itemView.findViewById(R.id.im_session_root_view);
        mUnreadCount = itemView.findViewById(R.id.session_message_unread_count);

        mSessionName = itemView.findViewById(R.id.session_group_name);
        mSessionHeader = itemView.findViewById(R.id.session_group_photo);
        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        mSwipeLayout.setSwipeEnabled(false);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SessionModel imSession) {
        if(!(imSession instanceof MentionSessionModel)) {
            return;
        }
        mSessionName.setText(mMentionText);
        mSessionHeader.setImageResource(R.drawable.session_mention_icon);
        int unreadCount = ((MentionSessionModel) imSession).getUnreadCount();
        if (unreadCount > 0) {
            IMEventManager.INSTANCE.setMention_num(Math.min(unreadCount, 99));
            mUnreadCount.setVisibility(View.VISIBLE);
            mUnreadCount.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
        } else {
            IMEventManager.INSTANCE.setMention_num(0);
            mUnreadCount.setVisibility(View.GONE);
        }
    }
}
