package com.redefine.im.bean;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMVideoMessage extends IMMessageBase {
    private String thumb;
    private String video;
    private String localFileName;
    private String localThumbFileName;

    public IMVideoMessage() { type = MESSAGE_TYPE_VIDEO; }

    public String getThumb() { return thumb; }

    public void setThumb(String thumb) { this.thumb = thumb; }

    public String getVideo() { return video; }

    public void setVideo(String video) { this.video = video; }

    public String getLocalFileName() { return localFileName; }

    public void setLocalFileName(String localFileName) { this.localFileName = localFileName; }

    public String getLocalThumbFileName() { return localThumbFileName; }

    public void setLocalThumbFileName(String localThumbFileName) { this.localThumbFileName = localThumbFileName; }
}
