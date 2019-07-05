package com.redefine.welike.business.publisher.management.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HotTopics {

    public static class HotTopic {
        public String id;
        public String topicName;
        public String bannerUrl;
        public String description;
    }


    public static List<HotTopic> parse(JSONObject result) {
        try {
            List<HotTopic> list = new ArrayList<>();
            if (result.containsKey("list")) {
                JSONArray jsonArray = result.getJSONArray("list");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HotTopic item = new HotTopic();
                    item.id = jsonObject.getString("id");
                    item.topicName = jsonObject.getString("topicName");
                    item.bannerUrl = jsonObject.getString("bannerUrl");
                    item.description = jsonObject.getString("description");
                    list.add(item);
                }
            }
            return list;
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }
}
