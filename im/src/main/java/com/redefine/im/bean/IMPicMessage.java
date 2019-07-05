package com.redefine.im.bean;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMPicMessage extends IMMessageBase {
    private String picUrl;
    private String picLargeUrl;
    private String localFileName;

    public IMPicMessage() { type = MESSAGE_TYPE_PIC; }

    public String getPicUrl() { return picUrl; }

    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }

    public String getPicLargeUrl() { return picLargeUrl; }

    public void setPicLargeUrl(String picLargeUrl) { this.picLargeUrl = picLargeUrl; }

    public String getLocalFileName() { return localFileName; }

    public void setLocalFileName(String localFileName) { this.localFileName = localFileName; }
}
