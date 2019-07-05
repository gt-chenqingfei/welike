package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedBottomContract;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.util.DefaultVideoClickHandler;
import com.redefine.welike.business.feeds.ui.view.CommonFeedBottomView;
import com.redefine.welike.business.feeds.ui.view.VideoFeedHeadView;
import com.redefine.welike.business.videoplayer.management.VideoPlayerConstants;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.view.FeedListVideoController;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function0;

/**
 * Created by MR on 2018/1/30.
 */

public class VideoFeedSpecialViewHolder extends BaseRecyclerViewHolder<PostBase> {
    private View mVideoFeedContainer;
    private final FrameLayout mVideoContainer;
    private final FeedListVideoController mVideoController;

    private CommonFeedBottomView mCommonFeedBottomView;
    private VideoFeedHeadView mVideoFeedHeadView;

    private IBrowseClickListener iBrowseClickListener;
    private OnPostButtonClickListener mPostButtonListener;

    public VideoFeedSpecialViewHolder(View itemView, IFeedOperationListener listener) {
        super(itemView);
        mVideoFeedContainer = itemView.findViewById(R.id.video_feed_container);
        mVideoContainer = itemView.findViewById(R.id.video_feed_video_player);
        mVideoController = itemView.findViewById(R.id.video_controller);

        mCommonFeedBottomView = ICommonFeedBottomContract.CommonFeedBottomFactory.createView(itemView.findViewById(R.id.common_feed_bottom_root_view));
        mVideoFeedHeadView = new VideoFeedHeadView(itemView, listener);

    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final PostBase data) {
        super.bindViews(adapter, data);

        mVideoFeedHeadView.bindViews(data, adapter);

        mCommonFeedBottomView.bindViews(data, adapter);
        mCommonFeedBottomView.setDismissDivider(true);
        mCommonFeedBottomView.setDismissLocation(true);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iBrowseClickListener != null) {
                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_POST, false, 0);
                }
                onClickFeedRootView(data);
                if (adapter instanceof FeedRecyclerViewAdapter) {
                    ExposeEventReporter.INSTANCE.reportPostClick(data, ((FeedRecyclerViewAdapter) adapter).getFeedSource(), EventLog1.FeedView.FeedClickArea.VIDEO);
                }
            }
        });



        if (data instanceof VideoPost) {
            final VideoPost videoPost = (VideoPost) data;
            String source = null;
            if (adapter instanceof FeedRecyclerViewAdapter) {
                source = ((FeedRecyclerViewAdapter) adapter).getFeedSource();
            }
            mVideoController.setCoverUrl(mVideoFeedContainer, data, videoPost, videoPost.getWidth(), videoPost.getHeight());
            mVideoController.setPlaySource(source);
            mVideoController.setPostContent(data.getSummary());
            if (!TextUtils.isEmpty(videoPost.getVideoUrl())) {
                final String finalSource = source;
                mVideoContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mPostButtonListener != null) {
                            mPostButtonListener.onPostAreaClick(data, EventConstants.CLICK_AREA_VIDEO);
                        }
                        List<VideoPost> videoPosts = new ArrayList<>();
                        videoPosts.add((VideoPost) data);
                        new DefaultVideoClickHandler(v.getContext()).onVideoClick(videoPosts, null, finalSource, iBrowseClickListener == null, VideoPlayerConstants.VIDEO_LIST_TYPE_SIMILAR, mVideoController.getCurrentPosition());
                        if (adapter instanceof FeedRecyclerViewAdapter) {
                            ExposeEventReporter.INSTANCE.reportPostClick(data, finalSource, EventLog1.FeedView.FeedClickArea.VIDEO);
                        }
                    }
                });
            }
            mCommonFeedBottomView.showShareAnim(false);
            mVideoController.setFinishCallback(new Function0() {
                @Override
                public Object invoke() {
                    mCommonFeedBottomView.showShareAnim(true);
                    return null;
                }
            });
        }
    }

    public VideoFeedHeadView getFeedHeadView() {
        return mVideoFeedHeadView;
    }

    private void onClickFeedRootView(PostBase postBase) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
        mCommonFeedBottomView.setBrowseClickListener(iBrowseClickListener);
        mVideoFeedHeadView.setBrowseClickListener(iBrowseClickListener);
    }

    public void setPostButtonListener(OnPostButtonClickListener mPostButtonListener) {
        this.mPostButtonListener = mPostButtonListener;
        mCommonFeedBottomView.setOnPostButtonClickListener(mPostButtonListener);
        mVideoFeedHeadView.setPostButtonClickListener(mPostButtonListener);
    }

}

