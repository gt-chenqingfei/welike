package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.R;
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

import kotlin.jvm.functions.Function0;

/**
 * Created by MR on 2018/1/30.
 */

public class VideoFeedViewHolder extends TextFeedViewHolder {
    private final View mVideoFeedContainer;
    private final FrameLayout mVideoContainer;
    private final FeedListVideoController mVideoController;

    public VideoFeedViewHolder(View itemView, IFeedOperationListener listener) {
        super(itemView, listener);
        mVideoFeedContainer = itemView.findViewById(R.id.video_feed_container);
        mVideoContainer = itemView.findViewById(R.id.video_feed_video_player);
        mVideoController = itemView.findViewById(R.id.video_controller);
        mVideoContainer.setBackground(itemView.getResources().getDrawable(R.color.transparent));
    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final PostBase postBase) {
        super.bindViews(adapter, postBase);
        if (postBase instanceof VideoPost) {
            final VideoPost videoPost = (VideoPost) postBase;
            String source = null;
            if (adapter instanceof FeedRecyclerViewAdapter) {
                source = ((FeedRecyclerViewAdapter) adapter).getFeedSource();
            }
            mVideoController.setCoverUrl(mVideoFeedContainer, postBase, videoPost, videoPost.getWidth(), videoPost.getHeight());
            mVideoController.setPlaySource(source);
            if (!TextUtils.isEmpty(videoPost.getVideoUrl())) {
                final String finalSource = source;
                mVideoContainer.setOnClickListener(new View.OnClickListener() {
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
}
