package com.redefine.welike.business.message.management.bean;

import com.redefine.richtext.RichItem;

import java.util.List;

/**
 * Created by daining on 2018/4/23.
 */

public class KMMagic {
    public String content;
    public KMMagic post;
    public String id;
    public long created;
    public KMUser user;
    public boolean liked;
    public boolean deleted;
    public int repliesCount;
    public String source;
    public String summary;
    public int forwardedPostsCount;
    public int commentsCount;
    public KMMagic comment;
    public KMMagic forwardPost;
    public KMMagic reply;
    public List<RichItem> attachments;
}
