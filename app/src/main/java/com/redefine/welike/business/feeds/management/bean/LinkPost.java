package com.redefine.welike.business.feeds.management.bean;

/**
 * Created by liubin on 2018/1/6.
 */

public class LinkPost extends PostBase {

    private static final long serialVersionUID = -6513226663035579903L;
    private String linkUrl;
    private String linkTitle;
    private String linkText;
    private String linkThumbUrl;

    public LinkPost() {
        type = POST_TYPE_LINK;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkTitle() { return linkTitle; }

    public void setLinkTitle(String linkTitle) { this.linkTitle = linkTitle; }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getLinkThumbUrl() {
        return linkThumbUrl;
    }

    public void setLinkThumbUrl(String linkThumbUrl) {
        this.linkThumbUrl = linkThumbUrl;
    }
}
