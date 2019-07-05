package com.redefine.welike.business.message.management.bean;

import android.text.TextUtils;

/**
 * Created by liubin on 2018/1/25.
 */

public abstract class NotificationBase {
    public static final String GENERAL_FOLLOW_ACTION = "FOLLOW";

    public static final String MENTION_FORWARD_ACTION = "FORWARD";  // 转发评论
    public static final String MENTION_POST_ACTION = "POST_MENTION";// post里@我
    public static final String MENTION_COMMENT_ACTION = "COMMENT_MENTION";// 评论里@我
    public static final String MENTION_REPLY_ACTION = "REPLY_MENTION"; // 回复里@我

//    public static final String FORWARD_COMMENT = "FORWARD_COMMENT";  // 转发 评论

    public static final String COMMENT_COMMENT_ACTION = "COMMENT";  // 评论了我的post
    public static final String COMMENT_REPLY_ACTION = "REPLY";   // 回复了我的评论

    public static final String LIKE_POST_ACTION = "POST_LIKE";  // like我的post
    public static final String LIKE_COMMENT_ACTION = "COMMENT_LIKE";  // like我的评论
    public static final String LIKE_REPLY_ACTION = "REPLY_LIKE";  // like我的回复

    protected String nid;
    protected long time;
    protected String sourceUid;
    protected String sourceNickName;
    protected String sourceHead;
    protected String targetUid;
    protected String targetNickName;
    protected String action;
    protected String url;
    protected boolean like;
    private int vip = 0;
    public int exp = 0;

    public String getNid() { return nid; }

    public void setNid(String nid) { this.nid = nid; }

    public long getTime() { return time; }

    public void setTime(long time) { this.time = time; }

    public String getSourceUid() { return sourceUid; }

    public void setSourceUid(String sourceUid) { this.sourceUid = sourceUid; }

    public String getSourceNickName() { return sourceNickName; }

    public void setSourceNickName(String sourceNickName) { this.sourceNickName = sourceNickName; }

    public String getSourceHead() { return sourceHead; }

    public void setSourceHead(String sourceHead) { this.sourceHead = sourceHead; }

    public String getTargetUid() { return targetUid; }

    public void setTargetUid(String targetUid) { this.targetUid = targetUid; }

    public String getTargetNickName() { return targetNickName; }

    public void setTargetNickName(String targetNickName) { this.targetNickName = targetNickName; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public boolean isLike() { return like; }

    public void setLike(boolean like) { this.like = like; }

    public static boolean isCommentNotification(String action) {
        return TextUtils.equals(action, MENTION_COMMENT_ACTION)
                || TextUtils.equals(action, COMMENT_COMMENT_ACTION)
//                || TextUtils.equals(action, MENTION_FORWARD_ACTION)
                || TextUtils.equals(action, LIKE_COMMENT_ACTION);
    }

    public static boolean isReplyNotification(String action) {
        return TextUtils.equals(action, COMMENT_REPLY_ACTION)
                || TextUtils.equals(action, LIKE_REPLY_ACTION)
                || TextUtils.equals(action, MENTION_REPLY_ACTION);
    }

    public static boolean isPostNotification(String action) {
        return TextUtils.equals(action, MENTION_FORWARD_ACTION)
                || TextUtils.equals(action, MENTION_POST_ACTION)
                || TextUtils.equals(action, LIKE_POST_ACTION);
    }

    public static boolean isFollowAction(String action) {
        return TextUtils.equals(GENERAL_FOLLOW_ACTION, action);
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
