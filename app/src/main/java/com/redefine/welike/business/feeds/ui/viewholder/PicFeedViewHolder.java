package com.redefine.welike.business.feeds.ui.viewholder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.feeds.management.bean.PicPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.adapter.NineGridAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.photoselector.PhotoSelector;
import com.redefine.welike.commonui.view.MultiGridView;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by liwb on 2018/1/11.
 */

public class PicFeedViewHolder extends TextFeedViewHolder implements MultiGridView.OnItemClickListener {

    private final NineGridAdapter mAdapter;
    private final MultiGridView mMultiGridView;
    private PicPost mPicPost;
    private RecyclerView.Adapter mHostAdapter;


    public PicFeedViewHolder(View itemView, IFeedOperationListener listener) {
        super(itemView, listener);
        mMultiGridView = itemView.findViewById(R.id.pic_feed_multi_grid_view);
        mMultiGridView.setOnItemClickListener(this);
        mAdapter = new NineGridAdapter();
        mAdapter.setForwardFeed(false);
        mMultiGridView.setAdapter(mAdapter);


    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, PostBase postBase) {
        super.bindViews(adapter, postBase);
        mHostAdapter = adapter;
        if (postBase instanceof PicPost) {
            mPicPost = (PicPost) postBase;
            mAdapter.setData(mPicPost.listPicInfo());
            mCommonFeedBottomView.showShareAnim(false);
        }
    }

    @Override
    public void onItemClick(View view, int position, Object t) {

        if (mListener != null) {
            mListener.onPostAreaClick(mPicPost, EventConstants.CLICK_AREA_PIC);
        }

        if (view.getContext() instanceof Activity && mIsImageClickable) {
            PhotoSelector.previewPics(view.getContext(), mPicPost.getNickName(), position, mPicPost.listPicInfo());
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
            bundle.putSerializable(FeedConstant.KEY_FEED, mPicPost);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
        }

        if (mHostAdapter instanceof FeedRecyclerViewAdapter) {
            ExposeEventReporter.INSTANCE.reportPostClick(mPicPost, ((FeedRecyclerViewAdapter) mHostAdapter).getFeedSource(), EventLog1.FeedView.FeedClickArea.PIC);
        }
    }
}
