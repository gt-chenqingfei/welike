package com.redefine.welike.business.publisher.management.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchSugBean {

    public TopicBean newTopic;
    public List<TopicBean> mTopics = new ArrayList<>();


    public static class TopicBean implements Serializable {

        private static final long serialVersionUID = -6121080745716120080L;
        public String id;
        public String name;
        public int count;

        public TopicBean() {

        }

        public TopicBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public static TopicBean parse(JSONObject jsonObject) {
            try {
                TopicBean topicBean = new TopicBean();
                topicBean.id = jsonObject.getString("id");
                topicBean.name = jsonObject.getString("topicName");
                return topicBean;
            } catch (Exception e) {
                // do nothing
            }
            return null;
        }
    }


    public static TopicSearchSugBean parse(JSONObject result) {
        if (result == null) {
            return null;
        }
        String newTopic = "";
        try {
            newTopic = result.getString("newTopic");
        } catch (Exception e) {
            // do nothing
        }
        JSONArray recommend = null;
        try {
            recommend = result.getJSONArray("topics");
        } catch (Exception e) {
            // do nothing
        }
        if (TextUtils.isEmpty(newTopic) && CollectionUtil.isEmpty(recommend)) {
            return null;
        }
        TopicSearchSugBean searchSugBean = new TopicSearchSugBean();

        boolean hasTopic = false;

        if (!CollectionUtil.isEmpty(recommend)) {
            List<TopicBean> topicBeans = new ArrayList<>();
            int size = recommend.size();
            TopicBean temp;
            JSONObject jsonObject;
            for (int i = 0; i < size; i++) {
                jsonObject = recommend.getJSONObject(i);
                temp = TopicBean.parse(jsonObject);
                if (temp != null) {
                    if (TextUtils.equals(temp.name, newTopic)) {
                        hasTopic = true;
                    }
                    topicBeans.add(temp);
                }
            }
            searchSugBean.mTopics.addAll(topicBeans);
        }

        if (!TextUtils.isEmpty(newTopic) && !hasTopic) {
            searchSugBean.newTopic = new TopicBean();
            searchSugBean.newTopic.name = newTopic;
            searchSugBean.newTopic.id = newTopic.hashCode() + "";//todo 暂时只能这么做，后续处理
        }

        return searchSugBean;
    }
}
