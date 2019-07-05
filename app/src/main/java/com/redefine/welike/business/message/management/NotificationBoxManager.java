package com.redefine.welike.business.message.management;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.management.provider.INotifyCallback;
import com.redefine.welike.business.message.management.provider.NotificationsProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/25.
 */

public class NotificationBoxManager extends SingleListenerManagerBase implements INotifyCallback {
    private NotificationsProvider dataSourceProvider;

    @Override
    public void onRefreshNotification(final List<NotificationBase> posts, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                INotifyCallback callback  = getCallback();
                if (callback != null) {
                    callback.onRefreshNotification(posts, errCode);
                }
            }
        });
    }

    @Override
    public void onReceiveHisNotification(final List<NotificationBase> posts, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                INotifyCallback callback  = getCallback();
                if (callback != null) {
                    callback.onReceiveHisNotification( posts, last, errCode);
                }
            }
        });
    }

    public interface NotificationCallback {

        void onRefreshNotification(NotificationBoxManager manager, List<NotificationBase> posts, int errCode);
        void onReceiveHisNotification(NotificationBoxManager manager, List<NotificationBase> posts, boolean last, int errCode);

    }

    public void setListener(INotifyCallback listener) {
        super.setListener(listener);
    }

    public void setNotificationType(String type) {
        dataSourceProvider = new NotificationsProvider(type);
        dataSourceProvider.setListener(this);
    }

    public void tryRefreshPosts() {
        if (dataSourceProvider != null) {
            dataSourceProvider.tryRefreshNotification();
        }
    }

    public void tryHisPosts() {
        if (dataSourceProvider != null) {
            dataSourceProvider.tryHisNotification();
        }
    }

//    @Override
//    public void onRefreshNotification(final List<NotificationBase> notificationBases, final int errCode) {
//        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//            @Override
//            public void run() {
//                NotificationCallback callback  = getCallback();
//                if (callback != null) {
//                    callback.onRefreshNotification(NotificationBoxManager.this, notificationBases, errCode);
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onReceiveHisNotification(final List<NotificationBase> notificationBases, final boolean last, final int errCode) {
//        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//            @Override
//            public void run() {
//                NotificationCallback callback  = getCallback();
//                if (callback != null) {
//                    callback.onReceiveHisNotification(NotificationBoxManager.this, notificationBases, last, errCode);
//                }
//            }
//        });
//    }

    private INotifyCallback getCallback() {
        INotifyCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof INotifyCallback) {
            callback = (INotifyCallback)l;
        }
        return callback;
    }

}
