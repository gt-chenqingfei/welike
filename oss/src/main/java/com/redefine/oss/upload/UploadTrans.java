package com.redefine.oss.upload;

import java.io.File;

import okhttp3.OkHttpClient;

public class UploadTrans implements BlockUploadRequest.BlockUploadRequestCallback {
    private static final int CHUNK_SIZE = 1024 * 1024 * 5;
    private final String sign;
    private final String objectKey;
    private final String fileName;
    private int currentNum;
    private final int total;
    private final int fileSize;
    private int lastBlockSize;
    private final OkHttpClient client;
    private final String host;
    private final String accessToken;
    private BlockUploadRequest uploadRequest;
    private UploadTransCallback callback;

    interface UploadTransCallback {

        void onUploadTransProgress(UploadTrans trans, float progress);
        void onUploadTransSuccessed(UploadTrans trans, String url);
        void onUploadTransFailed(UploadTrans trans, int errCode);

    }

    UploadTrans(String sign, String fileName, String objectKey, String host, String accessToken, OkHttpClient client) {
        this.sign = sign;
        this.host = host;
        this.accessToken = accessToken;
        this.client = client;
        this.objectKey = objectKey;
        this.fileName = fileName;
        currentNum = 1;
        lastBlockSize = 0;
        File file = new File(fileName);
        fileSize = (int)file.length();
        if (fileSize < CHUNK_SIZE) {
            total = 1;
        } else {
            int c1 = fileSize / CHUNK_SIZE;
            lastBlockSize = fileSize % CHUNK_SIZE;
            if (lastBlockSize != 0) {
                c1 += 1;
            }
            total = c1;
        }
    }

    String getSign() {
        return sign;
    }

    String getObjectKey() {
        return objectKey;
    }

    void start(UploadTransCallback callback) {
        this.callback = callback;
        uploadRequest = new BlockUploadRequest(CHUNK_SIZE, host);
        uploadRequest.upload(client, accessToken, fileName, objectKey, currentNum, total, this);
    }

    void stop() {
        if (uploadRequest != null) {
            uploadRequest.cancel();
            uploadRequest = null;
        }
        callback = null;
    }

    @Override
    public void onBlockUploadRequestSuccessed(BlockUploadRequest request, String url) {
        if (uploadRequest == request) {
            currentNum++;
        }
        if (currentNum > total) {
            uploadRequest = null;
            if (callback != null) {
                callback.onUploadTransSuccessed(this, url);
            }
            callback = null;
        } else {
            uploadRequest = new BlockUploadRequest(CHUNK_SIZE, host);
            uploadRequest.upload(client, accessToken, fileName, objectKey, currentNum, total, this);
        }
    }

    @Override
    public void onBlockUploadRequestProgress(BlockUploadRequest request, float progress) {
        float rate;
        if (fileSize < CHUNK_SIZE) {
            rate = progress;
        } else {
            int partSize;
            if (currentNum < total) {
                partSize = (int)((float)CHUNK_SIZE * (progress / 100f));
            } else {
                partSize = (int)((float)lastBlockSize * (progress / 100f));
            }
            int size = (currentNum - 1) * CHUNK_SIZE + partSize;
            rate = ((float)size / (float)fileSize) * 100f;
        }
        if (callback != null) {
            callback.onUploadTransProgress(this, rate);
        }
    }

    @Override
    public void onBlockUploadRequestFailed(BlockUploadRequest request, int errCode) {
        if (uploadRequest == request) {
            uploadRequest = null;
            if (callback != null) {
                callback.onUploadTransFailed(this, errCode);
            }
            callback = null;
        }
    }

}
