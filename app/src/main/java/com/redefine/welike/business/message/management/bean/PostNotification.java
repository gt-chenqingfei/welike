package com.redefine.welike.business.message.management.bean;

import com.redefine.welike.business.feeds.management.bean.PostBase;

/**
 * Created by liubin on 2018/1/25.
 */

public class PostNotification extends NotificationBase {
    private PostBase post;

    public PostBase getPost() { return post; }

    public void setPost(PostBase post) { this.post = post; }
}
