package com.redefine.welike.business.feeds.management.bean;

import com.redefine.richtext.RichItem;
import com.redefine.welike.business.user.management.bean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArticleInfo implements Serializable {
    private static final long serialVersionUID = 1766168464276191285L;
    private String content;
    private String title;
    private String cover;
    private User user;
    private long created;
    private VideoInfo videoInfo;



    private List<RichItem> richItemList=new ArrayList<>();


    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<RichItem> getRichItemList() {
        return richItemList;
    }

    public void setRichItemList(List<RichItem> richItemList) {
        this.richItemList = richItemList;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}

