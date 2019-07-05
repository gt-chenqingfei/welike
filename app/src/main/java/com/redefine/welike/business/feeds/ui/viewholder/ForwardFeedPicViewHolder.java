package com.redefine.welike.business.feeds.ui.viewholder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PicPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.adapter.NineGridAdapter;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.photoselector.PhotoSelector;
import com.redefine.welike.commonui.view.MultiGridView;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by liwb on 2018/1/11.
 */

public class ForwardFeedPicViewHolder extends ForwardFeedTextViewHolder implements MultiGridView.OnItemClickListener {

    private final MultiGridView mFrowardPicMultiGridView;
    private TextView mTvAdStatus;
    private final NineGridAdapter mAdapter;
    private PicPost mPicPost;
    private RecyclerView.Adapter mHostAdapter;

    public ForwardFeedPicViewHolder(View inflate, IFeedOperationListener listener) {
        super(inflate, listener);
        mFrowardPicMultiGridView = itemView.findViewById(R.id.forward_pic_feed_multi_grid_view);
        mAdapter = new NineGridAdapter();
        mAdapter.setForwardFeed(true);
        mFrowardPicMultiGridView.setMarginTotalWidth(ScreenUtils.dip2Px(12) * 2);
        mFrowardPicMultiGridView.setAdapter(mAdapter);
        mFrowardPicMultiGridView.setOnItemClickListener(this);
        mTvAdStatus = itemView.findViewById(R.id.tv_post_status);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, PostBase postBase) {
        super.bindViews(adapter, postBase);
        mHostAdapter = adapter;
        if (postBase instanceof ForwardPost) {
            ForwardPost forwardPost = (ForwardPost) postBase;
            if (forwardPost.getRootPost() instanceof PicPost) {
                mPicPost = (PicPost) forwardPost.getRootPost();
                mAdapter.setData(mPicPost.listPicInfo());
                if (mPicPost.getAdAttachment() != null) {
                    if (System.currentTimeMillis() > mPicPost.getAdAttachment().getEndTime()) {
                        if (!TextUtils.isEmpty(mPicPost.getAdAttachment().getStatusInfo())) {
                            mTvAdStatus.setVisibility(View.VISIBLE);
                            mTvAdStatus.setText(mPicPost.getAdAttachment().getStatusInfo());
                        } else {
                            mTvAdStatus.setVisibility(View.GONE);
                        }
                    } else {
                        mTvAdStatus.setVisibility(View.GONE);
                    }
                } else {
                    mTvAdStatus.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onItemClick(View view, int position, Object t) {
        if (mListener != null) {
            mListener.onPostAreaClick(mPicPost, EventConstants.CLICK_AREA_PIC);
        }
        if (view.getContext() instanceof Activity) {
            PhotoSelector.previewPics(view.getContext(), mPicPost.getNickName(), position, mPicPost.listPicInfo());
        }
        if (mHostAdapter instanceof FeedRecyclerViewAdapter) {
            ExposeEventReporter.INSTANCE.reportPostClick(mPicPost, ((FeedRecyclerViewAdapter) mHostAdapter).getFeedSource(), EventLog1.FeedView.FeedClickArea.PIC);
        }
    }
}
