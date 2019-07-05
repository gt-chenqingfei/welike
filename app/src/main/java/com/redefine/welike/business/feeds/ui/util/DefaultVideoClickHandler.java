package com.redefine.welike.business.feeds.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.videoplayer.management.VideoPlayerConstants;
import com.redefine.welike.business.videoplayer.ui.activity.VideoListPlayerActivity;
import com.redefine.welike.commonui.photoselector.PhotoSelector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/17.
 */

public class DefaultVideoClickHandler {

    private final Context mContext;

    public DefaultVideoClickHandler(Context context) {
        mContext = context;
    }

    public void onVideoClick(List<VideoPost> videoPosts, ForwardPost forwardPost, String playSource, boolean auth, String videoListType, int currentPosition) {
        if (CollectionUtil.isEmpty(videoPosts)) {
            return;
        }
        VideoPost targetPost = videoPosts.get(videoPosts.size() - 1);
        if (TextUtils.equals(targetPost.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
//            YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(mActivity);
//            if (result == YouTubeInitializationResult.SUCCESS) {
//                Bundle bundle = new Bundle();
//                bundle.putString(YoutubeConstant.KEY_URL, videoPost.getVideoUrl());
//                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_YOUTUBE_PLAYER, bundle));
//            } else {
//                Bundle bundle = new Bundle();
//                bundle.putString(WebViewConstant.KEY_URL, videoPost.getVideoUrl());
//                bundle.putBoolean(WebViewConstant.KEEP_SCREEN_ON, true);
//                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
//            }

            String postId = forwardPost == null ? "" : forwardPost.getPid();
            String postUid = forwardPost == null ? "" : forwardPost.getUid();
            String postUserHeader = forwardPost == null ? targetPost.getHeadUrl() : forwardPost.getHeadUrl();
            String postUserNick = forwardPost == null ? targetPost.getNickName() : forwardPost.getNickName();
            String rootPostId = targetPost.getPid();
            String rootPostUid = targetPost.getUid();
            String videoSource = PlayerConstant.VIDEO_SITE_YOUTUBE;

            PhotoSelector.launchPlayPostVideo((Activity) mContext, targetPost.getNickName(), targetPost.getCoverUrl(), videoSource, targetPost.getVideoUrl(), postId, postUid, rootPostId, rootPostUid, playSource, postUserHeader, postUserNick);
        } else {

//            String postId = forwardPost == null ? "" : forwardPost.getPid();
//            String postUid = forwardPost == null ? "" : forwardPost.getUid();
//            String postUserHeader = forwardPost == null ? videoPost.getHeadUrl() : forwardPost.getHeadUrl();
//            String postUserNick = forwardPost == null ? videoPost.getNickName() : forwardPost.getNickName();
//            String rootPostId = videoPost.getPid();
//            String rootPostUid = videoPost.getUid();
//            String videoSource = PlayerConstant.VIDEO_SITE_DEFAULT;
//
//            PhotoSelector.launchPlayPostVideo((Activity) mActivity, videoPost.getNickName(), videoPost.getCoverUrl(), videoSource, videoPost.getVideoUrl(), postId, postUid, rootPostId, rootPostUid, playSource, postUserHeader, postUserNick);
            Intent intent = new Intent(mContext, VideoListPlayerActivity.class);
            intent.putExtra(VideoPlayerConstants.INTENT_EXTRA_POST, (Serializable) videoPosts);
            intent.putExtra(VideoPlayerConstants.INTENT_EXTRA_POSITION, videoPosts.size() - 1);
            intent.putExtra(VideoPlayerConstants.INTENT_EXTRA_AUTH, auth);
            intent.putExtra(VideoPlayerConstants.INTENT_EXTRA_SOURCE, playSource);
            intent.putExtra(VideoPlayerConstants.INTENT_EXTRA_TYPE, videoListType);
            intent.putExtra(VideoPlayerConstants.INTENT_EXTRA_SEEK, currentPosition);
            mContext.startActivity(intent);
        }
    }
}
