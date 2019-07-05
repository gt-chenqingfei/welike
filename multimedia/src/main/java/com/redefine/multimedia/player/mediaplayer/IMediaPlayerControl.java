package com.redefine.multimedia.player.mediaplayer;

public interface IMediaPlayerControl {
    boolean    start();
    boolean    pause();
    int     getDuration();
    int     getCurrentPosition();
    void    seekTo(int pos);
    boolean isPlaying();
    int     getBufferPercentage();
    void setMute(boolean isMute);
    String getPlayUrl();
}