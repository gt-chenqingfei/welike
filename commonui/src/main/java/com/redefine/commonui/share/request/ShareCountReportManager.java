package com.redefine.commonui.share.request;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/9/9
 */
public class ShareCountReportManager extends BroadcastManagerBase implements RequestCallback {

    private ShareSuccessRequest request;
    private String postId;
    private ShareCountReportManager() {}

    private static class ShareCountManagerHolder {
        private static ShareCountReportManager sInstance = new ShareCountReportManager();
    }

    public interface ShareCountCallback {

        void onShareReportSuccess(String postId);

        void onShareReportFail(int errorCode);
    }

    public static ShareCountReportManager getInstance() {
        return ShareCountManagerHolder.sInstance;
    }

    public void register(ShareCountCallback listener) {
        super.register(listener);
    }

    public void unregister(ShareCountCallback listener) {
        super.unregister(listener);
    }

    public void reportPostShare(Context context, String postId) {
        if (request != null) {
            return;
        }
        this.postId = postId;
        request = new ShareSuccessRequest(context, postId);
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            notifyFail(ErrorCode.networkExceptionToErrCode(e));
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        notifyFail(errCode);
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        notifySuccess();
    }

    private void notifyFail(final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof ShareCountCallback) {
                            ShareCountCallback listener = (ShareCountCallback) l;
                            listener.onShareReportFail(errCode);
                        }
                    }
                    request = null;
                    postId = null;
                }
            }

        });
    }

    private void notifySuccess() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
            synchronized (listenerRefList) {
                for (int i = 0; i < listenerRefList.size(); i++) {
                    ListenerRefExt callbackRef = listenerRefList.get(i);
                    Object l = callbackRef.getListener();
                    if (l != null && l instanceof ShareCountCallback) {
                        ShareCountCallback listener = (ShareCountCallback) l;
                        listener.onShareReportSuccess(postId);
                    }
                }
                request = null;
                postId = null;
            }
            }

        });
    }

}
