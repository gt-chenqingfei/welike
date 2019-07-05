package com.redefine.im.management;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.im.bean.IMSession;
import com.redefine.im.cache.IMMessageCache;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liubin on 2018/2/11.
 */

public class IMCountProvider extends BroadcastManagerBase implements IIMCountProvider {
    private int count;

    public interface IMCountProviderCallback {

        void onIMMessagesCountChanged(int count);
        void onMessageNotificationCountChanged();

    }

    IMCountProvider() {}

    @Override
    public void register(IMCountProviderCallback listener) {
        super.register(listener);
    }

    @Override
    public void unregister(IMCountProviderCallback listener) {
        super.unregister(listener);
    }

    @Override
    public void reload() {
        count = 0;
        IMMessageCache.getInstance().listAllSessions(false, new IMMessageCache.IMMessageCacheSessionsCallback() {

            @Override
            public void onListSessions(List<IMSession> sessions) {
                if (sessions != null && sessions.size() > 0) {
                    for (IMSession session : sessions) {
                        count += session.getUnreadCount();
                    }
                }

                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (listenerRefList) {
                            for (int i = 0; i < listenerRefList.size(); i++) {
                                ListenerRefExt callbackRef = listenerRefList.get(i);
                                Object l = callbackRef.getListener();
                                if (l != null && l instanceof IMCountProvider.IMCountProviderCallback) {
                                    IMCountProvider.IMCountProviderCallback listener = (IMCountProvider.IMCountProviderCallback)l;
                                    listener.onIMMessagesCountChanged(count);
                                }
                            }
                        }
                    }

                });
            }

        });
    }

    @Override
    public int getIMMessagesCount() {
        return count;
    }

    void handleMessagesCountChanged(int count, boolean finished) {
        this.count = this.count + count;
        if (this.count < 0) {
            this.count = 0;
        }
        if (finished) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    synchronized (listenerRefList) {
                        for (int i = 0; i < listenerRefList.size(); i++) {
                            ListenerRefExt callbackRef = listenerRefList.get(i);
                            Object l = callbackRef.getListener();
                            if (l != null && l instanceof IMCountProvider.IMCountProviderCallback) {
                                IMCountProvider.IMCountProviderCallback listener = (IMCountProvider.IMCountProviderCallback)l;
                                listener.onIMMessagesCountChanged(IMCountProvider.this.count);
                            }
                        }
                    }
                }

            });
        }
    }

    public void handleMessageNotificationCountChanged() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IMCountProvider.IMCountProviderCallback) {
                            IMCountProvider.IMCountProviderCallback listener = (IMCountProvider.IMCountProviderCallback)l;
                            listener.onMessageNotificationCountChanged();
                        }
                    }
                }
            }

        });
    }

}
