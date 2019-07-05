package com.redefine.multimedia.player.base;

import android.view.View;

import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;

public interface IVideoView {

    void setDataSource(String url);

    void setMediaController(IMediaController mediaController);

    View getView();

    boolean pause();

    boolean start();

    void destroy();

    void setMediaPlayerInvoker(MediaPlayerInvoker invoker);

    void onPause();

    void onResume();

    String getPlayUrl();
}
