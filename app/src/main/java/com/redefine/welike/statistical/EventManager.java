package com.redefine.welike.statistical;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.Switcher;
import com.redefine.welike.base.dao.eventlog.EventEntity;
import com.redefine.welike.base.dao.eventlog.EventStore;
import com.redefine.welike.statistical.bean.EventBean;
import com.redefine.welike.statistical.http.AsyncEventRequest;
import com.redefine.welike.statistical.http.BaseEventRequest;
import com.redefine.welike.statistical.manager.InstallManager;
import com.redefine.welike.statistical.pagename.PageNameManager;
import com.redefine.welike.statistical.task.AmountUploadRunnable;
import com.redefine.welike.statistical.task.CacheUploadRunnable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;

public class EventManager implements AsyncEventRequest.EventCallback {

    public static final int TYPE_CACHE = 1;
    public static final int TYPE_AMOUNT = 2;

    private Context mContext;
    private FieldGenerator mFieldGenerator;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    private ExecutorService mSingleService = Executors.newSingleThreadExecutor();
    private CacheUploadRunnable mCacheRunnable;
    private AmountUploadRunnable mAmountRunnable;

    private EventManager() {}

    @Override
    public void onFailure(BaseEventRequest request, Exception e) {

    }

    @Override
    public void onResponse(BaseEventRequest request, Response result) throws Exception {

    }

    private static class EventManagerHolder {
        private static EventManager INSTANCE = new EventManager();
        private EventManagerHolder() {}
    }

    public static EventManager getInstance() {
        return EventManagerHolder.INSTANCE;
    }

    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be null!");
        }
        mContext = context.getApplicationContext();

        mSingleService.submit(new Runnable() {
            @Override
            public void run() {
                mFieldGenerator = new FieldGenerator(mContext);
                EventStore.INSTANCE.init(mContext);
                PageNameManager.INSTANCE.init(mContext);
                InstallManager.INSTANCE.registerReceiver(mContext);
                InstallManager.INSTANCE.checkNewInstall(mContext);

                mCacheRunnable = new CacheUploadRunnable();
                mExecutorService.submit(mCacheRunnable);
                mAmountRunnable = new AmountUploadRunnable(mContext);
                mExecutorService.submit(mAmountRunnable);
            }
        });
    }

    public void addEvent(final String eventId, final JSONObject eventInfo) {
        mSingleService.submit(new Runnable() {
            @Override
            public void run() {
                final EventBean bean = new EventBean(FieldGenerator.generate(mContext), eventId, eventInfo);
                EventStore.INSTANCE.insert(new EventEntity(CommonHelper.generateUUID(), bean.toString(), TYPE_AMOUNT, 0, 0));
                if (Switcher.EVENT_REPORT_AWS) {
                    EventStore.INSTANCE.insert(new EventEntity(CommonHelper.generateUUID(), bean.toString(), TYPE_AMOUNT, 0, 1));
                }
            }
        });
    }

    public void addEvent(final List<EventEntity> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        mSingleService.submit(new Runnable() {
            @Override
            public void run() {
                for (EventEntity entity : list) {
                    EventStore.INSTANCE.insert(entity);
                }
            }
        });
    }

    public void deleteEvent(final List<EventEntity> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        mSingleService.submit(new Runnable() {
            @Override
            public void run() {
                for (EventEntity entity : list) {
                    EventStore.INSTANCE.delete(entity);
                }
            }
        });
    }

    public void addRuntimeEvent(final String eventId, final JSONObject eventInfo) {
        mSingleService.submit(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("wng", "实时上传日志");
                final EventBean bean = new EventBean(FieldGenerator.generate(mContext), eventId, eventInfo);
                sendEvent(bean);
            }
        });
    }

    private void sendEvent(final EventBean event) {
        AsyncEventRequest request = new AsyncEventRequest();
        JSONArray jsonArray = new JSONArray();
        JSONObject parse = (JSONObject) JSONObject.parse(event.toString());
        jsonArray.add(parse);
        try {
            request.asyncSend(jsonArray, new AsyncEventRequest.EventCallback() {
                @Override
                public void onFailure(BaseEventRequest request, Exception e) {
                    LogUtil.d("wng", "实时上传日志fail");
                    mSingleService.submit(new Runnable() {
                        @Override
                        public void run() {
                            EventStore.INSTANCE.insert(new EventEntity(CommonHelper.generateUUID(), event.toString(), TYPE_CACHE, 0, 0));
                        }
                    });
                }

                @Override
                public void onResponse(BaseEventRequest request, Response result) throws Exception {
                    LogUtil.d("wng", "实时上传日志success");
                    if (!result.isSuccessful()) {
                        mSingleService.submit(new Runnable() {
                            @Override
                            public void run() {
                                EventStore.INSTANCE.insert(new EventEntity(CommonHelper.generateUUID(), event.toString(), TYPE_CACHE, 0, 0));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        InstallManager.INSTANCE.unRegisterReceiver(mContext);
        if (mCacheRunnable != null) {
            mCacheRunnable.stop();
            mCacheRunnable = null;
        }
        if (mAmountRunnable != null) {
            mAmountRunnable.stop();
            mAmountRunnable = null;
        }
        mExecutorService.shutdown();
        mSingleService.shutdown();
    }
}
