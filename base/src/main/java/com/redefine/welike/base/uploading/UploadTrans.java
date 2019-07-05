package com.redefine.welike.base.uploading;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.analytics.HitBuilders;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.track.TrackerUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by liubin on 2018/3/19.
 */

public class UploadTrans implements BlockUploadRequest.BlockUploadRequestCallback, RequestCallback {
    private static final int CHUNK_SIZE = 1024 * 1024 * 5;
    private final String objectKey;
    private final String fileName;
    private final String sign;
    private int currentNum;
    private final int total;
    private final int fileSize;
    private final boolean resume;
    private CheckUploadFileRequest checkUploadFileRequest;
    private BlockUploadRequest uploadRequest;
    private UploadTransCallback callback;
    private Context context;
    private long time;

    interface UploadTransCallback {

        void onUploadTransProcess(UploadTrans trans, float process);
        void onUploadTransSuccessed(UploadTrans trans, String url);
        void onUploadTransFailed(UploadTrans trans, int errCode);

    }

    public UploadTrans(String fileName, String objectKey, String sign, boolean resume, Context context) {
        this.objectKey = objectKey;
        this.fileName = fileName;
        this.sign = sign;
        currentNum = 1;
        File file = new File(fileName);
        fileSize = (int)file.length();
        this.resume = resume;
        total = fileSize / CHUNK_SIZE + (fileSize % CHUNK_SIZE != 0 ? 1 : 0);
        this.context = context;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getSign() {
        return sign;
    }

    public void start(UploadTransCallback callback) {
        time = new Date().getTime();
        this.callback = callback;
        uploadRequest = new BlockUploadRequest(CHUNK_SIZE, context);
        uploadRequest.upload(fileName, objectKey, currentNum, total, this);
    }

    public void resume(UploadTransCallback callback) {
        time = new Date().getTime();
        this.callback = callback;
        checkUploadFileRequest = new CheckUploadFileRequest(objectKey, context);
        try {
            checkUploadFileRequest.req(this);
        } catch (Exception e) {
            checkUploadFileRequest = null;
            e.printStackTrace();
            if (callback != null) {
                callback.onUploadTransFailed(this, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void stop() {
        if (checkUploadFileRequest != null) {
            checkUploadFileRequest.cancel();
            checkUploadFileRequest = null;
        }
        if (uploadRequest != null) {
            uploadRequest.cancel();
            uploadRequest = null;
        }
        callback = null;
    }

    public boolean isResume() {
        return resume;
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (checkUploadFileRequest == request) {
            checkUploadFileRequest = null;
            if (callback != null) {
                callback.onUploadTransFailed(this, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (checkUploadFileRequest == request) {
            checkUploadFileRequest = null;
            int offset = result.getIntValue("size");
            if (offset <= CHUNK_SIZE) {
                currentNum = 1;
            } else {
                currentNum = offset / CHUNK_SIZE + 1;
            }
            uploadRequest = new BlockUploadRequest(CHUNK_SIZE, context);
            uploadRequest.upload(fileName, objectKey, currentNum, total, this);
        }
    }

    @Override
    public void onBlockUploadRequestSuccessed(BlockUploadRequest request, String url) {
        if (uploadRequest == request) {
            currentNum++;
            if (currentNum > total) {
                uploadRequest = null;
                netCostTrack();
                if (callback != null) {
                    callback.onUploadTransSuccessed(this, url);
                }
            } else {
                uploadRequest = new BlockUploadRequest(CHUNK_SIZE, context);
                uploadRequest.upload(fileName, objectKey, currentNum, total, this);
            }
        }
    }

    @Override
    public void onBlockUploadRequestProgress(BlockUploadRequest request, int offset) {
        if (uploadRequest == request) {
            int size = (currentNum - 1) * CHUNK_SIZE + offset;
            int process = (int)(((float)100 / (float)fileSize) * size);
            if (callback != null) {
                callback.onUploadTransProcess(this, process);
            }
        }
    }

    @Override
    public void onBlockUploadRequestFailed(BlockUploadRequest request, int errCode) {
        if (uploadRequest == request) {
            uploadRequest = null;
            if (callback != null) {
                callback.onUploadTransFailed(this, errCode);
            }
        }
    }

    private void netCostTrack() {
        if (time > 0) {
            int level;
            long dur = new Date().getTime() - time;
            if (dur > 0) {
                if (dur <= 200) {
                    level = 1;
                } else if (dur <= 500) {
                    level = 2;
                } else if (dur <= 1000) {
                    level = 3;
                } else if (dur <= 3000) {
                    level = 4;
                } else if (dur <= 5000) {
                    level = 5;
                } else if (dur <= 10000) {
                    level = 6;
                } else if (dur <= 20000) {
                    level = 7;
                } else if (dur <= 30000) {
                    level = 8;
                } else if (dur <= 50000) {
                    level = 9;
                } else {
                    level = 10;
                }
                String label = "upload" + "[" + NetWorkUtil.getNetworkTypeName(context) + "]";
//                TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder().setCategory(TrackerConstant.EVENT_TIMING)
//                        .setAction(TrackerConstant.EVENT_TIME_NETWORK)
//                        .setValue(level)
//                        .setLabel(label).build());
            }
        }
    }

}
