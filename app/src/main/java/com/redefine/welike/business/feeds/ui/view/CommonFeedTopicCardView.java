package com.redefine.welike.business.feeds.ui.view;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.TopicCardInfo;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedTopicCardContract;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mengnan on 2018/5/25.
 **/
public class CommonFeedTopicCardView implements ICommonFeedTopicCardContract.ICommonFeedBottomTopicCardView, View.OnClickListener {
    private View mRootView;
    private LinearLayout mCardInfo;
    private ConstraintLayout mCardInfoWithIntroduce;
    private TextView mTopicTile;
    private TextView mTopicTileWithIntroduce;
    private TextView mTopicIntroduce;
    private TextView mTopicNums;
    private TextView mTopicNumsWithIntroduce;

    private PostBase mPostBase;
    private TopicCardInfo topicCardInfo;


    public CommonFeedTopicCardView(View view) {
        mRootView = view;
        mTopicTile = view.findViewById(R.id.topic_name_tv);
        mTopicTileWithIntroduce = view.findViewById(R.id.topic_name_tv_with_introduce);
        mTopicIntroduce = view.findViewById(R.id.topic_introduce_tv);
        mTopicNums = view.findViewById(R.id.topic_nums_tv);
        mTopicNumsWithIntroduce = view.findViewById(R.id.topic_nums_tv_with_introduce);

        mCardInfo = view.findViewById(R.id.card_info);
        mCardInfoWithIntroduce = view.findViewById(R.id.card_info_with_introduce);
    }

    @Override
    public void bindViews(PostBase postBase) {
        mPostBase = postBase;
        topicCardInfo = mPostBase.getTopicCardInfo();

        if (null != topicCardInfo && mPostBase.getActiveAttachment() == null) {
            mRootView.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(topicCardInfo.getTopicIntroduce())) {
                mCardInfo.setVisibility(View.VISIBLE);
                mCardInfoWithIntroduce.setVisibility(View.GONE);
                mTopicTile.setText(topicCardInfo.getTopicName());
                mTopicNums.setText(" " + topicCardInfo.getTopicNums() + " " + "posts");
            } else {
                mCardInfoWithIntroduce.setVisibility(View.VISIBLE);
                mCardInfo.setVisibility(View.GONE);
                mTopicTileWithIntroduce.setText(topicCardInfo.getTopicName());
                mTopicIntroduce.setText(topicCardInfo.getTopicIntroduce());
                mTopicNumsWithIntroduce.setText(" " + topicCardInfo.getTopicNums() + " " + "posts");
            }
            mRootView.setOnClickListener(this);

            // BannerUrlLoader.getInstance().loadBannerUrl(mTopicBg, topicCardInfo.getImageUrl());

        } else {
            mRootView.setVisibility(View.GONE);
        }
    }


    @Override
    public void setVisibility(boolean i) {
        mRootView.setVisibility(i ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString(RouteConstant.ROUTE_KEY_SUPER_TOPIC_ID, topicCardInfo.id);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE, bundle));
        if (!AccountManager.getInstance().isLogin() && null != iBrowseClickListener) {
            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, false, 0);
        }

    }


    private IBrowseClickListener iBrowseClickListener;

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
    }
}
