package com.redefine.welike.business.message.management.bean;

import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;

/**
 * Created by liubin on 2018/1/28.
 */

public class ReplyNotification extends NotificationBase {
    private Comment relpy;
    private Comment comment;
    private PostBase post;

    public Comment getRelpy() { return relpy; }

    public void setRelpy(Comment relpy) { this.relpy = relpy; }

    public Comment getComment() { return comment; }

    public void setComment(Comment comment) { this.comment = comment; }

    public PostBase getPost() { return post; }

    public void setPost(PostBase post) { this.post = post; }
}
