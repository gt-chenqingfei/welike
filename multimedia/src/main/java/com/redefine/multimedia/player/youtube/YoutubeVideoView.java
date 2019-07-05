package com.redefine.multimedia.player.youtube;

import android.content.Context;
import android.view.View;

import com.redefine.multimedia.player.base.IVideoView;
import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;

public class YoutubeVideoView implements IVideoView {

    public YoutubeVideoView(Context context) {

    }

    @Override
    public void setDataSource(String url) {

    }

    @Override
    public void setMediaController(IMediaController mediaController) {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public boolean pause() {
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setMediaPlayerInvoker(MediaPlayerInvoker invoker) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public String getPlayUrl() {
        return null;
    }
}
