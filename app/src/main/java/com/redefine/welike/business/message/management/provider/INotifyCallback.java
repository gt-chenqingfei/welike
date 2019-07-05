package com.redefine.welike.business.message.management.provider;

import com.redefine.welike.business.message.management.bean.NotificationBase;

import java.util.List;

/**
 * Created by MR on 2018/1/26.
 */

public interface INotifyCallback {

//    void onRefreshNotification(final List<KMNotify> posts, final int errCode);
//
//    void onReceiveHisNotification(final List<KMNotify> posts, final boolean last, final int errCode);

    void onRefreshNotification(final List<NotificationBase> posts, final int errCode);
    void onReceiveHisNotification(final List<NotificationBase> posts, final boolean last, final int errCode);

}
