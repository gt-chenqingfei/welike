package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.foundation.framework.Event;
import com.redefine.richtext.RichContent;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.RichTextView;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.block.BlockFactory;
import com.redefine.richtext.helper.RichTextHelper;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.util.PostRichItemClickHandler;
import com.redefine.welike.statistical.EventConstants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by liwb on 2018/1/11.
 */

public class ForwardFeedTextViewHolder extends TextFeedViewHolder {

    private final TextView mForwardFeedNickName;
    private final RichTextView mForwardFeedContent;
    private final View mForwardFeedRootView;
    // private final UserForwardFollowBtn mForwardFeedFollowBtn;
    // private final IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public ForwardFeedTextViewHolder(View inflate, IFeedOperationListener mUserFollowListener) {
        super(inflate, mUserFollowListener);
        mForwardFeedRootView = itemView.findViewById(R.id.forward_feed_root_view);
        mForwardFeedNickName = itemView.findViewById(R.id.forward_feed_nick_name);
        mForwardFeedContent = itemView.findViewById(R.id.forward_feed_text_content);/*
        mForwardFeedFollowBtn = itemView.findViewById(R.id.forward_follow_btn);
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mForwardFeedFollowBtn, false);*/
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final PostBase postBase) {
        super.bindViews(adapter, postBase);
        if (postBase instanceof ForwardPost) {
            final ForwardPost forwardPost = (ForwardPost) postBase;
            mForwardFeedContent.getRichProcessor().setOnRichItemClickListener(new PostRichItemClickHandler(mForwardFeedContent.getContext(), forwardPost.getRootPost()) {
                @Override
                public void onRichItemClick(RichItem richItem) {

                    if (mListener != null) {
                        mListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_TEXT);
                    }

                    if (iBrowseClickListener != null) {
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, true, 0);
                        return;
                    }
                    super.onRichItemClick(richItem);
                }
            });

            String content = mIsShowContent ? forwardPost.getRootPost().getText() : forwardPost.getRootPost().getSummary();

            Block block = BlockFactory.createMention(forwardPost.getRootPost().getNickName(), forwardPost.getRootPost().getUid());

            RichContent userNickContent = new RichContent();
            userNickContent.text = block.blockText;
            userNickContent.richItemList = new ArrayList<>();
            userNickContent.richItemList.add(block.richItem);
            RichContent richContent = RichTextHelper.mergeRichText(userNickContent, ":");

            RichContent postRichText = new RichContent();
            postRichText.text = content;
            postRichText.richItemList = forwardPost.getRootPost().getRichItemList();
            richContent = RichTextHelper.mergeRichText(richContent, postRichText);

            mForwardFeedContent.getRichProcessor().setRichContent(richContent.text, richContent.richItemList);

            if (!mIsShowContent) {
                int contentLength = TextUtils.isEmpty(forwardPost.getRootPost().getText()) ? 0 : forwardPost.getRootPost().getText().length();
                int summaryLength = TextUtils.isEmpty(forwardPost.getRootPost().getSummary()) ? 0 : forwardPost.getRootPost().getSummary().length();
                if (contentLength != summaryLength && contentLength != 0 && summaryLength != 0) {
                    mForwardFeedContent.getRichProcessor().showMoreBtn(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_summary_more"));
                }
            }

            // mForwardFeedNickName.setText("@"+forwardPost.getRootPost().getNickName());

            // mFollowBtnPresenter.bindView(forwardPost.getRootPost().getUid(), forwardPost.getRootPost().isFollowing(), false);
            if (mFeedForwardClickEnable) {
                mForwardFeedRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iBrowseClickListener != null) {
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_POST, false, 0);
                        }
                        if (mListener != null) {
                            mListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_TEXT);
                        }
                        onClickForwardFeed(forwardPost.getRootPost());
                    }
                });
            }
        }
    }


    private void onClickForwardFeed(PostBase childCard) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        bundle.putSerializable(FeedConstant.KEY_FEED, childCard);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    @Override
    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        super.setBrowseClickListener(iBrowseClickListener);
     /*   mForwardFeedFollowBtn
                .setOnClickFollowBtnListener(null);
        mForwardFeedFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForwardFeedTextViewHolder.this.iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_FOLLOW);
            }
        });*/
    }
}
