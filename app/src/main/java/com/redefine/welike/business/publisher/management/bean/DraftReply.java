package com.redefine.welike.business.publisher.management.bean;

import com.redefine.richtext.RichContent;

/**
 * Created by liubin on 2018/3/15.
 */

public class DraftReply extends DraftBase {
    private static final long serialVersionUID = -3827853830590437435L;
    private boolean asRepost;
    private String pid;
    private String cid;
    private String commentNick;
    private String commentUid;
    private RichContent ccontent;

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

    public RichContent getCcontent() {
        return ccontent;
    }

    public void setCcontent(RichContent ccontent) {
        this.ccontent = ccontent;
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
