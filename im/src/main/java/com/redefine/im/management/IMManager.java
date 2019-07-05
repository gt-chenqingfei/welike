package com.redefine.im.management;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.BuildConfig;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.cache.IMMessageCache;
import com.redefine.im.service.IIMServiceFactoryExceptionListener;
import com.redefine.im.service.IMService;
import com.redefine.im.service.IMServiceListener;
import com.redefine.im.service.IMServiceFactory;
import com.redefine.im.service.socket.IMMessageSocketObserver;

import java.util.List;
import java.util.Set;

/**
 * Created by liubin on 2018/2/6.
 */

public class IMManager extends SingleListenerManagerBase implements IMServiceFactory.IMServiceFactoryCallback,
        IMServiceListener {
    private IMServiceFactory serviceFactory;
    private IMService imService;
    private IMSessionProvider imSessionProvider;
    private IMChatProvider imChatProvider;
    private IMCountProvider imCountProvider;
    private String token;
    private String uid;
    private Context context;

    public interface IMManagerCallback {

        void onIMTokenInvalid();

    }

    private static class IMManagerHolder {
        public static IMManager instance = new IMManager();
    }

    private IMManager() {
        serviceFactory = new IMServiceFactory();
        serviceFactory.setListener(this);
        imChatProvider = new IMChatProvider(this);
        imCountProvider = new IMCountProvider();
        imSessionProvider = new IMSessionProvider(imCountProvider);
    }

    public static IMManager getInstance() {
        return IMManagerHolder.instance;
    }

    public void bind(Activity activity, IIMServiceFactoryExceptionListener listener, String token, String uid) {
        if(BuildConfig.DEBUG){
            return ;
        }
        this.token = token;
        this.uid = uid;
        UniqueFilter.INSTANCE.init(activity.getApplication());
        serviceFactory.bindService(activity, listener);
        context = activity.getApplicationContext();
    }

    public void unbind(Activity activity) {
        serviceFactory.unbindService(activity);
        token = null;
        context = null;
    }

    public void setListener(IMManagerCallback callback) {
        super.setListener(callback);
    }

    public IIMSessionProvider getIMSessionProvider() {
        return imSessionProvider;
    }

    public IIMChatProvider getIMCharProvider() {
        return imChatProvider;
    }

    public IIMCountProvider getIMCountProvider() {
        return imCountProvider;
    }

    @Override
    public void onOfflineMessages(List<IMMessageBase> messages, boolean last) {
        IMMessageCache.getInstance().insertReceivedMessages(messages, last, new IMMessageCache.IMMessageCacheReceivedCallback() {

            @Override
            public void onReceivedMessagesInserted(final List<IMMessageBase> messages2, Set<String> sids, final boolean last) {
                if (messages2 != null && messages2.size() > 0) {
                    imSessionProvider.handleOfflineMessages(last);

                    int count = 0;
                    if (!TextUtils.isEmpty(imChatProvider.startSid())) {
                        IMMessageCache.getInstance().clearSessionUnreadCount(imChatProvider.startSid(), new IMMessageCache.IMMessageCacheClearSessionUnreadCountCallback() {

                            @Override
                            public void onClearSessionUnreadCount(int count) {
                                for (IMMessageBase message : messages2) {
                                    if (!TextUtils.equals(message.getSid(), imChatProvider.startSid())) {
                                        if (message.getType() != IMMessageBase.MESSAGE_TYPE_SYSTEM) {
                                            count++;
                                        }
                                    }
                                }
                                imCountProvider.handleMessagesCountChanged(count, last);
                            }

                        });
                    } else {
                        for (IMMessageBase message : messages2) {
                            if (message.getType() != IMMessageBase.MESSAGE_TYPE_SYSTEM) {
                                count++;
                            }
                        }
                        imCountProvider.handleMessagesCountChanged(count, last);
                    }
                }
                if (last) {
                    imService.authIM(token, uid);
                }
            }

        });
    }

    @Override
    public void onReceiveMessages(List<IMMessageBase> messages) {
        IMMessageCache.getInstance().insertReceivedMessages(messages, true, new IMMessageCache.IMMessageCacheReceivedCallback() {

            @Override
            public void onReceivedMessagesInserted(final List<IMMessageBase> messages, Set<String> sids, boolean last) {
                if (messages != null && messages.size() > 0) {
                    int count;
                    if (!TextUtils.isEmpty(imChatProvider.startSid())) {
                        IMMessageCache.getInstance().clearSessionUnreadCount(imChatProvider.startSid(), new IMMessageCache.IMMessageCacheClearSessionUnreadCountCallback() {

                            @Override
                            public void onClearSessionUnreadCount(int count) {
                                for (IMMessageBase message : messages) {
                                    if (!TextUtils.equals(message.getSid(), imChatProvider.startSid())) {
                                        count++;
                                    }
                                }
                                imCountProvider.handleMessagesCountChanged(count, true);
                            }

                        });
                    } else {
                        count = messages.size();
                        imCountProvider.handleMessagesCountChanged(count, true);
                    }
                    imSessionProvider.handleReceiveMessages(messages, sids);
                    imChatProvider.handleReceivedMessages(messages);
                }
            }

        });
    }

    @Override
    public void onMessageSendResult(String oldMid, IMMessageBase message, int errCode) {
        imChatProvider.handleMessageSendResult(oldMid, message, errCode);
    }

    @Override
    public void onTokenInvalid() {
        IMManagerCallback callback = getCallback();
        if (callback != null) {
            callback.onIMTokenInvalid();
        }
    }

    @Override
    public void onMessageNotificationCountChanged() {
        imCountProvider.handleMessageNotificationCountChanged();
    }

    @Override
    public void onIMServiceCreated(IMService service) {
        LogUtil.d("welike-liubin", "onIMServiceCreated");
        imService = service;
        if (imService != null) {
            IMMessageSocketObserver messageObserver = new IMMessageSocketObserver();
            imService.init(messageObserver, context);
            imService.setListener(this);
            imSessionProvider.init(imService);
            imChatProvider.init(imService);
        }
    }

    @Override
    public void onIMServiceDestroy() {
        LogUtil.d("welike-liubin", "onIMServiceDestroy");
        imService = null;
        imSessionProvider.close();
        imChatProvider.close();
    }

    public void handleSendMessageSessionUpdated(String sid) {
        imSessionProvider.handleSendMessageSessionUpdated(sid);
    }

    private IMManagerCallback getCallback() {
        IMManagerCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof IMManagerCallback) {
            callback = (IMManagerCallback) l;
        }
        return callback;
    }

}
