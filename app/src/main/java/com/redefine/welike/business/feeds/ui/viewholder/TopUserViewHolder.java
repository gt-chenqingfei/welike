package com.redefine.welike.business.feeds.ui.viewholder;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;

public class TopUserViewHolder {


    private TextView mTopUserTitle;
    private LinearLayout mTopUserView;
    private TopUserItemViewHolder mNo1TopViewHolder;
    private TopUserItemViewHolder mNo2TopViewHolder;
    private TopUserItemViewHolder mNo3TopViewHolder;
     View mRootView;

    public void createViewHolder(ViewGroup parent) {
        mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.discovery_top_users_layout, null);
        mTopUserTitle = mRootView.findViewById(R.id.discover_top_user_title);
        mTopUserView = mRootView.findViewById(R.id.discover_top_user_view);
        mNo1TopViewHolder = new TopUserItemViewHolder(mRootView.findViewById(R.id.discover_top_no1_user_item));
        mNo2TopViewHolder = new TopUserItemViewHolder(mRootView.findViewById(R.id.discover_top_no2_user_item));
        mNo3TopViewHolder = new TopUserItemViewHolder(mRootView.findViewById(R.id.discover_top_no3_user_item));
    }


    public void bindView(ViewGroup parent, final TopUserShakeBean data) {

        if (TextUtils.isEmpty(data.title)) {
            mTopUserTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "trending_people"));
        } else {
            mTopUserTitle.setText(data.title);
        }
        mNo1TopViewHolder.bindViews(1, data.mTopUsers.get(0));
        mNo2TopViewHolder.bindViews(2, data.mTopUsers.get(1));
        mNo3TopViewHolder.bindViews(3, data.mTopUsers.get(2));

        if (!TextUtils.isEmpty(data.topicUrl)) {
            mTopUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (guideListener!=null){
//                        guideListener.onClick(mRootView);
//                    }
//                    MissionManager.INSTANCE.notifyEvent(MissionType.RANK);
                    new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TOP_USER).onUrlRedirect(data.topicUrl);
                }
            });
            mTopUserTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (guideListener!=null){
//                        guideListener.onClick(mRootView);
//                    }
//                    MissionManager.INSTANCE.notifyEvent(MissionType.RANK);
                    new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TOP_USER).onUrlRedirect(data.topicUrl);
                }
            });
        }
//        if (guideListener!=null){
//            guideListener.onShow(mRootView,null);
//        }
        parent.addView(mRootView);
    }

//    GuideListener<View> guideListener;
}
