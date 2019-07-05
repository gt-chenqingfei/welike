package com.redefine.welike.business.videoplayer.ui.viewholder;

import android.view.View;
import android.widget.FrameLayout;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.videoplayer.ui.adapter.VideoListAdapter;
import com.redefine.welike.business.videoplayer.ui.view.PostController;
import com.redefine.welike.business.videoplayer.ui.view.VideoController;
import com.redefine.welike.business.videoplayer.ui.view.VideoErrorView;

/**
 * Created by nianguowang on 2018/8/7
 */
public class FullScreenVideoHolder extends BaseRecyclerViewHolder {

    public VideoErrorView mErrorView;
    public VideoController mVideoController;
    public PostController mPostController;

    public ApolloVideoView mVideoView;

    public FullScreenVideoHolder(View itemView) {
        super(itemView);
        mErrorView = itemView.findViewById(R.id.video_error_view);
        mVideoController = itemView.findViewById(R.id.video_controller);
        mPostController = itemView.findViewById(R.id.video_post_controller);
    }

    public void bindViews(VideoPost videoPost) {
        mPostController.bindPost(videoPost);
        mVideoController.setCoverUrl(videoPost.getCoverUrl());
        mVideoController.bindPostController(mPostController);
    }

    public VideoListAdapter.ScreenListener listener = new VideoListAdapter.ScreenListener() {
        @Override
        public void onChange(int orientation) {
            mVideoController.changeConfig(orientation);
            mPostController.changeConfig(orientation);
        }
    };
}
