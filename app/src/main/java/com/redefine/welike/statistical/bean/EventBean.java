package com.redefine.welike.statistical.bean;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class EventBean {

    public JSONObject commonFiled;
    public String eventId;
    public JSONObject eventInfo;

    public EventBean(JSONObject commonFiled, String eventId, JSONObject eventInfo) {
        this.commonFiled = commonFiled;
        this.eventId = eventId;
        this.eventInfo = eventInfo;
    }

    @Override
    public String toString() {
        JSONObject parent = (JSONObject) commonFiled.clone();
        try {
            parent.put("event_id", eventId);
            if (eventInfo != null) {
                parent.put("event_info", eventInfo);
            }
        } catch (JSONException ignored) {
        }
        return parent.toString();
    }
}
