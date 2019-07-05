package com.redefine.welike.business.feeds.management.bean;

/**
 * Created by liubin on 2018/1/6.
 */

public class ForwardPost extends PostBase {
    private static final long serialVersionUID = -1413517822939312174L;
    private PostBase rootPost;
    private boolean forwardDeleted;

    public ForwardPost() {
        type = POST_TYPE_FORWARD;
        forwardDeleted = false;
    }

    public PostBase getRootPost() {
        return rootPost;
    }

    public void setRootPost(PostBase rootPost) {
        if (rootPost != null) {
            if (rootPost.type != POST_TYPE_FORWARD) {
                this.rootPost = rootPost;
            }
        }
    }

    public boolean isForwardDeleted() {
        return forwardDeleted;
    }

    public void setForwardDeleted(boolean forwardDeleted) {
        this.forwardDeleted = forwardDeleted;
    }

}
