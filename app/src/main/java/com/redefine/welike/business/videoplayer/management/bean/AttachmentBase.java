package com.redefine.welike.business.videoplayer.management.bean;

import java.io.Serializable;

/**
 * Created by nianguowang on 2018/9/26
 */
public class AttachmentBase implements Serializable {

    public static final String ATTACHMENT_TYPE_VIDEO = "VIDEO";
    public static final String ATTACHMENT_TYPE_IMAGE = "IMAGE";

    protected String aid;
    protected String pid;
    protected String uid;
    protected long attachmentCreatedTime;

    protected String content;
    protected String source;
    protected long postCreatedTime;
    protected boolean deleted;
    protected String summary;
    protected int forwardedPostsCount;
    protected int commentsCount;
    protected String[] tags;
    protected String language;
    protected int origin;
    protected long lastUpdateTime;
    protected String community;

    protected boolean like;
    protected String headUrl;
    protected String nickName;
    protected boolean following;
    protected long likeCount;
    protected long shareCount;

    protected String type;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getAttachmentCreatedTime() {
        return attachmentCreatedTime;
    }

    public void setAttachmentCreatedTime(long attachmentCreatedTime) {
        this.attachmentCreatedTime = attachmentCreatedTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getPostCreatedTime() {
        return postCreatedTime;
    }

    public void setPostCreatedTime(long postCreatedTime) {
        this.postCreatedTime = postCreatedTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getForwardedPostsCount() {
        return forwardedPostsCount;
    }

    public void setForwardedPostsCount(int forwardedPostsCount) {
        this.forwardedPostsCount = forwardedPostsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getShareCount() {
        return shareCount;
    }

    public void setShareCount(long shareCount) {
        this.shareCount = shareCount;
    }
}
