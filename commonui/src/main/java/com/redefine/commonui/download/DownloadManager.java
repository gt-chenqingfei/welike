package com.redefine.commonui.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.redefine.commonui.share.CommonListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.resource.ResourceTool;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DownloadManager {

    private static final int DOWNLOAD_PROGRESS = 1;
    private static final int TOTAL_LENGTH = 2;
    private static final int TASK_STATUS = 3;
    private static final int TASK_RETRY_COUNT = 4;


    private static final int MAX_RETRY_COUNT = 3;
    private final Map<String, DownloadTask> mTasks = new HashMap<>();
    private final Map<String, Set<IDownloadListener>> mListeners = new HashMap<>();

    public DownloadManager() {

    }

    public boolean download(final Context applicationContext, final String url, final String filePath, final boolean isToast, final IDownloadIntercept intercept, final IDownloadListener downloadListener) {
        DownloadTask downloadTask = mTasks.get(url);
        final File file = new File(filePath);
        if (downloadTask == null) {
            DownloadState status = getDownloadState(url, filePath);
            if (status == DownloadState.COMPLETED) {
                if (file.exists()) {
                    addDownloadListeners(url, downloadListener);
                    notifyDownloadFinish(applicationContext, url, file.getAbsolutePath(), isToast);
                    return true;
                }
            }
            downloadTask = new DownloadTask.Builder(url, file).setAutoCallbackToUIThread(true)
                    .setConnectionCount(5).setWifiRequired(false).setMinIntervalMillisCallbackProcess(300).build();
            addDownloadTask(url, downloadTask);
            addDownloadListeners(url, downloadListener);
            downloadTask.enqueue(new DownloadListener3() {
                @Override
                protected void started(@NonNull DownloadTask task) {
                    task.addTag(TASK_STATUS, DownloadState.PENDING);
                    notifyDownloadStarted(url, file.getAbsolutePath(), isToast);
                }

                @Override
                protected void completed(@NonNull final DownloadTask task) {
                    task.addTag(TASK_STATUS, DownloadState.PROCESSING);
                    if (task.getTag(TOTAL_LENGTH) != null) {
                        task.addTag(DOWNLOAD_PROGRESS, task.getTag(TOTAL_LENGTH));
                    }
                    Schedulers.newThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (intercept != null) {
                                intercept.intercept(url, filePath, new CommonListener<String>() {

                                    @Override
                                    public void onFinish(String value) {
                                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                            @Override
                                            public void run() {
                                                task.addTag(TASK_STATUS, DownloadState.COMPLETED);
                                                notifyDownloadFinish(applicationContext, url, file.getAbsolutePath(), isToast);
                                            }
                                        }, 100, TimeUnit.MILLISECONDS);
                                    }

                                    @Override
                                    public void onError(String value) {
                                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                            @Override
                                            public void run() {
                                                task.addTag(TASK_STATUS, DownloadState.COMPLETED);
                                                notifyDownloadFinish(applicationContext, url, file.getAbsolutePath(), isToast);
                                            }
                                        }, 100, TimeUnit.MILLISECONDS);
                                    }
                                });
                            } else {
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        task.addTag(TASK_STATUS, DownloadState.COMPLETED);
                                        notifyDownloadFinish(applicationContext, url, file.getAbsolutePath(), isToast);
                                    }
                                }, 100, TimeUnit.MILLISECONDS);
                            }
                        }
                    });

                }

                @Override
                protected void canceled(@NonNull DownloadTask task) {
                    task.addTag(TASK_STATUS, DownloadState.IDLE);
                    notifyDownloadCanceled(applicationContext, url, file.getAbsolutePath(), isToast);
                }

                @Override
                protected void error(@NonNull final DownloadTask task, @NonNull Exception e) {
                    int retryCount = task.getTag(TASK_RETRY_COUNT) == null ? 0 : ((int) task.getTag(TASK_RETRY_COUNT));
                    if (retryCount < MAX_RETRY_COUNT) {
                        final DownloadListener3 listener3 = this;
                        retryCount++;
                        task.addTag(TASK_RETRY_COUNT, retryCount);
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                task.enqueue(listener3);
                            }
                        }, 100, TimeUnit.MILLISECONDS);
                    } else {
                        task.addTag(TASK_STATUS, DownloadState.IDLE);
                        notifyDownloadError(applicationContext, url, file.getAbsolutePath(), isToast);
                    }
                }

                @Override
                protected void warn(@NonNull final DownloadTask task) {
                    int retryCount = task.getTag(TASK_RETRY_COUNT) == null ? 0 : ((int) task.getTag(TASK_RETRY_COUNT));
                    if (retryCount < MAX_RETRY_COUNT) {
                        final DownloadListener3 listener3 = this;
                        retryCount++;
                        task.addTag(TASK_RETRY_COUNT, retryCount);
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                task.enqueue(listener3);
                            }
                        }, 100, TimeUnit.MILLISECONDS);
                    } else {
                        task.addTag(TASK_STATUS, DownloadState.IDLE);
                        notifyDownloadError(applicationContext, url, file.getAbsolutePath(), isToast);
                    }
                }

                @Override
                public void retry(@NonNull final DownloadTask task, @NonNull ResumeFailedCause cause) {
                    final DownloadListener3 listener3 = this;
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            int retryCount = task.getTag(TASK_RETRY_COUNT) == null ? 0 : ((int) task.getTag(TASK_RETRY_COUNT));
                            if (retryCount < MAX_RETRY_COUNT) {
                                retryCount++;
                                task.addTag(TASK_RETRY_COUNT, retryCount);
                                task.enqueue(listener3);

                            }
                        }
                    }, 100, TimeUnit.MILLISECONDS);
                }

                @Override
                public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
                    task.addTag(TASK_STATUS, DownloadState.RUNNING);
                    task.addTag(DOWNLOAD_PROGRESS, currentOffset);
                    task.addTag(TOTAL_LENGTH, totalLength);
                    notifyDownloadProgress(url, file.getAbsolutePath(), currentOffset, totalLength, isToast);
                }

                @Override
                public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                    task.addTag(TASK_STATUS, DownloadState.RUNNING);
                    task.addTag(DOWNLOAD_PROGRESS, currentOffset);
                    task.addTag(TOTAL_LENGTH, totalLength);
                    notifyDownloadProgress(url, file.getAbsolutePath(), currentOffset, totalLength, isToast);
                }
            });
        } else {
            addDownloadListeners(url, downloadListener);
        }
        return true;
    }

    public boolean download(final Context applicationContext, final String url, final String filePath, final IDownloadIntercept intercept, final IDownloadListener downloadListener) {
        return download(applicationContext, url, filePath, true, intercept, downloadListener);
    }

    public DownloadState getDownloadState(String url, String filePath) {
        DownloadTask downloadTask = getDownloadTask(url);
        if (downloadTask != null) {
            DownloadState downloadState = DownloadState.valueOf(StatusUtil.getStatus(downloadTask));
            if (downloadState == DownloadState.COMPLETED) {
                if (downloadTask.getTag(TASK_STATUS) == DownloadState.PROCESSING) {
                    return DownloadState.PROCESSING;
                }
                return downloadState;
            }
        }
        if (TextUtils.isEmpty(filePath)) {
            return DownloadState.UNKNOWN;
        }
        File file = new File(filePath);
        return DownloadState.valueOf(StatusUtil.getStatus(url, file.getParentFile().getAbsolutePath(), file.getName()));
    }


    private void notifyDownloadProgress(String url, String absolutePath, long currentOffset, long totalLength, boolean isToast) {
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (CollectionUtil.isEmpty(listeners)) {
                return ;
            }
            for (IDownloadListener listener : listeners) {
                listener.onDownloadProgress(url, absolutePath, currentOffset, totalLength);
            }
        }
    }

    private void notifyDownloadError(Context context, String url, String absolutePath, boolean isToast) {
        removeDownloadTask(url);
        if (isToast) {
            String toast = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "download_failed");
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (CollectionUtil.isEmpty(listeners)) {
                return ;
            }
            for (IDownloadListener listener : listeners) {
                listener.onDownloadError(url, absolutePath);
            }
            mListeners.remove(url);
        }
    }

    private void notifyDownloadCanceled(Context context, String url, String absolutePath, boolean isToast) {
        removeDownloadTask(url);
        if (isToast) {
            String toast = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "download_failed");
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (CollectionUtil.isEmpty(listeners)) {
                return ;
            }
            for (IDownloadListener listener : listeners) {
                listener.onDownloadCancel(url, absolutePath);
            }
            mListeners.remove(url);
        }
    }

    private void notifyDownloadStarted(String url, String absolutePath, boolean isToast) {
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (CollectionUtil.isEmpty(listeners)) {
                return ;
            }
            for (IDownloadListener listener : listeners) {
                listener.onDownloadStart(url, absolutePath);
            }
        }
    }

    private void notifyDownloadFinish(Context context, String url, String absolutePath, boolean isToast) {
        removeDownloadTask(url);
        if (isToast) {
            String toast = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_save_success") + "\n" + absolutePath;
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (CollectionUtil.isEmpty(listeners)) {
                return ;
            }
            for (IDownloadListener listener : listeners) {
                listener.onDownloadSuccess(url, absolutePath);
            }
            mListeners.remove(url);
        }
    }


    public void unRegisterListener(String url, IDownloadListener downloadListener) {
        if (TextUtils.isEmpty(url)) {
            return ;
        }
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (listeners == null) {
                return ;
            }
            listeners.remove(downloadListener);
            mListeners.put(url, listeners);
        }
    }


    public void retryBindDownloadTask(String url, String filePath, IDownloadListener downloadListener) {
        if (TextUtils.isEmpty(url)) {
            return ;
        }
        DownloadTask task = getDownloadTask(url);
        if (task != null) {
            synchronized (mListeners) {
                Set<IDownloadListener> listeners = mListeners.get(url);
                if (listeners == null) {
                    listeners = new HashSet<>();
                }
                listeners.add(downloadListener);
                if (task.getTag(DOWNLOAD_PROGRESS) != null && task.getTag(TOTAL_LENGTH) != null) {
                    downloadListener.onDownloadProgress(url, filePath, (long)task.getTag(DOWNLOAD_PROGRESS), (long)task.getTag(TOTAL_LENGTH));
                }
                mListeners.put(url, listeners);
            }
        }
    }

    private boolean isTaskExist(String url) {
        return getDownloadTask(url) != null;
    }

    private void addDownloadListeners(String url, IDownloadListener downloadListener) {
        if (TextUtils.isEmpty(url)) {
            return ;
        }
        synchronized (mListeners) {
            Set<IDownloadListener> listeners = mListeners.get(url);
            if (listeners == null) {
                listeners = new HashSet<>();
            }
            listeners.add(downloadListener);
            mListeners.put(url, listeners);
        }
    }

    public void cancelDownloadTask(String url) {
        if (TextUtils.isEmpty(url)) {
            return ;
        }
        DownloadTask downloadTask = getDownloadTask(url);
        if (downloadTask != null) {
            downloadTask.cancel();
        }
    }

    private DownloadTask getDownloadTask(String url) {
        synchronized (mTasks) {
            return mTasks.get(url);
        }
    }

    private void removeDownloadTask(String url) {
        synchronized (mTasks) {
            mTasks.remove(url);
        }
    }

    private void addDownloadTask(String url, DownloadTask downloadTask) {
        synchronized (mTasks) {
            mTasks.put(url, downloadTask);
        }
    }


    public static interface IDownloadListener {
        void onDownloadStart(String url, String filePath);

        void onDownloadSuccess(String url, String filePath);

        void onDownloadError(String url, String filePath);

        void onDownloadCancel(String url, String filePath);

        void onDownloadProgress(String url, String filePath, long currentOffset, long totalLength);
    }
}
