package com.redefine.welike.statistical.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.dao.eventlog.EventEntity;
import com.redefine.welike.base.dao.eventlog.EventStore;
import com.redefine.welike.statistical.Config;
import com.redefine.welike.statistical.EventManager;
import com.redefine.welike.statistical.http.SyncEventRequest;

import java.util.List;

/**
 * Created by nianguowang on 2018/5/3
 */
public class CacheUploadRunnable extends BaseStrategyRunnable {

    private SyncEventRequest request;
    private JSONArray jsonArray;

    public CacheUploadRunnable() {
        request = new SyncEventRequest();
        jsonArray = new JSONArray();
    }

    @Override
    public void run() {
        mStop = false;
        while (!mStop) {
            List<EventEntity> entities = EventStore.INSTANCE.queryCacheEvent(Config.UPLOAD_COUNT_CACHE);
            if(!CollectionUtil.isEmpty(entities)) {
                jsonArray.clear();
                for (EventEntity entity : entities) {
                    String eventInfo = entity.getEventInfo();
                    JSONObject parse = (JSONObject) JSONObject.parse(eventInfo);
                    filter(parse);
                    jsonArray.add(parse);
                    EventStore.INSTANCE.delete(entity);
                }

                try {
                    boolean success = request.sendEvent(jsonArray);
                    if(!success) {
                        EventManager.getInstance().addEvent(entities);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            try {
                Thread.sleep(Config.UPLOAD_DURATION_CACHE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
