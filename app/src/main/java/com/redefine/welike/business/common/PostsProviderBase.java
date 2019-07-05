package com.redefine.welike.business.common;

import com.redefine.welike.business.feeds.management.bean.PostBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liubin on 2018/3/21.
 */

public abstract class PostsProviderBase {
    protected final Map<String, PostBase> cacheList = new ConcurrentHashMap<>();
    protected final Map<String, PostBase> onePageList = new ConcurrentHashMap<>();

    protected List<PostBase> filterPosts(List<PostBase> source) {
        List<PostBase> targetList;
        if (cacheList.size() > 0) {
            targetList = filterPosts(source, cacheList);
        } else {
            targetList = source;
        }
        return targetList;
    }

    protected void cacheFirstPage(List<PostBase> source) {
        onePageList.clear();
        if (source != null && source.size() > 0) {
            for (int i = 0; i < source.size(); i++) {
                PostBase postBase = source.get(i);
                onePageList.put(postBase.getPid(), postBase);
            }
        }
    }

    protected int refreshNewCount(List<PostBase> source) {
        if (source != null && source.size() > 0) {
            int count = source.size();
            for (PostBase p : source) {
                if (onePageList.containsKey(p.getPid())) {
                    count--;
                }
            }
            if (count < 0) count = 0;
            return count;
        } else {
            return 0;
        }
    }

    private static List<PostBase> filterPosts(List<PostBase> source, Map<String, PostBase> filter) {
        List<PostBase> target = new ArrayList<>();
        for (PostBase p : source) {
            if (!filter.containsKey(p.getPid())) {
                target.add(p);
                filter.put(p.getPid(), p);
            }
        }
        return target;
    }

}
