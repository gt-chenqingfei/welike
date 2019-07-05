package com.redefine.welike.business.videoplayer.management.manager;

import com.UCMobile.Apollo.MediaPreload;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.BuildConfig;
import com.redefine.welike.statistical.EventLog1;

import java.util.List;

import java.util.List;

/**
 * Created by nianguowang on 2018/8/11
 */
public class SingleVideoPlayerManager implements IVideoPlayerManager {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = BuildConfig.LOG_DEBUG;

    private ApolloVideoView mCurrentPlayer = null;

    public SingleVideoPlayerManager() {
        MediaPreload.supportPreloadBySo(MyApplication.getAppContext());
    }

    @Override
    public void mediaPreload(List<String> urls) {
        if (CollectionUtil.isEmpty(urls)) {
            return;
        }

        LogUtil.v(TAG, ">> mediaPreload, urls " + urls);
        for (String url : urls) {
            MediaPreload.Add(url, url, null, null);
        }
    }

    @Override
    public void mediaPreload(String videoUrl) {
        LogUtil.v(TAG, ">> mediaPreload, videoUrl " + videoUrl);
        MediaPreload.Add(videoUrl, videoUrl, null, null);
    }

    @Override
    public void seekPosition(int position) {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.seekTo(position);
        }
    }

    @Override
    public void playNewVideo(ApolloVideoView videoPlayerView, String videoUrl) {
        if (SHOW_LOGS)
            LogUtil.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", videoPlayerView " + videoPlayerView);

        boolean currentPlayerIsActive = mCurrentPlayer == videoPlayerView;
        boolean isAlreadyPlayingTheFile =
                mCurrentPlayer != null &&
                        videoUrl.equals(mCurrentPlayer.getPlayUrl());
        if (currentPlayerIsActive) {
            if (isAlreadyPlayingTheFile) {
                if (SHOW_LOGS)
                    LogUtil.v(TAG, "playNewVideo, videoPlayer " + videoPlayerView + " is already playing");
            } else {
                startNewPlayback(videoPlayerView, videoUrl);
            }
        } else {
            startNewPlayback(videoPlayerView, videoUrl);
        }

        if (SHOW_LOGS)
            LogUtil.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", videoUrl " + videoUrl);
    }

    private void startNewPlayback(ApolloVideoView videoPlayerView, String videoUrl) {
        if (SHOW_LOGS)
            LogUtil.v(TAG, "startNewPlayback videoPlayerView : " + videoPlayerView + " , videoUrl : " + videoUrl);
        if (mCurrentPlayer != null) {
            mCurrentPlayer.destroy();
        }
        mCurrentPlayer = videoPlayerView;
        mCurrentPlayer.setDataSource(videoUrl);
    }

    @Override
    public void pauseCurrentVideo() {
        if (SHOW_LOGS) LogUtil.v(TAG, ">>pauseCurrentVideo");
        if (mCurrentPlayer.isPlaying()) {
            mCurrentPlayer.pause();
        }
        if (SHOW_LOGS) LogUtil.v(TAG, ">>pauseCurrentVideo");
    }

    @Override
    public void continuePlayVideo() {
        if (SHOW_LOGS) LogUtil.v(TAG, ">>continuePlayVideo");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.start();
        }
        if (SHOW_LOGS) LogUtil.v(TAG, ">>continuePlayVideo");
    }

    @Override
    public boolean isCurrentVideoPlaying() {
        if (SHOW_LOGS) LogUtil.v(TAG, ">>isCurrentVideoPlaying");
        return mCurrentPlayer != null && mCurrentPlayer.isPlaying();
    }

    @Override
    public void onPause() {
        if (SHOW_LOGS) LogUtil.v(TAG, ">>onPause");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.onPause();
        }
        if (SHOW_LOGS) LogUtil.v(TAG, "<<onPause");
    }

    @Override
    public void onResume() {
        if (SHOW_LOGS) LogUtil.v(TAG, ">>onResume");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.onResume();
        }
        if (SHOW_LOGS) LogUtil.v(TAG, "<<onResume");
    }

    @Override
    public void onDestroy() {
        if (SHOW_LOGS) LogUtil.v(TAG, ">>onDestroy");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.destroy();
        }
        mCurrentPlayer = null;
        if (SHOW_LOGS) LogUtil.v(TAG, "<<onDestroy");
    }

}
