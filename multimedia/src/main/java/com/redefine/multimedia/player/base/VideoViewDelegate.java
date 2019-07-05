package com.redefine.multimedia.player.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;

public class VideoViewDelegate {

    private VideoViewType mVideoType;
    private IVideoView mVideoPlayer;

    public VideoViewDelegate(Context context, ViewGroup viewGroup, VideoViewType type) {
        mVideoType = type;
        mVideoPlayer = VideoViewFactory.createVideoPlayer(context, viewGroup, type);
    }

    public void setDataSource(String url) {
        mVideoPlayer.setDataSource(url);
    }

    public void onPause() {
        mVideoPlayer.onPause();
    }

    public void onResume() {
        mVideoPlayer.onResume();
    }

    public void onDestroy() {
        mVideoPlayer.destroy();
    }

    public VideoViewType getVideoType() {
        return mVideoType;
    }

    public View getView() {
        return mVideoPlayer.getView();
    }

    public void setMediaController(IMediaController mediaController) {
        mVideoPlayer.setMediaController(mediaController);
    }

    public void setMediaPlayerInvoker(MediaPlayerInvoker invoker) {
        mVideoPlayer.setMediaPlayerInvoker(invoker);
    }

    public String getPlayUrl() {
        return mVideoPlayer.getPlayUrl();
    }
}
