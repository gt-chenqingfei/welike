package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.LinkThumbUrlLoader;
import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.LinkPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by liwb on 2018/1/11.
 */

public class ForwardFeedLinkViewHolder extends ForwardFeedTextViewHolder {

    private final SimpleDraweeView mForwardLinkFeedThumb;
    private final TextView mForwardLinkFeedTitle;
    private final View mLinkFeedContainer;

    public ForwardFeedLinkViewHolder(View itemView, IFeedOperationListener listener) {
        super(itemView, listener);
        mForwardLinkFeedThumb = itemView.findViewById(R.id.linked_feed_thumb_image);
        mForwardLinkFeedTitle = itemView.findViewById(R.id.linked_feed_title);
        mLinkFeedContainer = itemView.findViewById(R.id.link_feed_container);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, PostBase postBase) {
        super.bindViews(adapter, postBase);
        if (postBase instanceof ForwardPost) {
            ForwardPost forwardPost = (ForwardPost) postBase;
            if (forwardPost.getRootPost() instanceof LinkPost) {
                final LinkPost linkPost = (LinkPost) forwardPost.getRootPost();
                mForwardLinkFeedThumb.getHierarchy().setRoundingParams(RoundingParams.fromCornersRadii(0, 0, 0, ScreenUtils.dip2Px(4)));
                LinkThumbUrlLoader.getInstance().loadLinkThumbUrl(mForwardLinkFeedThumb, linkPost.getLinkThumbUrl());
                mForwardLinkFeedTitle.setText(linkPost.getLinkTitle());
                mLinkFeedContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mListener != null) {
                            mListener.onPostAreaClick(linkPost, EventConstants.CLICK_AREA_TEXT);
                        }

                        if (iBrowseClickListener != null) {
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, true, 0);
                            return;
                        }
                        WebViewActivity.luanch(mLinkFeedContainer.getContext(), linkPost.getLinkUrl());
                    }
                });
            }
        }

    }
}
