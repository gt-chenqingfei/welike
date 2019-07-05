package com.redefine.welike.business.publisher.management.bean;

import com.redefine.richtext.RichContent;

/**
 * Created by liubin on 2018/3/15.
 */

public class DraftReplyBack extends DraftBase {
    private static final long serialVersionUID = -2954843284109810977L;
    private boolean asRepost;
    private String pid;
    private String cid;
    private String replyId;
    private String commentNick;
    private String commentUid;
    private RichContent rcontent;

    public boolean isAsRepost() {
        return asRepost;
    }

    public void setAsRepost(boolean asRepost) {
        this.asRepost = asRepost;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public RichContent getRcontent() {
        return rcontent;
    }

    public void setRcontent(RichContent rcontent) {
        this.rcontent = rcontent;
    }

    public String getCommentNick() {
        return commentNick;
    }

    public void setCommentNick(String commentNick) {
        this.commentNick = commentNick;
    }

    public String getCommentUid() {
        return commentUid;
    }

    public void setCommentUid(String commentUid) {
        this.commentUid = commentUid;
    }
}
