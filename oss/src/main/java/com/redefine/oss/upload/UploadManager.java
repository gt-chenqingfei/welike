package com.redefine.oss.upload;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.foundation.framework.WeakPool;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.MD5Helper;
import com.redefine.oss.adapter.GatewayUploadConfig;
import com.redefine.oss.adapter.IUploadConfig;
import com.redefine.oss.adapter.IUploadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class UploadManager implements IUploadManager {
    public static final String UPLOAD_TYPE_IMG = "img";
    public static final String UPLOAD_TYPE_VIDEO = "video";
    private static final int UPLOAD_TASKS_NUM = 3;
    private final List<UploadTask> waitingTasks = new ArrayList<>();
    private final Map<String, UploadTask> runningTasks = new ConcurrentHashMap<>();
    private final WeakPool<IUploadTaskCallback> listeners = new WeakPool<>();
    private OkHttpClient okHttpClient;
    private final UploadTrans.UploadTransCallback uploadTransCallback = new UploadTrans.UploadTransCallback() {

        @Override
        public void onUploadTransProgress(UploadTrans trans, float progress) {
            String sign = trans.getSign();
            UploadTask task = runningTasks.get(sign);
            if (task != null) {
                broadcastProgress(task.getObjectKey(), progress, task.callback);
            }
        }

        @Override
        public void onUploadTransSuccessed(UploadTrans trans, String url) {
            synchronized (runningTasks) {
                String sign = trans.getSign();
                UploadTask task = runningTasks.remove(sign);
                if (task != null) {
                    broadcastCompleted(task.getObjectKey(), url, task.callback);
                }
                next();
            }
        }

        @Override
        public void onUploadTransFailed(UploadTrans trans, int errCode) {
            synchronized (runningTasks) {
                String sign = trans.getSign();
                UploadTask task = runningTasks.remove(sign);
                if (task != null) {
                    broadcastFailed(task.getObjectKey(), errCode, task.callback);
                }
                next();
            }
        }

    };
    private String host;
    private String accessToken;

    private static class UploadTask extends UploadTrans {
        private final IUploadTaskCallback callback;

        UploadTask(String sign, String fileName, String objectKey, String host, String accessToken, OkHttpClient client, final IUploadTaskCallback callback) {
            super(sign, fileName, objectKey, host, accessToken, client);
            this.callback = callback;
        }
    }

    public void setEnv(@NonNull IUploadConfig uploadConfig) {
        GatewayUploadConfig config = (GatewayUploadConfig) uploadConfig;
        if (!hasSetEnv()) {
            resetParams(config.getHost(), config.getOkHttpClient());
        }
        this.accessToken = config.getAccessToken();
    }

    private void register(IUploadTaskCallback callback) {
        synchronized (listeners) {
            listeners.add(callback);
        }
    }

    private void unregister(IUploadTaskCallback callback) {
        synchronized (listeners) {
            listeners.remove(callback);
        }
    }

    private boolean hasSetEnv() {
        return okHttpClient != null;
    }

    private String upload(String objFileName, String extName, String objectType) {
        return upload(objFileName, extName, objectType, null);
    }

    public String upload(String objFileName, String extName, String objectType, IUploadTaskCallback callback) {
        if (TextUtils.isEmpty(host)) {
            broadcastFailed(null, ErrorCode.ERROR_UPLOAD_OBJECT_INVALID, callback);
            return null;
        }
        if (TextUtils.isEmpty(objFileName)) {
            broadcastFailed(null, ErrorCode.ERROR_UPLOAD_OBJECT_INVALID, callback);
            return null;
        }
        File file = new File(objFileName);
        if (!file.exists()) {
            broadcastFailed(null, ErrorCode.ERROR_UPLOAD_OBJECT_INVALID, callback);
            return null;
        }

        String objectKey = buildObjectKey(CommonHelper.generateUUID(), extName, objectType);
        String sign = sign(objFileName, objectKey);

        UploadTask task = new UploadTask(sign, objFileName, objectKey, host, accessToken, okHttpClient, callback);
        if (runningTasks.size() < UPLOAD_TASKS_NUM) {
            startTask(task);
        } else {
            waitingTasks.add(task);
        }
        return objectKey;
    }

    private void stop(String objectKey, String objFileName) {
        String sign = sign(objFileName, objectKey);
        removeWaitingTasks(sign);
        UploadTask task = runningTasks.remove(sign);
        if (task != null) {
            task.stop();
        }
    }

    public void stopAll() {
        synchronized (waitingTasks) {
            waitingTasks.clear();
        }
        synchronized (runningTasks) {
            for (UploadTask task : runningTasks.values()) {
                task.stop();
            }
            runningTasks.clear();
        }
    }

    private void resetParams(String host, OkHttpClient okHttpClient) {
        this.host = host;
        this.okHttpClient = okHttpClient;
    }

    private void startTask(UploadTask task) {
        synchronized (runningTasks) {
            removeWaitingTasks(task.getSign());
            runningTasks.put(task.getSign(), task);
            task.start(uploadTransCallback);
        }
    }

    private void next() {
        if (runningTasks.size() < UPLOAD_TASKS_NUM) {
            UploadTask task = null;
            synchronized (waitingTasks) {
                if (waitingTasks.size() > 0) {
                    task = waitingTasks.remove(0);
                }
            }
            if (task != null) {
                startTask(task);
            }
        }
    }

    private UploadTask removeWaitingTasks(String sign) {
        synchronized (waitingTasks) {
            int idx = -1;
            for (int i = 0; i < waitingTasks.size(); i++) {
                UploadTask t = waitingTasks.get(i);
                if (TextUtils.equals(t.getSign(), sign)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                return waitingTasks.remove(idx);
            }
        }
        return null;
    }

    private void broadcastProgress(String objKey, float progress, IUploadTaskCallback singleCallback) {
        final float p = (progress > 100) ? 100 : progress;
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                IUploadTaskCallback callback = listeners.get(i);
                if (callback != null) {
                    callback.onUploadTaskProgress(objKey, p);
                }
            }
        }
        if (singleCallback != null) {
            singleCallback.onUploadTaskProgress(objKey, p);
        }
    }

    private void broadcastCompleted(String objKey, String url, IUploadTaskCallback singleCallback) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                IUploadTaskCallback callback = listeners.get(i);
                if (callback != null) {
                    callback.onUploadTaskCompleted(objKey, url);
                }
            }
        }
        if (singleCallback != null) {
            singleCallback.onUploadTaskCompleted(objKey, url);
        }
    }

    private void broadcastFailed(final String objKey, final int errCode, final IUploadTaskCallback singleCallback) {
        Schedulers.newThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listeners) {
                    for (int i = 0; i < listeners.size(); i++) {
                        IUploadTaskCallback callback = listeners.get(i);
                        if (callback != null) {
                            callback.onUploadTaskFailed(objKey, errCode);
                        }
                    }
                }
                if (singleCallback != null) {
                    singleCallback.onUploadTaskFailed(objKey, errCode);
                }
            }

        });
    }

    private static String buildObjectKey(String subObjectKey, String extName, String objectType) {
        String objKey;
        if (!TextUtils.isEmpty(extName)) {
            objKey = String.format("%s-%s.%s", objectType, subObjectKey, extName);
        } else {
            objKey = String.format("%s-%s", objectType, subObjectKey);
        }
        return objKey;
    }

    private static String sign(String objFileName, String objKey) {
        String s = null;
        try {
            String ss = MD5Helper.md5(objFileName) + MD5Helper.md5(objKey);
            s = MD5Helper.md5(ss);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
