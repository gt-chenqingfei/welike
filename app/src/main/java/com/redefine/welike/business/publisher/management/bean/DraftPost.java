package com.redefine.welike.business.publisher.management.bean;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.publisher.management.draft.ArticleDraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/3/13.
 */

public class DraftPost extends DraftPostBase {
    private static final long serialVersionUID = -7302821458908089864L;
    private List<DraftPicAttachment> picDraftList;
    private DraftVideoAttachment video;
    private DraftPicAttachment videoThumb;

    private long expiredTime = GlobalConfig.DEFAULT_POLL_EXPIRED_TIME;
    private List<PollItemInfo> mPollItemInfos = new ArrayList<>();
    private ArticleDraft mArticle;

    public List<DraftPicAttachment> getPicDraftList() {
        return picDraftList;
    }

    public void setPicDraftList(List<DraftPicAttachment> picDraftList) {
        this.picDraftList = picDraftList;
    }

    public DraftVideoAttachment getVideo() {
        return video;
    }

    public void setVideo(DraftVideoAttachment video) {
        this.video = video;
    }

    public DraftPicAttachment getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(DraftPicAttachment videoThumb) {
        this.videoThumb = videoThumb;
    }

    public List<PollItemInfo> getPollItemInfos() {
        return mPollItemInfos;
    }

    public void setPollItemInfos(List<PollItemInfo> pollItemInfos) {

        if (!CollectionUtil.isEmpty(pollItemInfos)) {
            this.mPollItemInfos = pollItemInfos;
        }
    }

    public void addPollItemInfos(List<PollItemInfo> pollItemInfos) {
        this.mPollItemInfos.addAll(pollItemInfos);
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public void setArticle(ArticleDraft articleDraft) {
        mArticle = articleDraft;
    }

    public ArticleDraft getArticle() {
        return mArticle;
    }


}
