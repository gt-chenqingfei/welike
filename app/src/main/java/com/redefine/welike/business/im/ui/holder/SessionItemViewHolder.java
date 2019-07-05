package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.redefine.im.Constants;
import com.redefine.im.room.MessageSession;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.model.SessionModel;
import com.redefine.welike.business.im.model.SingleSessionModel;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by liwenbo on 2018/2/6.
 */

public class SessionItemViewHolder extends BaseSessionItemViewHolder<SessionModel> {

    private static final int MAX_UN_READ_COUNT = 99;
    private final VipAvatar mDraweeView;
    private final TextView mMessageTime;
    private final TextView mMessageUnReadCount;
    private final TextView mSessionName;
    private final RichTextView mSessionContent;
    private final String mImageText;
    private final String mCardText;
    private final SwipeLayout mSwipeLayout;
    private final View mDeleteBtn;
    private final IDeleteSessionCallback mCallback;
    private final OnSessionLongClickListener mLongClickListener;


    public SessionItemViewHolder(View itemView, IDeleteSessionCallback callback, OnSessionLongClickListener listener) {
        super(itemView);
        mCallback = callback;
        mLongClickListener = listener;
        mSwipeLayout = itemView.findViewById(R.id.im_session_root_view);
        mImageText = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "im_session_pic_message");
        mCardText = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "im_session_card_message");
        mDraweeView = itemView.findViewById(R.id.session_group_photo);
        mMessageTime = itemView.findViewById(R.id.session_message_last_time);
        mMessageUnReadCount = itemView.findViewById(R.id.session_message_unread_count);
        mSessionName = itemView.findViewById(R.id.session_group_name);
        mSessionContent = itemView.findViewById(R.id.session_message_content);
        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        mDeleteBtn = itemView.findViewById(R.id.im_session_delete_btn);
        mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, mDeleteBtn);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SessionModel sessionModel) {
        super.bindViews(adapter, sessionModel);
        if(!(sessionModel instanceof SingleSessionModel)) {
            return;
        }
        final MessageSession imSession = ((SingleSessionModel) sessionModel).getMessageSession();
        VipUtil.set(mDraweeView, imSession.getSHead(), 0);
        mMessageTime.setText(DateTimeUtil.INSTANCE.formatImChatTime(mDraweeView.getResources(), imSession.getMessage().getTime()));
        if(imSession.getSUnread() > 0) {
            mMessageUnReadCount.setVisibility(View.VISIBLE);
            String unReadCount = imSession.getSUnread() > MAX_UN_READ_COUNT ? "99+" : String.valueOf(imSession.getSUnread());
            mMessageUnReadCount.setText(unReadCount);
        } else {
            mMessageUnReadCount.setVisibility(View.GONE);
        }

        mSessionName.setText(imSession.getSName());
        if (imSession.getMessage().getType() == Constants.MESSAGE_TYPE_TXT) {
            mSessionContent.getRichProcessor().setRichContent(imSession.getMessage().getText(), null);
        } else if (imSession.getMessage().getType() == Constants.MESSAGE_TYPE_PIC) {
            mSessionContent.setText(mImageText);
        } else if (imSession.getMessage().getType() == Constants.MESSAGE_TYPE_CARD) {
            mSessionContent.setText(mCardText);
        }
        mSessionContentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mLongClickListener != null) {
                    mLongClickListener.onSessionLongClick(v, imSession);
                    return true;
                }
                return false;
            }
        });
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onDeleteSession(imSession);
                }
            }
        });
    }

    public static interface IDeleteSessionCallback {
        void onDeleteSession(MessageSession imSession);
    }

    public static interface OnSessionLongClickListener {
        void onSessionLongClick(View view, MessageSession imSession);
    }
}
