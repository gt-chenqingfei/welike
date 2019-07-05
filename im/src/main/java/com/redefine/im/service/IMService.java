package com.redefine.im.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.redefine.foundation.framework.ScheduleService;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.bean.IMOfflineMessagesTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liubin on 2018/2/3.
 */

public class
IMService extends Service implements IMOfflineMessagesTask.OfflineMessagesCallack,
        MessageObserverListener {
    private static final int IM_SERVICE_STATE_DISCONNECT = 0;
    private static final int IM_SERVICE_STATE_OFFILINE_LOADING = 1;
    private static final int IM_SERVICE_STATE_OFFILINE_COMPLETED = 2;
    private static final int IM_SERVICE_STATE_CONNECTING = 3;
    private static final int IM_SERVICE_STATE_CONNECTED = 4;
    private static final long IM_OFFLINE_MESSAGES_UPDATE_RETRY_PERIOD_SEC = 10;

    private IMServiceListener listener;
    private IMBinder binder = new IMBinder();
    private int state;
    private String offlineMessagesTaskId;
    private IMessageObserver messageObserver;

    public class IMBinder extends Binder {

        public IMService getService() {
            return IMService.this;
        }

    }

    public IMService() {
        state = IM_SERVICE_STATE_DISCONNECT;
    }

    public void init(IMessageObserver messageObserver, Context context) {
        if (state == IM_SERVICE_STATE_DISCONNECT) {
            this.messageObserver = messageObserver;
            this.messageObserver.setListener(this);
            this.messageObserver.prepare(context);
            IMOfflineMessagesTask task = new IMOfflineMessagesTask(context);
            task.setListener(this);
            ScheduleService.getInstance().startRetry(task, IM_OFFLINE_MESSAGES_UPDATE_RETRY_PERIOD_SEC, TimeUnit.SECONDS);
            state = IM_SERVICE_STATE_OFFILINE_LOADING;
        }
    }

    public void setListener(IMServiceListener listener) {
        this.listener = listener;
    }

    public void authIM(String token, String uid) {
        if (state == IM_SERVICE_STATE_OFFILINE_COMPLETED) {
            if (messageObserver != null) {
                messageObserver.connect(token, uid);
                state = IM_SERVICE_STATE_CONNECTING;
            }
        }
    }

    public void sendMessage(IMMessageBase message) {
        if (messageObserver != null) {
            messageObserver.sendMessage(message);
        }
    }

    public void createSingleSession(String uid, String reqId, IMServiceCallback callback) {
        if (messageObserver != null) {
            messageObserver.createSingleSession(uid, reqId, callback);
        }
    }

    public void createCustomerSession(String reqId, IMServiceCallback callback) {
        if (messageObserver != null) {
            messageObserver.createCustomerSession(reqId, callback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtil.d("welike-liubin", "IMService onDestroy");
        super.onDestroy();
        disconnect();
        listener = null;
    }

    @Override
    public void onOfflineMessages(List<IMMessageBase> messages, boolean last) {
        if (last) {
            offlineMessagesTaskId = null;
            state = IM_SERVICE_STATE_OFFILINE_COMPLETED;
        }
        List<IMMessageBase> offlineMessages = messages;
        if (offlineMessages == null) {
            offlineMessages = new ArrayList<>();
        }
        if (listener != null) {
            listener.onOfflineMessages(offlineMessages, last);
        }
    }

    @Override
    public void onAuthSuccessed() {
        if (listener != null) {
            state = IM_SERVICE_STATE_CONNECTED;
        }
    }

    @Override
    public void onTokenInvalid() {
        if (listener != null) {
            listener.onTokenInvalid();
        }
    }

    @Override
    public void onReceiveMessages(List<IMMessageBase> messages) {
        if (listener != null) {
            listener.onReceiveMessages(messages);
        }
    }

    @Override
    public void onMessageSendResult(String oldMid, IMMessageBase message, int errCode) {
        if (listener != null) {
            listener.onMessageSendResult(oldMid, message, errCode);
        }
    }

    @Override
    public void onMessageNotificatinsCountChanged() {
        if (listener != null) {
            listener.onMessageNotificationCountChanged();
        }
    }

    private void disconnect() {
        if (state != IM_SERVICE_STATE_DISCONNECT) {
            if (!TextUtils.isEmpty(offlineMessagesTaskId)) {
                ScheduleService.getInstance().cancel(offlineMessagesTaskId);
                offlineMessagesTaskId = null;
            }
            if (messageObserver != null) {
                messageObserver.disconnect();
                messageObserver = null;
            }
            state = IM_SERVICE_STATE_DISCONNECT;
        }
    }

}
