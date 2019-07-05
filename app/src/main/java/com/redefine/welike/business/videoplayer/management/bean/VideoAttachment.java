package com.redefine.welike.business.videoplayer.management.bean;

import com.redefine.welike.business.feeds.management.bean.VideoPost;

/**
 * Created by nianguowang on 2018/9/26
 */
public class VideoAttachment extends AttachmentBase {

    private String videoUrl;
    private String videoWidth;
    private String videoHeight;
    private String coverUrl;
    private String downloadUrl;

    public VideoAttachment() {
        this.type = ATTACHMENT_TYPE_VIDEO;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(String videoWidth) {
        this.videoWidth = videoWidth;
    }

    public String getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(String videoHeight) {
        this.videoHeight = videoHeight;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public static VideoPost toVideoPost(VideoAttachment videoAttachment) {
        if (videoAttachment == null) {
            return null;
        }
        VideoPost videoPost = new VideoPost();
        videoPost.setDownloadUrl(videoAttachment.downloadUrl);
        videoPost.setVideoUrl(videoAttachment.videoUrl);
        videoPost.setCoverUrl(videoAttachment.coverUrl);
        try {
            videoPost.setWidth(Integer.parseInt(videoAttachment.videoWidth));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            videoPost.setHeight(Integer.parseInt(videoAttachment.videoHeight));
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoPost.setPid(videoAttachment.pid);
        videoPost.setUid(videoAttachment.uid);
        videoPost.setCommentCount(videoAttachment.commentsCount);
        videoPost.setDeleted(videoAttachment.deleted);
        videoPost.setFrom(videoAttachment.source);
        videoPost.setForwardCount(videoAttachment.forwardedPostsCount);
        videoPost.setText(videoAttachment.content);
        videoPost.setSummary(videoAttachment.summary);
        videoPost.setTime(videoAttachment.postCreatedTime);
        videoPost.setLike(videoAttachment.like);
        videoPost.setLikeCount(videoAttachment.likeCount);
        videoPost.setShareCount(videoAttachment.shareCount);
        videoPost.setFollowing(videoAttachment.following);
        videoPost.setNickName(videoAttachment.nickName);
        videoPost.setHeadUrl(videoAttachment.headUrl);

        return videoPost;
    }
}
