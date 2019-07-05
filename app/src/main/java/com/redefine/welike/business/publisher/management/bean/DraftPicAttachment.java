package com.redefine.welike.business.publisher.management.bean;

/**
 * Created by liubin on 2018/3/31.
 */

public class DraftPicAttachment extends DraftAttachmentBase {
    private static final long serialVersionUID = 1322120760053389236L;
    private int width;
    private int height;

    public DraftPicAttachment(String localFileName) {
        super(localFileName);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
