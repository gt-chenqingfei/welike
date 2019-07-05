package com.redefine.welike.business.videoplayer.management.manager;

import com.redefine.multimedia.player.apollo.ApolloVideoView;

import java.util.List;


/**
 * Created by nianguowang on 2018/8/11
 */
public interface IVideoPlayerManager {

    /**
     * A interface to preload video.
     * @param videoUrl
     */
    void mediaPreload(String videoUrl);

    void mediaPreload(List<String> videoUrls);

    /**
     * Seek to position to play.
     * @param position
     */
    void seekPosition(int position);


    /**
     * Call it if you have direct url or path to video source
     * @param videoPlayerView - the actual video player
     * @param videoUrl - the link to the video source
     */
    void playNewVideo(ApolloVideoView videoPlayerView, String videoUrl);

    /**
     * Call it if you need to pause the playing video.
     */
    void pauseCurrentVideo();

    /**
     * Call it if you need continue to play the paused video.
     */
    void continuePlayVideo();

    /**
     * To get if current video is playing.
     * @return if playing ,return true, else false.
     */
    boolean isCurrentVideoPlaying();

    /**
     * Call it if Activity onPause.
     */
    void onPause();

    /**
     * Call it if Activity onResume.
     */
    void onResume();

    /**
     * Call it if Activity onDestroy.
     */
    void onDestroy();

}
