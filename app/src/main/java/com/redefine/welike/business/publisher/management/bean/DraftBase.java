package com.redefine.welike.business.publisher.management.bean;

import com.redefine.richtext.RichContent;

import java.io.Serializable;

/**
 * Created by liubin on 2018/3/13.
 */

public class DraftBase implements Serializable {
    public static final int STATE_NONE = 0;
    public static final int STATE_UPLOADING = 1;
    public static final int STATE_UPLOAD_FAILED = 2;
    public static final int STATE_UPLOAD_SUCCESS = 3;
    private static final long serialVersionUID = 7764027944869992497L;
    private String draftId;
    protected long time;
    protected int type;
    protected boolean show;
    private boolean isSaveDB = true;
    protected RichContent content;
    protected String superTopicId;
    private int state;
    private String contentUid;
    private String sequenceId;
    private String from;

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public RichContent getContent() {
        return content;
    }

    public void setContent(RichContent content) {
        this.content = content;
    }

    public void setIsSaveDB(boolean isSaveDB) {
        this.isSaveDB = isSaveDB;
    }

    public boolean isSaveDB() {
        return isSaveDB;
    }

    public void setSuperTopicId(String hashID) {
        superTopicId = hashID;
    }

    public String getSuperTopicId() {
        return superTopicId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContentUid() {
        return contentUid;
    }

    public void setContentUid(String contentUid) {
        this.contentUid = contentUid;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
