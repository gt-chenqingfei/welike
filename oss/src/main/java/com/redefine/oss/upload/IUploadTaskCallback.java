package com.redefine.oss.upload;

public interface IUploadTaskCallback {

    void onUploadTaskProgress(String objKey, float progress);
    void onUploadTaskCompleted(String objKey, String url);
    void onUploadTaskFailed(String objKey, int errCode);

}
