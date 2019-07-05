package com.redefine.welike.business.publisher.management.bean;

import android.text.TextUtils;

import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.uploading.UploadingManager;

/**
 * Created by liubin on 2018/3/15.
 */

public class PostAttachmentTrans implements UploadingManager.UploadingCallback {
    public static final int POST_ATTACHMENT_STATE_IDLE = 0;
    public static final int POST_ATTACHMENT_STATE_UPLOADING = 1;
    public static final int POST_ATTACHMENT_STATE_DONE = 2;
    public static final int POST_ATTACHMENT_STATE_FAILED = 3;
    private String attachmentId;
    private int state;
    private String objectKey;
    private String uploadLocalFileName;
    private String url;
    private String type;
    private boolean hasUploading;
    private boolean hide;
    private PostAttachmentTransCallback callback;

    public interface PostAttachmentTransCallback {

        void onPostAttachmentProcess(String attachmentId, float process);
        void onPostAttachmentSave(String attachmentId);
        void onPostAttachmentCompleted(String attachmentId);
        void onPostAttachmentFailed(String attachmentId);

    }

    public PostAttachmentTrans(DraftAttachmentBase attachmentDraft, String type, boolean hide, PostAttachmentTransCallback callback) {
        state = POST_ATTACHMENT_STATE_IDLE;
        attachmentId = attachmentDraft.getAttachmentDraftId();
        this.type = type;
        this.hide = hide;
        this.objectKey = attachmentDraft.getObjectKey();
        this.uploadLocalFileName = attachmentDraft.getUploadLocalFileName();
        this.url = attachmentDraft.getUrl();
        this.callback = callback;
        hasUploading = false;
    }

    public int getState() {
        return state;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getUploadLocalFileName() {
        return uploadLocalFileName;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public boolean isHide() {
        return hide;
    }

    public void start() {
        if (state != POST_ATTACHMENT_STATE_IDLE) return;
        if (!TextUtils.isEmpty(url)) {
            state = POST_ATTACHMENT_STATE_DONE;
            if (callback != null) {
                callback.onPostAttachmentCompleted(attachmentId);
            }
        } else {
            state = POST_ATTACHMENT_STATE_UPLOADING;
            UploadingManager.getInstance().register(this);
            if (!TextUtils.isEmpty(objectKey)) {
                if (TextUtils.isEmpty(UploadingManager.getInstance().upload(objectKey, uploadLocalFileName, null, null))) {
                    hasUploading = true;
                } else {
                    if (callback != null) {
                        callback.onPostAttachmentFailed(attachmentId);
                    }
                }
            } else {
                String suffix = WeLikeFileManager.parseTmpFileSuffix(uploadLocalFileName);
                objectKey = UploadingManager.getInstance().upload(null, uploadLocalFileName, suffix, type);
                if (!TextUtils.isEmpty(objectKey)) {
                    hasUploading = false;
                } else {
                    if (callback != null) {
                        callback.onPostAttachmentFailed(attachmentId);
                    }
                }
            }
        }
    }

    public void close() {
        callback = null;
        UploadingManager.getInstance().unregister(this);
    }

    @Override
    public void onUploadingProcess(String objKey, float process) {
        if (TextUtils.equals(objKey, objectKey)) {
            if (!hasUploading) {
                if (process > 1) {
                    hasUploading = true;
                    if (callback != null) {
                        callback.onPostAttachmentSave(attachmentId);
                    }
                }
            }
            if (!hide) {
                if (callback != null) {
                    callback.onPostAttachmentProcess(attachmentId, process);
                }
            }
        }
    }

    @Override
    public void onUploadingCompleted(String objKey, String url) {
        if (TextUtils.equals(objKey, objectKey)) {
            UploadingManager.getInstance().unregister(this);
            this.url = url;
            state = POST_ATTACHMENT_STATE_DONE;
            if (callback != null) {
                callback.onPostAttachmentCompleted(attachmentId);
            }
        }
    }

    @Override
    public void onUploadingFailed(String objKey) {
        if (TextUtils.equals(objKey, objectKey)) {
            UploadingManager.getInstance().unregister(this);
            state = POST_ATTACHMENT_STATE_FAILED;
            if (callback != null) {
                callback.onPostAttachmentFailed(attachmentId);
            }
        }
    }

}
