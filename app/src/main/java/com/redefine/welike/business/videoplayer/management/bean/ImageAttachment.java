package com.redefine.welike.business.videoplayer.management.bean;

/**
 * Created by nianguowang on 2018/9/26
 */
public class ImageAttachment extends AttachmentBase {

    private String imageUrl;
    private int imageWidth;
    private int imageHeight;

    public ImageAttachment() {
        this.type = ATTACHMENT_TYPE_IMAGE;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
