package com.redefine.welike.business.videoplayer.management;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;
import com.redefine.welike.R;
import com.redefine.welike.business.videoplayer.ui.view.PostController;
import com.redefine.welike.business.videoplayer.ui.view.VideoController;

/**
 * 计算视频详情页（类似抖音的页面）的视频播放暂停逻辑，以及加载下一页的逻辑。
 *
 * Created by nianguowang on 2018/7/26
 */
public class VideoPagerItemCalculator extends RecyclerView.OnScrollListener {

    private VideoPlayStateCallback mCallback;
    private MediaPlayerInvoker mMediaPlayerInvoker;
    private int mCurrentPlayingPosition = -1;
    private int mScrollDirection;

    public VideoPagerItemCalculator(VideoPlayStateCallback callback, MediaPlayerInvoker mediaPlayerInvoker) {
        mCallback = callback;
        mMediaPlayerInvoker = mediaPlayerInvoker;
    }

    public interface VideoPlayStateCallback {
        void startLoadMore();
        void startNewPlay(int position, ApolloVideoView videoPlayerView, int scrollDirection);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                LogUtil.d("wng", "VideoPagerItemCalculator onScrollStateChanged : SCROLL_STATE_DRAGGING");
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                LogUtil.d("wng", "VideoPagerItemCalculator onScrollStateChanged : SCROLL_STATE_SETTLING");
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                LogUtil.d("wng", "VideoPagerItemCalculator onScrollStateChanged : SCROLL_STATE_IDLE");
                calculatorPlayItem(recyclerView);
                break;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mScrollDirection = dy > 0 ? VideoPlayerConstants.SCROLL_DIRECTION_DOWN : VideoPlayerConstants.SCROLL_DIRECTION_UP;
    }

    /**
     * 当列表滑动静止的时候，计算是否有VideoView是否显示在屏幕上，并且其显示面积满足播放要求；如果有，则让其播放。
     * @param recyclerView
     */
    private void calculatorPlayItem(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            LogUtil.d("wng", "VideoPagerItemCalculator calculatorPlayItem firstVisibleItemPosition : " + firstVisibleItemPosition);
            int itemCount = layoutManager.getItemCount();
            LogUtil.d("wng", "VideoPagerItemCalculator calculatorPlayItem itemCount : " + itemCount);
            if (firstVisibleItemPosition >= itemCount - 1) {
                mCallback.startLoadMore();
            }
            if (mCurrentPlayingPosition == firstVisibleItemPosition) {
                return;
            }
            View holderView = layoutManager.getChildAt(0);
            if (holderView != null) {
                ViewGroup rootView = holderView.findViewById(R.id.video_feed_video_player);
                VideoController videoController = holderView.findViewById(R.id.video_controller);
                if (rootView != null && videoController != null) {
                    rootView.removeAllViews();
                    ApolloVideoView mVideoView = new ApolloVideoView(holderView.getContext());
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                    rootView.addView(mVideoView.getView(), layoutParams);
                    mVideoView.setMediaPlayerInvoker(mMediaPlayerInvoker);
                    mVideoView.setMediaController(videoController);
                    videoController.reset();
                    if (mCallback != null) {
                        mCallback.startNewPlay(firstVisibleItemPosition, mVideoView, mScrollDirection);
                    }
                    mCurrentPlayingPosition = firstVisibleItemPosition;
                }
            }
        }
    }

}
