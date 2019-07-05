package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.swipe.SwipeLayout;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.model.SessionModel;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class SessionShakeViewHolder extends BaseSessionItemViewHolder<SessionModel> {

    private final TextView mSessionName;
    private final LottieAnimationView mSessionHeader;
    private final SwipeLayout mSwipeLayout;
    private final SessionItemViewHolder.IDeleteSessionCallback mCallback;
    private final String mShakeText;

    public SessionShakeViewHolder(View itemView, SessionItemViewHolder.IDeleteSessionCallback listener) {
        super(itemView);
        mCallback = listener;
        mShakeText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "shake");
        mSwipeLayout = itemView.findViewById(R.id.im_session_root_view);

        mSessionName = itemView.findViewById(R.id.session_group_name);
        mSessionHeader = itemView.findViewById(R.id.session_group_photo);
        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        mSwipeLayout.setSwipeEnabled(false);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SessionModel imSession) {
        mSessionName.setText(mShakeText);
        mSessionHeader.setAnimation("shake.json");
        mSessionHeader.playAnimation();
    }

    public void playAnimation() {
        if (mSessionHeader != null) {
            mSessionHeader.playAnimation();
        }
    }
}
