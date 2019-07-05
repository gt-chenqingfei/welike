package com.redefine.welike.business.feeds.management.bean;

import java.io.Serializable;

/**
 * Created by liubin on 2018/1/6.
 */

public class PicInfo implements Serializable {
    private static final long serialVersionUID = -477683182894509800L;
    private String thumbUrl;
    private String picUrl;
    private int height;
    private int width;

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

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
