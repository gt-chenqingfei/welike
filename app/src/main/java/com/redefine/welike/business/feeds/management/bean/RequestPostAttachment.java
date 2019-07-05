package com.redefine.welike.business.feeds.management.bean;

/**
 * Created by liubin on 2018/1/17.
 */

public class RequestPostAttachment {
    private String attId;
    private String type;
    private String url;
    private String targetAttId;
    private int width;
    private int height;

    public String getAttId() { return attId; }

    public void setAttId(String attId) { this.attId = attId; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTargetAttId() { return targetAttId; }

    public void setTargetAttId(String targetAttId) { this.targetAttId = targetAttId; }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
