package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedBottomContract;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedHeadContract;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedTopicCardContract;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.view.CommonFeedAdCardView;
import com.redefine.welike.business.feeds.ui.view.CommonFeedBottomView;
import com.redefine.welike.business.feeds.ui.view.CommonFeedHeadCardView;
import com.redefine.welike.business.feeds.ui.view.CommonFeedHeadView;
import com.redefine.welike.business.feeds.ui.view.CommonFeedTopicCardView;
import com.redefine.welike.business.feeds.ui.view.CommonFeedActiveCardView;
import com.redefine.welike.statistical.manager.PostEventManager;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class BaseFeedViewHolder extends BaseRecyclerViewHolder<PostBase> {
    private final ICommonFeedHeadContract.ICommonFeedHeadPresenter mCommonFeedHeadPresenter;

    CommonFeedBottomView mCommonFeedBottomView;
    private CommonFeedTopicCardView mCommonFeedTopicCardView;
    protected OnPostButtonClickListener mListener;
    protected boolean mFeedClickEnable = true;
    protected boolean mFeedForwardClickEnable = true;
    protected boolean mIsShowContent = false;
    protected boolean mIsImageClickable = false;

    private PostBase mPostBase;
    private long mAttachTime;
    private RecyclerView.Adapter mAdapter;

    private CommonFeedActiveCardView commonFeedActiveCardView;
    private CommonFeedAdCardView commonFeedAdCardView;
    private CommonFeedHeadCardView commonFeedHeadView;
//    ArrowTextView guideLike, guideFollow;

    protected IBrowseClickListener iBrowseClickListener;


    public BaseFeedViewHolder(View itemView, IFeedOperationListener listener) {
        super(itemView);
        mCommonFeedHeadPresenter = ICommonFeedHeadContract.CommonFeedHeadFactory.createPresenter(itemView.findViewById(R.id.common_feed_head_root_view), listener);
        mCommonFeedBottomView = ICommonFeedBottomContract.CommonFeedBottomFactory.createView(itemView.findViewById(R.id.common_feed_bottom_root_view));
        mCommonFeedTopicCardView = ICommonFeedTopicCardContract.CommonFeedBottomFactory.createView(itemView.findViewById(R.id.topic_card_layout));
        commonFeedActiveCardView = new CommonFeedActiveCardView(itemView.findViewById(R.id.active_card_layout));
        commonFeedAdCardView = new CommonFeedAdCardView(itemView.findViewById(R.id.ad_card_layout));
        commonFeedHeadView = new CommonFeedHeadCardView(itemView.findViewById(R.id.header_card_layout));
//        guideFollow = itemView.findViewById(R.id.guide_follow);
    }

    @CallSuper
    public void bindViews(RecyclerView.Adapter adapter, PostBase postBase) {
        mAdapter = adapter;
        mPostBase = postBase;
        mCommonFeedHeadPresenter.bindViews(postBase, adapter);
        mCommonFeedBottomView.bindViews(postBase, adapter);
        mCommonFeedTopicCardView.bindViews(postBase);
        commonFeedActiveCardView.bindViews(postBase);
        commonFeedAdCardView.bindViews(postBase);
        commonFeedHeadView.bindViews(postBase);
//        guideLike.setVisibility(View.GONE);
//        guideFollow.setVisibility(View.GONE);
    }

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
        mCommonFeedBottomView.setBrowseClickListener(iBrowseClickListener);
        mCommonFeedHeadPresenter.setIBrowseListener(iBrowseClickListener);
        mCommonFeedTopicCardView.setBrowseClickListener(iBrowseClickListener);
    }

    public void setDismissBottom(boolean b) {
        mCommonFeedBottomView.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    public void setShowContent(boolean isShowContent) {
        this.mIsShowContent = isShowContent;
    }

    public void setFeedClickEnable(boolean b) {
        mFeedClickEnable = b;
    }

    public void setForwardFeedClickEnable(boolean b) {
        mFeedForwardClickEnable = b;
    }

    public void setFollowStatus(PostBase postBase) {
        mCommonFeedHeadPresenter.bindViews(postBase, mAdapter);
    }

    public void dismissDivider(boolean b) {
        mCommonFeedBottomView.setDismissDivider(b);
    }

    public void dismissFollowBtn(boolean b) {
        mCommonFeedHeadPresenter.dismissFollowBtn(b);
    }

    public void setIsImageClickable(boolean mIsImageClickable) {
        this.mIsImageClickable = mIsImageClickable;
    }

    public void setDismissBottomContent(boolean b) {
        mCommonFeedBottomView.setDismissBottomContent(b);
    }

    public void showHotFlog(boolean b) {
        getFeedHeadView().showHotFlag(b);
    }

    public void showTopPostFlag(boolean b) {
        getFeedHeadView().showTopPostFlag(b);
    }

    public void showTopFlog(boolean b) {
        getFeedHeadView().showTopFlag(b);
    }

    public void showReadCount(boolean b) {
        mCommonFeedHeadPresenter.showReadCount(b);
    }

    public void performClickDeleteBtn() {
        mCommonFeedHeadPresenter.performClickDeleteBtn();
    }

    public void dismissArrowBtn(boolean b) {
        mCommonFeedHeadPresenter.dismissArrowBtn(b);
    }

    public CommonFeedBottomView getmCommonFeedBottomView() {
        return mCommonFeedBottomView;
    }

    public CommonFeedHeadView getFeedHeadView() {
        return mCommonFeedHeadPresenter.getView();
    }

    public void dismissTopicCard(boolean isShow) {
        mCommonFeedTopicCardView.setVisibility(!isShow);
        commonFeedActiveCardView.setVisible(isShow ? View.GONE : View.VISIBLE);
    }

    public void setOnButtonClickListener(OnPostButtonClickListener listener) {
        mListener = listener;
        mCommonFeedHeadPresenter.getView().setPostButtonClickListener(listener);

    }

    @Override
    public void onAttachToWindow() {
        mAttachTime = System.currentTimeMillis();
    }

    @Override
    public void onDetachToWindow() {
        long detachTime = System.currentTimeMillis();
        long duration = detachTime - mAttachTime;
        if (duration / 1000 > 2 && mPostBase != null) {
            PostEventManager.INSTANCE.addPost(mPostBase.getPid());
        }
    }

}
