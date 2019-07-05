package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/9/12
 */
public enum NewShareEventManager {
    INSTANCE;

    private int shareChannel;
    private int contentType;
    private int postType;
    private int videoPostType;
    private int shareFrom;
    private int popFrom;
    private int shareResult;

    public void setShareChannel(int shareChannel) {
        this.shareChannel = shareChannel;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public void setVideoPostType(int videoPostType) {
        this.videoPostType = videoPostType;
    }

    public void setShareFrom(int shareFrom) {
        this.shareFrom = shareFrom;
    }

    public void setPopFrom(int popFrom) {
        this.popFrom = popFrom;
    }

    public void reset() {
        shareChannel = 0;
        contentType = 0;
        postType = 0;
        videoPostType = 0;
        shareFrom = 0;
        popFrom = 0;
        shareResult = 0;
    }

    public void onShareResult(int shareResult) {
        EventLog.NewShare.report1(shareChannel, contentType, postType, videoPostType, shareFrom, popFrom, shareResult);
        reset();
    }
}
