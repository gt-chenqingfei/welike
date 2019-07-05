package com.redefine.welike.business.common;

import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liubin on 2018/3/21.
 */

public class UsersProviderBase {
    protected final Map<String, User> cacheList = new ConcurrentHashMap<>();
    protected final Map<String, User> onePageList = new ConcurrentHashMap<>();

    protected List<User> filterUsers(List<User> source) {
        List<User> targetList;
        if (cacheList.size() > 0) {
            targetList = filterUsers(source, cacheList);
        } else {
            targetList = source;
        }
        return targetList;
    }

    protected void cacheFirstPage(List<User> source) {
        onePageList.clear();
        if (source != null && source.size() > 0) {
            for (int i = 0; i < source.size(); i++) {
                User user = source.get(i);
                onePageList.put(user.getUid(), user);
            }
        }
    }

    protected int refreshNewCount(List<User> source) {
        if (source != null && source.size() > 0) {
            int count = source.size();
            for (User u : source) {
                if (onePageList.containsKey(u.getUid())) {
                    count--;
                }
            }
            if (count < 0) count = 0;
            return count;
        } else {
            return 0;
        }
    }

    private static List<User> filterUsers(List<User> source, Map<String, User> filter) {
        List<User> target = new ArrayList<>();
        for (User u : source) {
            if (!filter.containsKey(u.getUid())) {
                target.add(u);
                filter.put(u.getUid(), u);
            }
        }
        return target;
    }
}
