package com.redefine.welike.business.publisher.management.bean;

import com.redefine.richtext.RichContent;

/**
 * Created by liubin on 2018/3/13.
 */

public class DraftComment extends DraftBase {
    private static final long serialVersionUID = -6621350725870592911L;
    private boolean asRepost;
    private String pid;
    private String commentNick;
    private String commentUid;
    private RichContent fcontent;

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

    public RichContent getFcontent() {
        return fcontent;
    }

    public void setFcontent(RichContent fcontent) {
        this.fcontent = fcontent;
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
