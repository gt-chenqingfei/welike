package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.util.DefaultVideoClickHandler;
import com.redefine.welike.business.user.ui.adapter.UserPostFeedAdapter;
import com.redefine.welike.business.videoplayer.management.VideoPlayerConstants;
import com.redefine.welike.business.videoplayer.management.util.VideoPostHelper;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.view.FeedListVideoController;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/30.
 */

public class ForwardFeedVideoViewHolder extends ForwardFeedTextViewHolder {
    private final View mVideoFeedContainer;
    private final FrameLayout mVideoPlayerView;
    private final FeedListVideoController mVideoController;

    public ForwardFeedVideoViewHolder(View inflate, IFeedOperationListener mUserFollowListener) {
        super(inflate, mUserFollowListener);
        mVideoFeedContainer = itemView.findViewById(R.id.video_feed_container);
        mVideoPlayerView = itemView.findViewById(R.id.video_feed_video_player);
        mVideoController = itemView.findViewById(R.id.video_controller);
        mVideoPlayerView.setBackground(inflate.getResources().getDrawable(R.drawable.drawable_shape_feed_forward_bg));
    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final PostBase postBase) {
        super.bindViews(adapter, postBase);
        if (postBase instanceof ForwardPost) {
            final ForwardPost forwardPost = (ForwardPost) postBase;
            if (forwardPost.getRootPost() instanceof VideoPost) {
                String source = null;
                if (adapter instanceof FeedRecyclerViewAdapter) {
                    source = ((FeedRecyclerViewAdapter) adapter).getFeedSource();
                }
                final VideoPost videoPost = (VideoPost) forwardPost.getRootPost();
                mVideoController.setCoverUrl(mVideoFeedContainer, postBase, videoPost, videoPost.getWidth(), videoPost.getHeight());
                mVideoController.setPlaySource(source);
                if (!TextUtils.isEmpty(videoPost.getVideoUrl())) {
                    final String finalSource = source;
                    mVideoPlayerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mListener != null) {
                                mListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_VIDEO);
                            }
                            if (TextUtils.equals(videoPost.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                                List<VideoPost> videoPosts = new ArrayList<>();
                                videoPosts.add(videoPost);
                                new DefaultVideoClickHandler(v.getContext()).onVideoClick(videoPosts, null, finalSource, iBrowseClickListener == null, "", mVideoController.getCurrentPosition());
                            } else {
                                List<VideoPost> videoPosts = new ArrayList<>();
                                String videoListType;
                                if (adapter instanceof UserPostFeedAdapter) {
                                    videoPosts.addAll(VideoPostHelper.subVideoPost(((UserPostFeedAdapter) adapter).getPostData(), getLayoutPosition()));
                                    videoListType = VideoPlayerConstants.VIDEO_LIST_TYPE_PROFILE;
                                } else {
                                    videoPosts.add(videoPost);
                                    videoListType = VideoPlayerConstants.VIDEO_LIST_TYPE_SIMILAR;
                                }
                                new DefaultVideoClickHandler(v.getContext()).onVideoClick(videoPosts, null, finalSource, iBrowseClickListener == null, videoListType, mVideoController.getCurrentPosition());
                            }
                            if (adapter instanceof FeedRecyclerViewAdapter) {
                                ExposeEventReporter.INSTANCE.reportPostClick(postBase, ((FeedRecyclerViewAdapter) adapter).getFeedSource(), EventLog1.FeedView.FeedClickArea.VIDEO);
                            }
                        }
                    });
                }
            }
        }
    }
}
