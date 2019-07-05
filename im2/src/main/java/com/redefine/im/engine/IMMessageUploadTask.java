package com.redefine.im.engine;

import android.text.TextUtils;

import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.uploading.UploadingManager;

import java.io.File;

/**
 * Created by liubin on 2018/2/10.
 */

class IMMessageUploadTask implements UploadingManager.UploadingCallback {
    private String mainObjKey;
    private String mainObjUrl;
    private String mainFileName;
    private String subObjKey;
    private String subObjUrl;
    private String subFileName;
    private String messageId;
    private MessageUploadingTaskListener listener;

    public interface MessageUploadingTaskListener {

        void onMessageUploadingTaskProcess(String mid, float process);
        void onMessageUploadingTaskEnd(String mid, boolean successed);

    }

    public void setListener(MessageUploadingTaskListener listener) {
        this.listener = listener;
    }

    String getMainObjectUrl() {
        return mainObjUrl;
    }

    String getSubObjectUrl() {
        return subObjUrl;
    }

    boolean upload(String mid, String mainFileName, String subFileName, String objectType) {
        if (TextUtils.isEmpty(objectType)) {
            return false;
        }

        if (TextUtils.isEmpty(mainFileName)) {
            return false;
        }
        File mainFile = new File(mainFileName);
        if (!mainFile.exists() || !mainFile.isFile()) {
            return false;
        }

        if (!TextUtils.isEmpty(subFileName)) {
            File subFile = new File(subFileName);
            if (!subFile.exists() || !subFile.isFile()) {
                return false;
            }
        }

        messageId = mid;
        this.mainFileName = mainFileName;
        String mainExt = WeLikeFileManager.parseTmpFileSuffix(mainFileName);
        mainObjKey = UploadingManager.getInstance().upload(null, mainFileName, mainExt, objectType);
        if (!TextUtils.isEmpty(subFileName)) {
            this.subFileName = subFileName;
            String subExt = WeLikeFileManager.parseTmpFileSuffix(subFileName);
            subObjKey = UploadingManager.getInstance().upload(null, subFileName, subExt, UploadingManager.UPLOAD_TYPE_IMG);
        }
        return true;
    }

    @Override
    public void onUploadingProcess(String objKey, float process) {
        if (TextUtils.equals(mainObjKey, objKey)) {
            if (listener != null) {
                listener.onMessageUploadingTaskProcess(messageId, process);
            }
        }
    }

    @Override
    public void onUploadingCompleted(String objKey, String url) {
        if (TextUtils.equals(mainObjKey, objKey)) {
            mainObjUrl = url;
        }
        if (!TextUtils.isEmpty(subObjKey)) {
            if (TextUtils.equals(subObjKey, objKey)) {
                subObjUrl = url;
            }
        }

        boolean mainCompleted = false;
        boolean subCompleted = false;
        if (!TextUtils.isEmpty(mainObjUrl)) {
            mainCompleted = true;
        }
        if (!TextUtils.isEmpty(subObjKey)) {
            if (!TextUtils.isEmpty(subObjUrl)) {
                subCompleted = true;
            }
        } else {
            subCompleted = true;
        }
        if (mainCompleted && subCompleted) {
            if (listener != null) {
                listener.onMessageUploadingTaskEnd(messageId, true);
            }
        }
    }

    @Override
    public void onUploadingFailed(String objKey) {
        boolean failed = false;
        if (TextUtils.equals(mainObjKey, objKey)) {
            failed = true;
            UploadingManager.getInstance().remove("","");
            UploadingManager.getInstance().remove(mainObjKey, mainFileName);
            if (!TextUtils.isEmpty(subObjKey)) {
                UploadingManager.getInstance().remove(objKey, subFileName);
            }
        } else if (!TextUtils.isEmpty(subObjKey)) {
            if (TextUtils.equals(subObjKey, objKey)) {
                UploadingManager.getInstance().remove(mainObjKey, mainFileName);
                UploadingManager.getInstance().remove(objKey, subFileName);
                failed = true;
            }
        }
        if (failed) {
            if (listener != null) {
                listener.onMessageUploadingTaskEnd(messageId, false);
            }
        }
    }

}
