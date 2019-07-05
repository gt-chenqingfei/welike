package com.redefine.multimedia.player.base;

import android.content.Context;
import android.view.ViewGroup;

import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.multimedia.player.h5.WebVideoView;
import com.redefine.multimedia.player.youtube.YoutubeVideoView;

public class VideoViewFactory {

    public static IVideoView createVideoPlayer(Context context, ViewGroup viewGroup, VideoViewType type) {
        IVideoView videoView;
        switch (type) {
            case WEB:
                videoView = new WebVideoView(viewGroup);
                break;
            case APOLLO:
                videoView = new ApolloVideoView(context);
                break;
            case YOUTUBE:
                videoView = new YoutubeVideoView(context);
                break;
            default:
                videoView = new ApolloVideoView(context);
                break;
        }
        return videoView;
    }
}
