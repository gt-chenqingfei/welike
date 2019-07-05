package com.redefine.welike.base.uploading;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.MD5Helper;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.base.profile.UploadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/15.
 */

public class UploadingManager extends BroadcastManagerBase implements UploadTrans.UploadTransCallback {
    public static final String UPLOAD_TYPE_IMG = "img";
    public static final String UPLOAD_TYPE_VIDEO = "video";
    private static final int UPLOAD_TASKS_NUM = 3;
    private final List<UploadTrans> waitingTasks = new ArrayList<>();
    private final Map<String, UploadTrans> runningTasks = new ConcurrentHashMap<>();
    private Context context;

    public interface UploadingCallback {

        void onUploadingProcess(String objKey, float process);

        void onUploadingCompleted(String objKey, String url);

        void onUploadingFailed(String objKey);

    }

    private static class UploadingManagerHolder {
        public static UploadingManager instance = new UploadingManager();
    }

    private UploadingManager() {
    }

    public static UploadingManager getInstance() {
        return UploadingManagerHolder.instance;
    }

    public void register(UploadingCallback listener) {
        super.register(listener);
    }

    public void unregister(UploadingCallback listener) {
        super.unregister(listener);
    }

    public void init(Context context) {
        this.context = context;
    }

    public String upload(String objectKey, String objFileName, String extName, String objectType) {
        return upload(objectKey, objFileName, extName, objectType, null);
    }

    public String upload(String objectKey, String objFileName, String extName, String objectType, UploadListener listener) {
        if (TextUtils.isEmpty(objFileName)) return null;
        File file = new File(objFileName);
        if (!file.exists()) return null;

        String sign;
        if (TextUtils.isEmpty(objectKey)) {
            objectKey = buildObjectKey(CommonHelper.generateUUID(), extName, objectType);
            sign = sign(objFileName, objectKey);
        } else {
            sign = sign(objFileName, objectKey);
        }
        UploadTrans trans;
        String k = UploadStatusStorage.getInstance().getMultiPartStatus(sign);
        if (!TextUtils.isEmpty(k) && TextUtils.equals(k, objectKey)) {
            trans = new UploadTrans(objFileName, objectKey, sign, true, context);
        } else {
            trans = new UploadTrans(objFileName, objectKey, sign, false, context);
        }
        if (runningTasks.size() < UPLOAD_TASKS_NUM) {
            startTask(trans, listener);
        } else {
            waitingTasks.add(trans);
        }
        return objectKey;
    }

    public void remove(String objectKey, String objFileName) {
        String sign = sign(objFileName, objectKey);
        UploadTrans trans = removeWaitingTrans(sign);
        if (trans != null) {
            UploadStatusStorage.getInstance().removeMultiPartStatus(sign);
        } else {
            trans = runningTasks.remove(sign);
            if (trans != null) {
                trans.stop();
                UploadStatusStorage.getInstance().removeMultiPartStatus(sign);
            }
        }
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

    @Override
    public void onUploadTransProcess(UploadTrans trans, float process) {
        String sign = trans.getSign();
        if (runningTasks.containsKey(sign)) {
            broadcastProcess(trans.getObjectKey(), process);
        }
    }

    @Override
    public void onUploadTransSuccessed(UploadTrans trans, String url) {
        String sign = trans.getSign();
        if (runningTasks.remove(sign) != null) {
            UploadStatusStorage.getInstance().removeMultiPartStatus(sign);
            broadcastCompleted(trans.getObjectKey(), url);
            next();
        }
    }

    @Override
    public void onUploadTransFailed(UploadTrans trans, int errCode) {
        String sign = trans.getSign();
        if (runningTasks.remove(sign) != null) {
            broadcastFailed(trans.getObjectKey());
            next();
        }
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

    private void startTask(UploadTrans trans, final UploadListener listener) {
        removeWaitingTrans(trans.getSign());
        runningTasks.put(trans.getSign(), trans);
        UploadTrans.UploadTransCallback callback = new UploadTrans.UploadTransCallback() {

            @Override
            public void onUploadTransProcess(UploadTrans trans, float process) {
                UploadingManager.this.onUploadTransProcess(trans, process);
            }

            @Override
            public void onUploadTransSuccessed(UploadTrans trans, String url) {
                UploadingManager.this.onUploadTransSuccessed(trans, url);
                if (listener != null) {
                    listener.onFinish(url);
                }
            }

            @Override
            public void onUploadTransFailed(UploadTrans trans, int errCode) {
                UploadingManager.this.onUploadTransFailed(trans, errCode);
                if (listener != null) {
                    listener.onFail();
                }
            }
        };
        if (trans.isResume()) {
            trans.resume(callback);
        } else {
            trans.start(callback);
            UploadStatusStorage.getInstance().putMultiPartStatus(trans.getSign(), trans.getObjectKey());
        }
    }

    private void next() {
        if (runningTasks.size() < UPLOAD_TASKS_NUM) {
            UploadTrans trans = null;
            synchronized (waitingTasks) {
                if (waitingTasks.size() > 0) {
                    trans = waitingTasks.remove(0);
                }
            }
            if (trans != null) {
                startTask(trans, null);
            }
        }
    }

    private UploadTrans removeWaitingTrans(String sign) {
        synchronized (waitingTasks) {
            int idx = -1;
            for (int i = 0; i < waitingTasks.size(); i++) {
                UploadTrans t = waitingTasks.get(i);
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

    private void broadcastProcess(final String objKey, float process) {
        float p = process;
        if (p > 100) p = 100;
        final float pp = p;
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof UploadingCallback) {
                            UploadingCallback listener = (UploadingCallback) l;
                            listener.onUploadingProcess(objKey, pp);
                        }
                    }
                }
            }

        });
    }

    private void broadcastCompleted(final String objKey, final String url) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof UploadingCallback) {
                            UploadingCallback listener = (UploadingCallback) l;
                            listener.onUploadingCompleted(objKey, url);
                        }
                    }
                }
            }

        });
    }

    private void broadcastFailed(final String objKey) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof UploadingCallback) {
                            UploadingCallback listener = (UploadingCallback) l;
                            listener.onUploadingFailed(objKey);
                        }
                    }
                }
            }

        });
    }

}
