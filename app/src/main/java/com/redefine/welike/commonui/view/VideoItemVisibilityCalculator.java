package com.redefine.welike.commonui.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;

/**
 * 用来计算Feed流中应该播放视频的item，与FeedRecyclerViewAdapter中的Item强耦合。
 * <p>
 * Created by nianguowang on 2018/7/26
 */
public class VideoItemVisibilityCalculator extends RecyclerView.OnScrollListener {

    private String TAG = "VideoItemVisibilityCalculator";

    private static final int PLAY_ITEM_INVALID = -1;
    private int mCurrentPlayItem = PLAY_ITEM_INVALID;
    private VideoPlayStateCallback mCallback;


    private int mOldPlayItem = -1;

    public VideoItemVisibilityCalculator(VideoPlayStateCallback callback) {
        mCallback = callback;
    }

    public interface VideoPlayStateCallback {
        void stopPlayback();

        void startNewPlay(int position, ApolloVideoView videoPlayerView);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                LogUtil.d("wng", "VideoItemVisibilityCalculator onScrollStateChanged : SCROLL_STATE_DRAGGING");
                calculatorPauseItem(recyclerView);
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                LogUtil.d("wng", "VideoItemVisibilityCalculator onScrollStateChanged : SCROLL_STATE_SETTLING");
                calculatorPauseItem(recyclerView);
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                LogUtil.d("wng", "VideoItemVisibilityCalculator onScrollStateChanged : SCROLL_STATE_IDLE");
                calculatorPlayItem(recyclerView);
                break;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.e(TAG, "onScrolled: ");
        calculatorPauseItem(recyclerView);
    }

    public void resetCurrentPlayItem() {
        mCurrentPlayItem = PLAY_ITEM_INVALID;
    }

    /**
     * 当列表正在滑动的时候，计算当前播放当VideoView，是否划出屏幕外，如果是，则停止播放。
     *
     * @param recyclerView
     */
    private void calculatorPauseItem(RecyclerView recyclerView) {
        if (1==1) {
            return;
        }
        if (!NetWorkUtil.isWifiConnected(recyclerView.getContext())) {
            return;
        }
        if (mCurrentPlayItem == mOldPlayItem || mCurrentPlayItem == PLAY_ITEM_INVALID) {
            return;
        }

        Log.e(TAG, "calculatorPauseItem: ");

        mOldPlayItem = mCurrentPlayItem;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            if (mCurrentPlayItem < firstVisibleItemPosition || mCurrentPlayItem > lastVisibleItemPosition) {
                mCurrentPlayItem = PLAY_ITEM_INVALID;
                if (mCallback != null) {
                    mCallback.stopPlayback();
                }
            } else if (mCurrentPlayItem == firstVisibleItemPosition || mCurrentPlayItem == lastVisibleItemPosition) {
                View currentPlayView = layoutManager.getChildAt(mCurrentPlayItem - firstVisibleItemPosition);
                if (currentPlayView == null) {
                    return;
                }
                View videoView = currentPlayView.findViewById(R.id.video_feed_video_player);
                if (videoView != null) {
                    int height = videoView.getHeight();
                    Rect rect = new Rect();
                    videoView.getLocalVisibleRect(rect);
                    int showHeight = rect.bottom - rect.top;
                    if (showHeight < height / 2) {
                        mCurrentPlayItem = PLAY_ITEM_INVALID;
                        if (mCallback != null) {
                            mCallback.stopPlayback();
                        }
                    }
                }
            }
        }

        mOldPlayItem = PLAY_ITEM_INVALID;
    }

    /**
     * 当列表滑动静止当时候，计算是否有VideoView是否显示在屏幕上，并且其显示面积满足播放要求；如果有，则让其播放。
     *
     * @param recyclerView
     */
    private void calculatorPlayItem(RecyclerView recyclerView) {
        if (1==1) {
            return;
        }
        if (!NetWorkUtil.isWifiConnected(recyclerView.getContext())) {
            return;
        }

        Log.e(TAG, "calculatorPlayItem: ");
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                View indexView = layoutManager.getChildAt(i - firstVisibleItemPosition);
                if (indexView == null) {
                    continue;
                }
                ViewGroup videoViewContainer = indexView.findViewById(R.id.video_feed_video_player);
                FeedListVideoController videoController = indexView.findViewById(R.id.video_controller);
                if (videoViewContainer != null && videoController != null) {
                    int height = videoViewContainer.getHeight();
                    Rect rect = new Rect();
                    videoController.getLocalVisibleRect(rect);
                    if (rect.bottom > 0) {
                        int showHeight = rect.bottom - rect.top;
                        if (showHeight >= height / 2) {
                            if (mCurrentPlayItem == i) {
                                return;
                            }
                            mCurrentPlayItem = i;
                            videoViewContainer.removeAllViews();
                            ApolloVideoView apolloVideoView = new ApolloVideoView(indexView.getContext().getApplicationContext());
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                            videoViewContainer.addView(apolloVideoView.getView(), layoutParams);
                            apolloVideoView.setMediaController(videoController);
                            apolloVideoView.setMediaPlayerInvoker(videoController);
                            apolloVideoView.setMute(true);

                            if (mCallback != null) {
                                mCallback.startNewPlay(i, apolloVideoView);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

}
