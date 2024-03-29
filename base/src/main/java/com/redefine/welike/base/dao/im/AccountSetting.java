package com.redefine.welike.base.dao.im;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ACCOUNT_SETTING".
 */
@Entity
public class AccountSetting {

    @Id
    @Unique
    private String uid;
    private String imMessageCursor;
    private Integer mentionMsgUnReadCount;
    private Integer commentMsgUnReadCount;
    private Integer likeMsgUnReadCount;

    @Generated
    public AccountSetting() {
    }

    public AccountSetting(String uid) {
        this.uid = uid;
    }

    @Generated
    public AccountSetting(String uid, String imMessageCursor, Integer mentionMsgUnReadCount, Integer commentMsgUnReadCount, Integer likeMsgUnReadCount) {
        this.uid = uid;
        this.imMessageCursor = imMessageCursor;
        this.mentionMsgUnReadCount = mentionMsgUnReadCount;
        this.commentMsgUnReadCount = commentMsgUnReadCount;
        this.likeMsgUnReadCount = likeMsgUnReadCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImMessageCursor() {
        return imMessageCursor;
    }

    public void setImMessageCursor(String imMessageCursor) {
        this.imMessageCursor = imMessageCursor;
    }

    public Integer getMentionMsgUnReadCount() {
        return mentionMsgUnReadCount;
    }

    public void setMentionMsgUnReadCount(Integer mentionMsgUnReadCount) {
        this.mentionMsgUnReadCount = mentionMsgUnReadCount;
    }

    public Integer getCommentMsgUnReadCount() {
        return commentMsgUnReadCount;
    }

    public void setCommentMsgUnReadCount(Integer commentMsgUnReadCount) {
        this.commentMsgUnReadCount = commentMsgUnReadCount;
    }

    public Integer getLikeMsgUnReadCount() {
        return likeMsgUnReadCount;
    }

    public void setLikeMsgUnReadCount(Integer likeMsgUnReadCount) {
        this.likeMsgUnReadCount = likeMsgUnReadCount;
    }

}
