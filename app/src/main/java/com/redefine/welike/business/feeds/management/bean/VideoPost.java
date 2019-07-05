package com.redefine.welike.business.feeds.management.bean;

/**
 * Created by liubin on 2018/1/30.
 */

public class VideoPost extends PostBase {
    private static final long serialVersionUID = -5836312734905099046L;
    private String videoUrl;
    private String downloadUrl;
    private String coverUrl;
    private String videoSite;
    private int width;
    private int height;
    private int downloadProgress;

    public VideoPost() {
        type = POST_TYPE_VIDEO;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setVideoSite(String videoSite) {
        this.videoSite = videoSite;
    }

    public String getVideoSite() {
        return this.videoSite;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }
}
