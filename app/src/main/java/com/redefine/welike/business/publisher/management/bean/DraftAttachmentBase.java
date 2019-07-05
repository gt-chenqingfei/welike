package com.redefine.welike.business.publisher.management.bean;

import com.redefine.foundation.utils.MD5Helper;

import java.io.Serializable;

/**
 * Created by liubin on 2018/3/14.
 */

public abstract class DraftAttachmentBase implements Serializable {
    private static final long serialVersionUID = 7342176481031924732L;
    private String attachmentDraftId;
    private String mimeType;
    private String localFileName;
    private String uploadLocalFileName;
    private String url;
    private String objectKey;

    protected DraftAttachmentBase(String localFileName) {
        this.localFileName = localFileName;
        try {
            attachmentDraftId = MD5Helper.md5(localFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAttachmentDraftId() {
        return attachmentDraftId;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public String getUploadLocalFileName() {
        return uploadLocalFileName;
    }

    public void setUploadLocalFileName(String uploadLocalFileName) {
        this.uploadLocalFileName = uploadLocalFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

}
