package com.redefine.welike.commonui.event.model;

import com.redefine.welike.commonui.event.commonenums.BooleanValue;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;

/**
 * Created by nianguowang on 2018/12/3
 */
public class PublishEventModel {
    private static final int KB = 1024;
    public static String uploadType = "";
    private EventLog1.Publish.Source source;
    private EventLog1.Publish.MainSource mainSource;
    private EventLog1.Publish.PageType pageType;
    private EventLog1.Publish.ExistState existState;
    private int wordNum;
    private int pictureNum;
    private String pictureSize;
    private long pictureUploadTime;
    private int videoNum;
    private long videoSize;
    private long videoConvertTime;
    private long videoUploadTime;
    private int linkNum;
    private BooleanValue poolType;
    private int pollNum;
    private EventLog1.Publish.PollTime pollTime;
    private int emojiNum;
    private int atNum;
    private int topicNum;
    private EventLog1.Publish.TopicSource topicSource;
    private BooleanValue alsoRepost;
    private BooleanValue alsoComment;
    private String postId;
    private String repostId;
    private String topicId;
    private String community;
    private String postLa;
    private String contentUid;
    private String sequenceId;
    private int errorCode;
    private String errorMessage;
    private int retryTimes;
    private String draftId;
    private EventLog1.Publish.ReSendFrom reSendFrom;
    private ArrayList<String> postTags;
    private PublishEventProxy proxy;

    /**
     * @param source
     * @param mainSource
     * @param repostId   被评论或者被转发的post的id，如果是新发布post，该值传null即可。
     */
    public PublishEventModel(EventLog1.Publish.Source source, EventLog1.Publish.MainSource mainSource, String repostId) {
        this.source = source;
        this.mainSource = mainSource;
        this.repostId = repostId;
        proxy = new PublishEventProxy(this);
    }

    public PublishEventModel() {
        proxy = new PublishEventProxy(this);
    }

    public PublishEventModel(EventLog1.Publish.Source source, EventLog1.Publish.MainSource mainSource) {
        this.source = source;
        this.mainSource = mainSource;
        proxy = new PublishEventProxy(this);
    }


    public EventLog1.Publish.Source getSource() {
        return source;
    }

    public PublishEventModel setSource(EventLog1.Publish.Source source) {
        this.source = source;
        return this;
    }

    public EventLog1.Publish.MainSource getMainSource() {
        return mainSource;
    }

    public PublishEventModel setMainSource(EventLog1.Publish.MainSource mainSource) {
        this.mainSource = mainSource;
        return this;
    }

    public EventLog1.Publish.PageType getPageType() {
        return pageType;
    }

    public PublishEventModel setPageType(EventLog1.Publish.PageType pageType) {
        this.pageType = pageType;
        return this;
    }

    public EventLog1.Publish.ExistState getExistState() {
        return existState;
    }

    public PublishEventModel setExistState(EventLog1.Publish.ExistState existState) {
        this.existState = existState;
        return this;
    }

    public int getWordNum() {
        return wordNum;
    }

    public PublishEventModel setWordNum(int wordNum) {
        this.wordNum = wordNum;
        return this;
    }

    public int getPictureNum() {
        return pictureNum;
    }

    public PublishEventModel setPictureNum(int pictureNum) {
        this.pictureNum = pictureNum;
        return this;
    }

    public String getPictureSize() {
        return pictureSize;
    }

    public PublishEventModel setPictureSize(String pictureSize) {
        this.pictureSize = pictureSize;
        return this;
    }

    public long getPictureUploadTime() {
        return pictureUploadTime;
    }

    public PublishEventModel setPictureUploadTime(long pictureUploadTime) {
        this.pictureUploadTime = pictureUploadTime;
        return this;
    }

    public int getVideoNum() {
        return videoNum;
    }

    public PublishEventModel setVideoNum(int videoNum) {
        this.videoNum = videoNum;
        return this;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public PublishEventModel setVideoSize(long videoSize) {
        this.videoSize = videoSize;
        return this;
    }

    public long getVideoConvertTime() {
        return videoConvertTime;
    }

    public PublishEventModel setVideoConvertTime(long videoConvertTime) {
        this.videoConvertTime = videoConvertTime;
        return this;
    }

    public long getVideoUploadTime() {
        return videoUploadTime;
    }

    public PublishEventModel setVideoUploadTime(long videoUploadTime) {
        this.videoUploadTime = videoUploadTime;
        return this;
    }

    public int getLinkNum() {
        return linkNum;
    }

    public PublishEventModel setLinkNum(int linkNum) {
        this.linkNum = linkNum;
        return this;
    }

    public BooleanValue getPoolType() {
        return poolType;
    }

    public PublishEventModel setPoolType(BooleanValue poolType) {
        this.poolType = poolType;
        return this;
    }

    public int getPollNum() {
        return pollNum;
    }

    public PublishEventModel setPollNum(int pollNum) {
        this.pollNum = pollNum;
        return this;
    }

    public EventLog1.Publish.PollTime getPollTime() {
        return pollTime;
    }

    public PublishEventModel setPollTime(EventLog1.Publish.PollTime pollTime) {
        this.pollTime = pollTime;
        return this;
    }

    public int getEmojiNum() {
        return emojiNum;
    }

    public PublishEventModel addEmojiNum() {
        this.emojiNum++;
        return this;
    }

    public PublishEventModel removeEmojiNum() {
        emojiNum--;
        if (emojiNum < 0) {
            emojiNum = 0;
        }

        return this;
    }

    public int getAtNum() {
        return atNum;
    }

    public PublishEventModel setAtNum(int atNum) {
        this.atNum = atNum;
        return this;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public PublishEventModel setTopicNum(int topicNum) {
        this.topicNum = topicNum;
        return this;
    }

    public EventLog1.Publish.TopicSource getTopicSource() {
        return topicSource;
    }

    public PublishEventModel setTopicSource(EventLog1.Publish.TopicSource topicSource) {
        this.topicSource = topicSource;
        return this;
    }

    public BooleanValue getAlsoRepost() {
        return alsoRepost;
    }

    public PublishEventModel setAlsoRepost(BooleanValue alsoRepost) {
        this.alsoRepost = alsoRepost;
        return this;
    }

    public BooleanValue getAlsoComment() {
        return alsoComment;
    }

    public PublishEventModel setAlsoComment(BooleanValue alsoComment) {
        this.alsoComment = alsoComment;
        return this;
    }

    public String getPostId() {
        return postId;
    }

    public PublishEventModel setPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getRepostId() {
        return repostId;
    }

    public PublishEventModel setRepostId(String repostId) {
        this.repostId = repostId;
        return this;
    }

    public String getTopicId() {
        return topicId;
    }

    public PublishEventModel setTopicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public String getCommunity() {
        return community;
    }

    public PublishEventModel setCommunity(String community) {
        this.community = community;
        return this;
    }

    public String getPostLa() {
        return postLa;
    }

    public ArrayList<String> getPostTags() {
        return postTags;
    }

    public PublishEventModel setPostTags(ArrayList<String> postTags) {
        this.postTags = postTags;
        return this;
    }

    public PublishEventModel setPostLa(String postLa) {
        this.postLa = postLa;
        return this;
    }


    public String getContentUid() {
        return contentUid;
    }

    public PublishEventModel setContentUid(String contentUid) {
        this.contentUid = contentUid;
        return this;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public PublishEventModel setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public EventLog1.Publish.ReSendFrom getReSendFrom() {
        return reSendFrom;
    }

    public void setReSendFrom(EventLog1.Publish.ReSendFrom reSendFrom) {
        this.reSendFrom = reSendFrom;
    }

    public PublishEventProxy getProxy() {
        return proxy;
    }


}
