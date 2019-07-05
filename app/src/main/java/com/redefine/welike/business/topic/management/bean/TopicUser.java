package com.redefine.welike.business.topic.management.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/11.
 */

public class TopicUser {

    public long time;
    public User user;
    public String topicId;
    public boolean isEmcee = false;

    public static List<TopicUser> parse(JSONObject result) {
        JSONArray usersJSON = null;
        try {
            usersJSON = result.getJSONArray("list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<TopicUser> topicUsers = null;
        if (usersJSON != null && usersJSON.size() > 0) {
            topicUsers = new ArrayList<>();
            for (int i = 0; i < usersJSON.size(); i++) {
                try {
                    JSONObject userJSON = usersJSON.getJSONObject(i);
                    User u = UserParser.parseUser(userJSON);
                    if (u == null) {
                        continue;
                    }
                    long passTime = userJSON.getLongValue("recentPostTime");
                    TopicUser user = new TopicUser();
                    user.time = passTime;
                    user.user = u;
                    try {
                        if (i == 0) {
                            user.isEmcee = userJSON.getBoolean("emcee");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    topicUsers.add(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return topicUsers;
    }
}
