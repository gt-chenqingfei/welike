package com.redefine.foundation.framework;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 2018/2/1.
 */

public abstract class BroadcastManagerBase {
    protected final ReferenceQueue<? super Object> referenceQueue = new ReferenceQueue<>();
    protected final List<ListenerRefExt> listenerRefList = new ArrayList<>();

    public class ListenerRefExt {
        private WeakReference<?> reference;
        private Map<String, String> userInfo = new HashMap<>();

        private ListenerRefExt(WeakReference<?> reference, Map<String, String> userInfo) {
            this.reference = reference;
            if (userInfo != null) {
                this.userInfo.putAll(userInfo);
            }
        }

        private void clear() {
            if (reference != null) {
                reference.clear();
            }
        }

        public Object getListener() {
            if (reference != null) {
                return reference.get();
            }
            return null;
        }

        public String userInfoGetObject(String key) { return userInfo.get(key); }

    }

    protected void register(Object listener) {
        synchronized (listenerRefList) {
            if (!containListener(listener)) {
                WeakReference<?> listenerRef = new WeakReference<>(listener, referenceQueue);
                ListenerRefExt refExt = new ListenerRefExt(listenerRef, null);
                listenerRefList.add(refExt);
            }
        }
    }

    protected void register(Object listener, Map<String, String> userInfo) {
        synchronized (listenerRefList) {
            if (!containListener(listener)) {
                WeakReference<?> listenerRef = new WeakReference<>(listener, referenceQueue);
                ListenerRefExt refExt = new ListenerRefExt(listenerRef, userInfo);
                listenerRefList.add(refExt);
            }
        }
    }

    protected void unregister(Object listener) {
        synchronized (listenerRefList) {
            for (int i = 0; i < listenerRefList.size(); i++) {
                ListenerRefExt refExt = listenerRefList.get(i);
                if (refExt.reference != null && refExt.reference.get() == listener) {
                    refExt.clear();
                    listenerRefList.remove(i);
                    break;
                }
            }
        }
    }

    private boolean containListener(Object listener) {
        for (int i = 0; i < listenerRefList.size(); i++) {
            ListenerRefExt refExt = listenerRefList.get(i);
            if (refExt.reference != null && refExt.reference.get() == listener) return true;
        }
        return false;
    }

}
