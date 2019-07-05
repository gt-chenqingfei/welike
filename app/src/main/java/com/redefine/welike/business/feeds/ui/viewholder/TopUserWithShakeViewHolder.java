package com.redefine.welike.business.feeds.ui.viewholder;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.feeds.ui.listener.GuideListener;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;

public class TopUserWithShakeViewHolder {


    private View mRootView;
    private TextView mTopUserTitle;

    private TopShakeUserItemViewHolder mNo1TopViewHolder;
    private TopShakeUserItemViewHolder mNo2TopViewHolder;
    private TopShakeUserItemViewHolder mNo3TopViewHolder;
    View mTopUserRootView;
    private TextView mShakeTitle;
    private View mShakeRootView;

    public void createViewHolder(ViewGroup parent) {
        mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_user_shake_item, null);
        mNo1TopViewHolder = new TopShakeUserItemViewHolder(mRootView.findViewById(R.id.discover_shake_top_no1_user_item));
        mNo2TopViewHolder = new TopShakeUserItemViewHolder(mRootView.findViewById(R.id.discover_shake_top_no2_user_item));
        mNo3TopViewHolder = new TopShakeUserItemViewHolder(mRootView.findViewById(R.id.discover_shake_top_no3_user_item));
        mTopUserTitle = mRootView.findViewById(R.id.discover_top_user_title);
        mShakeTitle = mRootView.findViewById(R.id.discover_shake_title);
        mTopUserRootView = mRootView.findViewById(R.id.discover_top_user_layout);
        mShakeRootView = mRootView.findViewById(R.id.discover_shake_item_layout);

    }

    public void bindView(ViewGroup parent, final TopUserShakeBean data) {
        mNo1TopViewHolder.bindViews(1, data.mTopUsers.get(0));
        mNo2TopViewHolder.bindViews(2, data.mTopUsers.get(1));
        mNo3TopViewHolder.bindViews(3, data.mTopUsers.get(2));
        if (TextUtils.isEmpty(data.title)) {
            mTopUserTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "discover_top_user"));
        } else {
            mTopUserTitle.setText(data.title);
        }

        mTopUserRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissionManager.INSTANCE.notifyEvent(MissionType.RANK);
                new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TOP_USER).onUrlRedirect(data.topicUrl);
                if (guideListener != null) {
                    guideListener.onClick(mTopUserRootView);
                }
            }
        });

        mShakeRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TOP_USER).onUrlRedirect(data.shakeUrl);
            }
        });

        mShakeTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "shake"));
//        if (guideListener != null) {
//            guideListener.onShow(mTopUserRootView, null);
//        }
        parent.addView(mRootView);
    }

    GuideListener<View> guideListener;
}
