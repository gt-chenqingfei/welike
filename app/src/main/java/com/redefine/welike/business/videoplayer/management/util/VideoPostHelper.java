package com.redefine.welike.business.videoplayer.management.util;

import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/9/26
 */
public class VideoPostHelper {

    public static List<VideoPost> filterVideoPost(List<PostBase> postBases) {
        if (CollectionUtil.isEmpty(postBases)) {
            return new ArrayList<>();
        }
        List<VideoPost> videoPosts = new ArrayList<>();
        for (PostBase postBase : postBases) {
            if (postBase instanceof VideoPost && !postBase.isDeleted()) {
                VideoPost videoPost = (VideoPost) postBase;
                if (!TextUtils.equals(videoPost.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                    videoPosts.add(videoPost);
                }
            }
        }
        return videoPosts;
    }

    public static List<VideoPost> subVideoPost(List<PostBase> postBases, int position) {
        if (CollectionUtil.isEmpty(postBases)) {
            return new ArrayList<>();
        }
        List<VideoPost> videoPosts = new ArrayList<>();
        for (int i = 0; i <= position; i++) {
            PostBase postBase = postBases.get(i);
            if (postBase instanceof VideoPost && !postBase.isDeleted()) {
                VideoPost videoPost = (VideoPost) postBase;
                if (!TextUtils.equals(videoPost.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                    videoPosts.add(videoPost);
                }
            }
            if (postBase instanceof ForwardPost && !postBase.isDeleted()) {
                ForwardPost forwardPost = (ForwardPost) postBase;
                PostBase rootPost = forwardPost.getRootPost();
                if (rootPost instanceof VideoPost && !rootPost.isDeleted()) {
                    VideoPost videoPost = (VideoPost) rootPost;
                    if (!TextUtils.equals(videoPost.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                        videoPosts.add(videoPost);
                    }
                }
            }
        }
        return videoPosts;
    }
}
