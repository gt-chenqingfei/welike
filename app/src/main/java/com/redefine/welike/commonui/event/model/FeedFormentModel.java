package com.redefine.welike.commonui.event.model;

import com.redefine.welike.commonui.event.commonenums.BooleanValue;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.commonenums.SendStatus;

import java.io.Serializable;

/**
 * Created by nianguowang on 2018/11/4
 */
public class FeedFormentModel implements Serializable {
    private PostType postType;
    private String source;
    private FeedButtonFrom buttonFrom;
    private String postId;
    private String poolCode;
    private String operationType;
    private String postLanguage;
    private String[] postTags;
    private String contentUid;
    private SendStatus sendStatus;
    private BooleanValue ifRepost;
    private String sequenceId;

    public FeedFormentModel(PostType postType, String source, FeedButtonFrom buttonFrom, String postId, String poolCode,
                            String operationType, String postLanguage, String[] postTags, String contentUid, String sequenceId) {
        this.postType = postType;
        this.source = source;
        this.buttonFrom = buttonFrom;
        this.postId = postId;
        this.poolCode = poolCode;
        this.postLanguage = postLanguage;
        this.postTags = postTags;
        this.operationType = operationType;
        this.contentUid = contentUid;
        this.sequenceId = sequenceId;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public void setIfRepost(BooleanValue ifRepost) {
        this.ifRepost = ifRepost;
    }

    public PostType getPostType() {
        return postType;
    }

    public String getSource() {
        return source;
    }

    public FeedButtonFrom getButtonFrom() {
        return buttonFrom;
    }

    public String getPostId() {
        return postId;
    }

    public String getPoolCode() {
        return poolCode;
    }

    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public BooleanValue getIfRepost() {
        return ifRepost;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getPostLanguage() {
        return postLanguage;
    }

    public String[] getPostTags() {
        return postTags;
    }

    public String getContentUid() {
        return contentUid;
    }

    public String getSequenceId() {
        return sequenceId;
    }
}
