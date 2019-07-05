package com.redefine.welike.business.publisher.management.bean;

import com.redefine.welike.business.feeds.management.bean.PostBase;

/**
 * Created by liubin on 2018/3/13.
 */

public class DraftForwardPost extends DraftPostBase {
    private static final long serialVersionUID = 5061614809469269794L;
    private PostBase rootPost;
    private boolean forwardDeleted;
    private boolean asComment;
    private String commentPid;

    public PostBase getRootPost() {
        return rootPost;
    }

    public void setRootPost(PostBase rootPost) {
        this.rootPost = rootPost;
    }

    public boolean isForwardDeleted() {
        return forwardDeleted;
    }

    public void setForwardDeleted(boolean forwardDeleted) {
        this.forwardDeleted = forwardDeleted;
    }

    public boolean isAsComment() {
        return asComment;
    }

    public void setAsComment(boolean asComment) {
        this.asComment = asComment;
    }

    public String getCommentPid() {
        return commentPid;
    }

    public void setCommentPid(String commentPid) {
        this.commentPid = commentPid;
    }
}
