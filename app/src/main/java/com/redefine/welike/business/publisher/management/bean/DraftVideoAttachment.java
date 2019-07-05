package com.redefine.welike.business.publisher.management.bean;

/**
 * Created by liubin on 2018/3/31.
 */

public class DraftVideoAttachment extends DraftAttachmentBase {
    private static final long serialVersionUID = -580222853404018143L;
    private long duration;
    private int width;
    private int height;
    private boolean isFromRecorder;

    public DraftVideoAttachment(String localFileName) {
        super(localFileName);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public boolean isFromRecorder() {
        return isFromRecorder;
    }

    public void setFromRecorder(boolean fromRecorder) {
        isFromRecorder = fromRecorder;
    }
}
