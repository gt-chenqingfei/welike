package com.redefine.welike.business.common.mission;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

import com.redefine.foundation.utils.VersionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.commonui.util.ToastUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by nianguowang on 2018/5/25
 */
public class MissionNotificationHelper {

    private static int notifyId = 1000;
    private static Disposable disposable;

    private static SparseArray<String> mTitleMap = new SparseArray<>();
    private static SparseArray<String> mContentMap = new SparseArray<>();

    static {
        mTitleMap.put(1, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_like"));
        mTitleMap.put(2, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_discover"));
        mTitleMap.put(3, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_hot_post"));
        mTitleMap.put(4, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_rank"));
        mTitleMap.put(5, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_activity"));
        mTitleMap.put(6, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_avatar"));
        mTitleMap.put(7, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_introduction"));
        mTitleMap.put(8, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_new_weliker_post"));
        mTitleMap.put(9, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_title_task_list"));


        mContentMap.put(1, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_like"));
        mContentMap.put(2, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_discover"));
        mContentMap.put(3, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_hot_post"));
        mContentMap.put(4, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_rank"));
        mContentMap.put(5, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_activity"));
        mContentMap.put(6, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_avatar"));
        mContentMap.put(7, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_introduction"));
        mContentMap.put(8, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_new_weliker_post"));
        mContentMap.put(9, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "new_task_content_task_list"));
    }

    public static void showNotification(int value) {
        if(value < 1 || value > 9) {
            return;
        }
        String title = mTitleMap.get(value);
        String content = mContentMap.get(value);
        Context context = MyApplication.getAppContext();
        if(VersionUtil.isUpperOrEqual5_0()) {
            showNotificationAboveM(context, title, content);
        } else {
            showNotificationBelowM(content);
        }
    }

    private static void showNotificationAboveM(Context context, String title, String content) {
        if(disposable != null) {
            disposable.dispose();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifyId, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context);
        notificationBuilder

                .setSmallIcon(R.drawable.welike_logo)
                .setPriority(PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(content)
                .setFullScreenIntent(pendingIntent, true)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        Notification notification = notificationBuilder.build();

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != notificationManager) {
            notificationManager.notify(notifyId, notification);
        }

        disposable = AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (notificationManager != null) {
                    notificationManager.cancel(notifyId);
                }
            }
        }, 4000, TimeUnit.MILLISECONDS);
    }

    private static void showNotificationBelowM(String content) {
        ToastUtils.showLong(content);
    }
}
