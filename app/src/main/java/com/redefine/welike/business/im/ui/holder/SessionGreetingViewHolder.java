package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.im.room.MessageSession;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.model.GreetingSessionModel;
import com.redefine.welike.business.im.model.SessionModel;
import com.redefine.welike.statistical.manager.IMEventManager;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class SessionGreetingViewHolder extends BaseSessionItemViewHolder<SessionModel> {

    private final TextView mSessionName;
    private final SimpleDraweeView mSessionHeader;
    private final SwipeLayout mSwipeLayout;
    private final View mDeleteBtn;
    private final SessionItemViewHolder.IDeleteSessionCallback mCallback;
    private final String mStrangerText;
    private final View mRedPoint;

    public SessionGreetingViewHolder(View itemView, SessionItemViewHolder.IDeleteSessionCallback listener) {
        super(itemView);
        mCallback = listener;
        mStrangerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "session_greetings");
        mSwipeLayout = itemView.findViewById(R.id.im_session_root_view);
        mRedPoint = itemView.findViewById(R.id.session_red_point);
        mSessionHeader = itemView.findViewById(R.id.session_group_photo);

        mSessionName = itemView.findViewById(R.id.session_group_name);
        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        mDeleteBtn = itemView.findViewById(R.id.im_session_delete_btn);
        mSwipeLayout.setSwipeEnabled(false);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SessionModel sessionModel) {
        mSessionName.setText(mStrangerText);
        mSessionHeader.setImageResource(R.drawable.session_greetings_icon);

        if(!(sessionModel instanceof GreetingSessionModel)) {
            return;
        }
        final MessageSession imSession = ((GreetingSessionModel) sessionModel).getMessageSession();
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDeleteSession(imSession);
            }
        });

        if (imSession.getSUnread() > 0) {
            IMEventManager.INSTANCE.setStranger_num(1);
            mRedPoint.setVisibility(View.VISIBLE);
        } else {
            IMEventManager.INSTANCE.setStranger_num(0);
            mRedPoint.setVisibility(View.GONE);
        }
    }
}
