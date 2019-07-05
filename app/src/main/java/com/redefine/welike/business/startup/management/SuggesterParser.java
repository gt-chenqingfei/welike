package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.business.startup.management.bean.RecommondUser;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.List;

public class SuggesterParser {

    public static final String KEY_USER_LIST = "list";
    public static final String KEY_USER_TAG = "tag";
    public static final String KEY_USER_VALUE = "value";
    public static final String KEY_USER_USERS = "users";

    public static ReferrerInfo parserReferrer(JSONObject result) {
        ReferrerInfo info = null;
        if (result != null && result.containsKey("referrer")) {
            info = new ReferrerInfo();
            JSONObject referrerJO = result.getJSONObject("referrer");
            if (referrerJO != null) {
                info.name = referrerJO.getString("nickName");
                info.avatar = referrerJO.getString("avatarUrl");
                info.toast = result.getString("referrerMsg");
                info.vip = 0;
                try {
                    info.vip = referrerJO.getInteger("vip");
                } catch (Exception e) {
                }
            }
        }
        return info;
    }

    public static List<RecommondUser> parser(JSONObject result) {
        List<RecommondUser> users = null;
        if (result != null) {
            JSONArray usersJSON = result.getJSONArray(KEY_USER_LIST);
            users = parseToUsers(usersJSON);
        }
        return users;
    }

    private static List<RecommondUser> parseToUsers(JSONArray userArray) {
        List<RecommondUser> recommondUsers = null;
        if (userArray != null && userArray.size() > 0) {
            recommondUsers = new ArrayList<>();
            for (int i = 0; i < userArray.size(); i++) {
                JSONObject jsonObject = userArray.getJSONObject(i);
                if (jsonObject != null) {
                    RecommondUser recommondUser = new RecommondUser();

                    JSONObject tagObject = jsonObject.getJSONObject(KEY_USER_TAG);
                    String tag = parseTag(tagObject);

                    JSONArray users = jsonObject.getJSONArray(KEY_USER_USERS);
                    List<User> userList = parseUserList(users);

                    recommondUser.tagName = tag;
                    recommondUser.userList = userList;
                    recommondUsers.add(recommondUser);
                }
            }
        }
        return recommondUsers;
    }

    private static String parseTag(JSONObject tagObject) {
        if (tagObject != null) {
            return tagObject.getString(KEY_USER_VALUE);
        }
        return null;
    }

    private static List<User> parseUserList(JSONArray userArray) {
        List<User> list = new ArrayList<>();
        if (userArray != null && userArray.size() > 0) {
            for (int i = 0; i < userArray.size(); i++) {
                JSONObject jsonObject = userArray.getJSONObject(i);
                User user = UserParser.parseUser(jsonObject);
                list.add(user);
            }
        }
        return list;
    }
}
