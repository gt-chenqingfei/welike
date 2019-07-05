package com.redefine.im.management;

import android.text.TextUtils;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.cache.IMMessageCache;
import com.redefine.im.service.IMService;
import com.redefine.im.service.IMServiceCallback;
import com.redefine.welike.base.ErrorCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liubin on 2018/2/6.
 */

public class IMSessionProvider extends BroadcastManagerBase implements IIMSessionProvider {
    private IMService imService;
    private String goInSessionReqId;
    private IMCountProvider imCountProvider;

    public interface IMSessionProviderCallback {

        void onOfflineMessagesCompleted();
        void onReceivedMessages(List<IMMessageBase> messages);
        void onSessionUpdated(Set<String> sids);
        void onListAllSessions(List<IMSession> sessions, boolean greet);
        void onListSessions(List<IMSession> sessions);
        void onCountSessions(int count, boolean greet);

    }

    IMSessionProvider(IMCountProvider imCountProvider) {
        this.imCountProvider = imCountProvider;
    }

    public void init(IMService imService) {
        this.imService = imService;
    }

    void close() {
        imService = null;
    }

    @Override
    public void register(IMSessionProviderCallback listener) {
        super.register(listener);
    }

    @Override
    public void unregister(IMSessionProviderCallback listener) {
        super.unregister(listener);
    }

    @Override
    public void goInSession(final IMSession session, final IMSessionGotoCallback callback) {
        goInSessionReqId = CommonHelper.generateUUID();
        final String myGoInSessionReqId = goInSessionReqId;
        IMMessageCache.getInstance().clearSessionUnreadCount(session.getSid(), new IMMessageCache.IMMessageCacheClearSessionUnreadCountCallback() {

            @Override
            public void onClearSessionUnreadCount(int count) {
                imCountProvider.handleMessagesCountChanged(count, true);
                callGoInSession(session, myGoInSessionReqId, ErrorCode.ERROR_SUCCESS, callback);
            }
        });
    }

    @Override
    public void goInSingleSession(final String uid, final IMSessionGotoCallback callback) {
        goInSessionReqId = CommonHelper.generateUUID();
        final String myGoInSessionReqId = goInSessionReqId;
        if (imService != null) {
            IMMessageCache.getInstance().getSingleChatSession(uid, new IMMessageCache.IMMessageCacheSingleSessionCallback() {

                @Override
                public void onGetSingleSession(final IMSession session) {
                    if (session == null) {
                        imService.createSingleSession(uid, myGoInSessionReqId, new IMServiceCallback() {

                            @Override
                            public void onSessionCreated(IMSession session, String reqId, int errCode) {
                                if (errCode == ErrorCode.ERROR_SUCCESS) {
                                    session.setUnreadCount(0);
                                    callGoInSession(session, myGoInSessionReqId, ErrorCode.ERROR_SUCCESS, callback);
                                } else {
                                    callGoInSession(null, myGoInSessionReqId, errCode, callback);
                                }
                            }

                        });
                    } else {
                        IMMessageCache.getInstance().clearSessionUnreadCount(session.getSid(), new IMMessageCache.IMMessageCacheClearSessionUnreadCountCallback() {

                            @Override
                            public void onClearSessionUnreadCount(int count) {
                                imCountProvider.handleMessagesCountChanged(count, true);
                                callGoInSession(session, myGoInSessionReqId, ErrorCode.ERROR_SUCCESS, callback);
                            }

                        });
                    }
                }
            });
        } else {
            callGoInSession(null, myGoInSessionReqId, ErrorCode.ERROR_IM_SERVICE_MISS, callback);
        }
    }

    @Override
    public void goInCustomerSession(final IMSessionGotoCallback callback) {
        goInSessionReqId = CommonHelper.generateUUID();
        final String myGoInSessionReqId = goInSessionReqId;
        if (imService != null) {
            imService.createCustomerSession(myGoInSessionReqId, new IMServiceCallback() {

                @Override
                public void onSessionCreated(IMSession session, String reqId, int errCode) {
                    if (errCode == ErrorCode.ERROR_SUCCESS) {
                        session.setUnreadCount(0);
                        //IMMessageCache.getInstance().insertOrUpdateSession(session);
                        callGoInSession(session, myGoInSessionReqId, ErrorCode.ERROR_SUCCESS, callback);
                    } else {
                        callGoInSession(null, myGoInSessionReqId, errCode, callback);
                    }
                }

            });
        } else {
            callGoInSession(null, myGoInSessionReqId, ErrorCode.ERROR_IM_SERVICE_MISS, callback);
        }
    }

    @Override
    public void listAllSessions(final boolean greet) {
        IMMessageCache.getInstance().listAllSessions(greet, new IMMessageCache.IMMessageCacheSessionsCallback() {

            @Override
            public void onListSessions(List<IMSession> sessions) {
                broadcastAllSessions(sessions, greet);
            }

        });
    }

    @Override
    public void getSessions(Set<String> sids) {
        IMMessageCache.getInstance().getSessions(sids, new IMMessageCache.IMMessageCacheSessionsCallback() {

            @Override
            public void onListSessions(List<IMSession> sessions) {
                broadcastSessions(sessions);
            }

        });
    }

    @Override
    public void countAllSessions(final boolean greet) {
        IMMessageCache.getInstance().countAllSessions(greet, new IMMessageCache.IMMessageCacheSessionsCountCallback() {

            @Override
            public void onSessionsCount(int count) {
                broadcastSessionsCount(count, greet);
            }

        });
    }

    @Override
    public void removeSession(IMSession session) {
        imCountProvider.handleMessagesCountChanged(-session.getUnreadCount(), true);
        IMMessageCache.getInstance().deleteSession(session);
    }

    void handleOfflineMessages(boolean last) {
        if (last) {
            broadcastOfflineMessagesCompleted();
        }
    }

    void handleReceiveMessages(List<IMMessageBase> messages, Set<String> sids) {
        broadcastReceivedMessages(messages, sids);
    }

    void handleSendMessageSessionUpdated(String sid) {
        final Set<String> sids = new HashSet<>();
        sids.add(sid);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMSessionProviderCallback) {
                            IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                            listener.onSessionUpdated(sids);
                        }
                    }
                }
            }

        });
    }

    private void broadcastOfflineMessagesCompleted() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMSessionProviderCallback) {
                            IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                            listener.onOfflineMessagesCompleted();
                        }
                    }
                }
            }

        });
    }

    private void broadcastReceivedMessages(final List<IMMessageBase> messages, final Set<String> sids) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMSessionProviderCallback) {
                            IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                            listener.onReceivedMessages(messages);
                        }
                    }
                }
            }

        });

        if (sids != null && sids.size() > 0) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    synchronized (listenerRefList) {
                        for (int i = 0; i < listenerRefList.size(); i++) {
                            ListenerRefExt callbackRef = listenerRefList.get(i);
                            Object l = callbackRef.getListener();
                            if (l != null && l instanceof IMSessionProviderCallback) {
                                IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                                listener.onSessionUpdated(sids);
                            }
                        }
                    }
                }

            });
        }
    }

    private void broadcastAllSessions(final List<IMSession> sessions, final boolean greet) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMSessionProviderCallback) {
                            IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                            listener.onListAllSessions(sessions, greet);
                        }
                    }
                }
            }

        });
    }

    private void broadcastSessions(final List<IMSession> sessions) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMSessionProviderCallback) {
                            IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                            listener.onListSessions(sessions);
                        }
                    }
                }
            }

        });
    }

    private void broadcastSessionsCount(final int count, final boolean greet) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMSessionProviderCallback) {
                            IMSessionProviderCallback listener = (IMSessionProviderCallback)l;
                            listener.onCountSessions(count, greet);
                        }
                    }
                }
            }

        });
    }

    private void callGoInSession(final IMSession session, final String reqId, final int errCode, final IMSessionGotoCallback callback) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (TextUtils.equals(reqId, goInSessionReqId)) {
                    if (callback != null) {
                        callback.onGotoSession(session, errCode);
                    }
                }
            }

        });
    }

}
