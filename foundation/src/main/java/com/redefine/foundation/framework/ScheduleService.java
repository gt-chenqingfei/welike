package com.redefine.foundation.framework;

import com.redefine.foundation.utils.CommonHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/3.
 */

public class ScheduleService extends BroadcastManagerBase {
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(8);
    private final ScheduledExecutorService serviceLine = Executors.newScheduledThreadPool(1);

    private final ConcurrentHashMap<String, ScheduledFuture> tasks = new ConcurrentHashMap<>();

    public interface RetryTask {

        boolean runTask();

    }

    public interface ScheduleTaskCallback {

        void onRetryTaskCompleted(String taskId);

    }

    private class RetryRunnable implements Runnable {
        private String taskId;
        private RetryTask task;

        private RetryRunnable(RetryTask task) {
            this.task = task;
            taskId = CommonHelper.generateUUID();
        }

        private String getTaskId() {
            return taskId;
        }

        @Override
        public void run() {
            boolean result = task.runTask();
            if (result) {
                ScheduleService.getInstance().cancel(taskId);
                broadcast(taskId);
            }
        }

    }

    private static class RetryServiceHolder {
        public static ScheduleService instance = new ScheduleService();
    }

    private ScheduleService() {}

    public static ScheduleService getInstance() { return RetryServiceHolder.instance; }

    public String startRetryInline(RetryTask task, long period, TimeUnit unit) {
        RetryRunnable taskRunnable = new RetryRunnable(task);
        String taskId = taskRunnable.getTaskId();
        ScheduledFuture taskFuture = serviceLine.scheduleWithFixedDelay(taskRunnable, 0, period, unit);
        tasks.put(taskId, taskFuture);
        return taskId;
    }

    public String startRetry(RetryTask task, long period, TimeUnit unit) {
        RetryRunnable taskRunnable = new RetryRunnable(task);
        String taskId = taskRunnable.getTaskId();
        ScheduledFuture taskFuture = service.scheduleWithFixedDelay(taskRunnable, 0, period, unit);
        tasks.put(taskId, taskFuture);
        return taskId;
    }

    public String startRepeat(Runnable task, long period, TimeUnit unit) {
        String taskId = CommonHelper.generateUUID();
        ScheduledFuture taskFuture = service.scheduleWithFixedDelay(task, 0, period, unit);
        tasks.put(taskId, taskFuture);
        return taskId;
    }

    public void cancel(String taskId) {
        ScheduledFuture future = tasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
    }

    public void register(ScheduleTaskCallback listener) {
        super.register(listener);
    }

    public void unregister(ScheduleTaskCallback listener) {
        super.unregister(listener);
    }

    private void broadcast(final String taskId) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof ScheduleTaskCallback) {
                            ScheduleTaskCallback listener = (ScheduleTaskCallback)l;
                            listener.onRetryTaskCompleted(taskId);
                        }
                    }
                }
            }

        });
    }

}
