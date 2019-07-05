package com.redefine.welike.business.videoplayer.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.commonui.share.request.ShareCountReportManager;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.videoplayer.management.command.CommandInvoker;
import com.redefine.welike.business.videoplayer.management.manager.IVideoPlayerManager;
import com.redefine.welike.business.videoplayer.management.manager.SingleVideoPlayerManager;
import com.redefine.welike.business.videoplayer.ui.view.PostController;
import com.redefine.welike.business.videoplayer.ui.view.VideoErrorView;
import com.redefine.welike.business.videoplayer.ui.viewholder.FullScreenVideoHolder;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.statistical.EventConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by nianguowang on 2018/8/7
 */
public class VideoListAdapter extends BaseRecyclerAdapter implements FollowUserManager.FollowUserCallback, ShareCountReportManager.ShareCountCallback, IDataProvider<List<VideoPost>> {

    private List<VideoPost> mVideoPosts = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private boolean mAuth;
    private IVideoPlayerManager mSingleVideoPlayerManager;
    private CommandInvoker.PostCommandInvoker mPostCommandInvoker;
    private CommandInvoker.VideoCommandInvoker mVideoCommandInvoker;

    public void setOrientation(final int orientation) {
        this.orientation = orientation;
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                for (ScreenListener listener : listeners) {
                    listener.onChange(orientation);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);

    }

    public int orientation = ORIENTATION_PORTRAIT;

    public VideoListAdapter(CommandInvoker.PostCommandInvoker postCommandInvoker, CommandInvoker.VideoCommandInvoker videoCommandInvoker, boolean auth) {
        mAuth = auth;
        mPostCommandInvoker = postCommandInvoker;
        mVideoCommandInvoker = videoCommandInvoker;
        mSingleVideoPlayerManager = new SingleVideoPlayerManager();
        FollowUserManager.getInstance().register(this);
        ShareCountReportManager.getInstance().register(this);
    }

    @Override
    public List<VideoPost> getData() {
        return mVideoPosts;
    }

    @Override
    public String getSource() {
        return EventConstants.FEED_PAGE_VIDEO_PLAYER;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    public void addNewVideos(final List<VideoPost> videoPosts) {
        if (CollectionUtil.isEmpty(videoPosts)) {
            return;
        }
        try {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return CollectionUtil.getCount(mVideoPosts);
                }

                @Override
                public int getNewListSize() {
                    return CollectionUtil.getCount(videoPosts);
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return TextUtils.equals(mVideoPosts.get(oldItemPosition).getPid(), videoPosts.get(newItemPosition).getPid());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mVideoPosts.get(oldItemPosition) == videoPosts.get(newItemPosition);
                }
            });
            mVideoPosts.clear();
            mVideoPosts.addAll(videoPosts);
            diffResult.dispatchUpdatesTo(this);
        } catch (Exception e) {
            //do nothing
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FullScreenVideoHolder holder = new FullScreenVideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.full_screen_video_item_layout, parent, false));
        addListener(holder.listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final VideoPost videoPost = mVideoPosts.get(position);
        final FullScreenVideoHolder fullScreenVideoHolder = (FullScreenVideoHolder) holder;
        fullScreenVideoHolder.bindViews(videoPost);
        mSingleVideoPlayerManager.mediaPreload(videoPost.getVideoUrl());
        if (position + 1 < mVideoPosts.size()) {
            VideoPost videoPost1 = mVideoPosts.get(position + 1);
            mSingleVideoPlayerManager.mediaPreload(videoPost1.getVideoUrl());
        }
        fullScreenVideoHolder.mPostController.setPostCommandInvoker(mPostCommandInvoker);
        fullScreenVideoHolder.mVideoController.setVideoCommandInvoker(mVideoCommandInvoker);
        fullScreenVideoHolder.mVideoController.changeConfig(orientation);
        fullScreenVideoHolder.mPostController.changeConfig((orientation));
        ((FullScreenVideoHolder) holder).mErrorView.setOnErrorClickListener(new VideoErrorView.OnErrorClickListener() {
            @Override
            public void onErrorClick() {
                mSingleVideoPlayerManager.onDestroy();
                mSingleVideoPlayerManager.playNewVideo(((FullScreenVideoHolder) holder).mVideoView, videoPost.getVideoUrl());
            }
        });
        if (!mAuth) {
            fullScreenVideoHolder.mPostController.disableFollow();
        }
    }

    @Override
    public int getItemCount() {
        return mVideoPosts.size();
    }

    public void seekPosition(int duration) {
        mSingleVideoPlayerManager.seekPosition(duration);
    }

    public VideoPost playVideo(int position, ApolloVideoView videoPlayerView) {
        VideoPost videoPost = mVideoPosts.get(position);
        videoPlayerView.onVideoSizeChanged(null, videoPost.getWidth(), videoPost.getHeight());
        mSingleVideoPlayerManager.playNewVideo(videoPlayerView, videoPost.getVideoUrl());
        return videoPost;
    }

    public void onPause() {
        mSingleVideoPlayerManager.onPause();
    }

    public void onResume() {
        mSingleVideoPlayerManager.onResume();
    }

    public void destroy() {
        mSingleVideoPlayerManager.onDestroy();
        FollowUserManager.getInstance().unregister(this);
        ShareCountReportManager.getInstance().unregister(this);
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnFollow(uid);
        } else {
            refreshOnUnFollow(uid);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnUnFollow(uid);
        } else {
            refreshOnFollow(uid);
        }
    }

    public void refreshOnUnFollow(String uid) {
        for (VideoPost postBase : mVideoPosts) {
            if (TextUtils.equals(postBase.getUid(), uid)) {
                postBase.setFollowing(false);
                int index = mVideoPosts.indexOf(postBase);
                notifyPostStatusChanged(index, postBase);
            }
        }
    }

    public void refreshOnFollow(String uid) {
        for (VideoPost postBase : mVideoPosts) {
            if (TextUtils.equals(postBase.getUid(), uid)) {
                postBase.setFollowing(true);
                int index = mVideoPosts.indexOf(postBase);
                notifyPostStatusChanged(index, postBase);
            }
        }
    }

    private void notifyPostStatusChanged(int position, VideoPost videoPost) {
        if (mRecyclerView == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        View view = layoutManager.findViewByPosition(position);
        if (view != null) {
            PostController postController = view.findViewById(R.id.video_post_controller);
            postController.bindPost(videoPost);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;

    }

    private ArrayList<ScreenListener> listeners = new ArrayList<>();


    private void addListener(ScreenListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void onShareReportSuccess(String postId) {
        if (!CollectionUtil.isEmpty(mVideoPosts)) {
            for (int i = 0; i < mVideoPosts.size(); i++) {
                VideoPost videoPost = mVideoPosts.get(i);
                if (TextUtils.equals(postId, videoPost.getPid())) {
                    videoPost.setShareCount(videoPost.getShareCount() + 1);
                    notifyPostStatusChanged(i, videoPost);
                }
            }
        }
    }

    @Override
    public void onShareReportFail(int errorCode) {

    }

    public interface ScreenListener {
        void onChange(int orientation);
    }
}
