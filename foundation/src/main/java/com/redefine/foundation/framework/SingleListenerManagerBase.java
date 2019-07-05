package com.redefine.foundation.framework;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Created by liubin on 2018/2/1.
 */

public abstract class SingleListenerManagerBase {
    protected final ReferenceQueue<? super Object> referenceQueue = new ReferenceQueue<>();
    protected WeakReference<?> listenerRef;

    protected void setListener(Object listener) {
        boolean isSame = false;
        if (listenerRef != null) {
            Object oldObj = listenerRef.get();
            if (oldObj != null && listener == oldObj) {
                isSame = true;
            }
        }

        if (!isSame) {
            if (listenerRef != null) {
                referenceQueue.poll();
                listenerRef.clear();
                listenerRef = null;
            }
            if (listener != null) {
                listenerRef = new WeakReference<>(listener, referenceQueue);
            }
        }
    }

    protected Object getListener() {
        if (listenerRef != null) {
            return listenerRef.get();
        }
        return null;
    }

}
