package com.redefine.welike.business.feeds.management;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.business.feeds.management.bean.PostBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nianguowang on 2018/4/19
 */
public class DiscoverDataSourceCache {

    private final List<PostBase> mHotPostList = new ArrayList<>();

    private final List<PostBase> mLatestPostList = new ArrayList<>();

    public Map<String, List<PostBase>> getInterestPostsMap() {
        return mInterestPostsMap;
    }

    private final Map<String, List<PostBase>> mInterestPostsMap = new ConcurrentHashMap<>();


    private final Map<String, List<PostBase>> mCachePostsMap = new ConcurrentHashMap<>();
    private final Map<String, List<PostBase>> mOnpageCachePostsMap = new ConcurrentHashMap<>();


    public List<PostBase> getHotPostList() {
        return mHotPostList;
    }

    public List<PostBase> getLatestPostList() {
        return mLatestPostList;
    }

    public List<PostBase> getInterestPostList(String id) {
        return mInterestPostsMap.get(id);
    }

    public void addHotHisData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mHotPostList.addAll(feeds);
    }

    public void addHotNewData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mHotPostList.clear();
        mHotPostList.addAll(feeds);
    }

    public void addNewInterestsPost(String id, List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        List<PostBase> postBases = mInterestPostsMap.get(id);
        if (postBases == null) {
            postBases = new ArrayList<>();
        }
        postBases.clear();
        postBases.addAll(feeds);
        mInterestPostsMap.put(id, postBases);
    }

    public void addHistoryInterestsPost(String id, List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        List<PostBase> postBases = mInterestPostsMap.get(id);
        if (postBases == null) {
            postBases = new ArrayList<>();
        }
        postBases.addAll(feeds);
    }

    public void addLatestHisData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mLatestPostList.addAll(feeds);
    }

    public void addLatestNewData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mLatestPostList.clear();
        mLatestPostList.addAll(feeds);
    }


    public Map<String, List<PostBase>> getCachePostsMap() {
        return mCachePostsMap;
    }

    public Map<String, List<PostBase>> getOnePageCachePostsMap() {
        return mOnpageCachePostsMap;
    }

    public List<PostBase> filterPosts(List<PostBase> source, String tag) {
        List<PostBase> targetList;
        if (!CollectionUtil.isEmpty(mCachePostsMap.get(tag))) {
            targetList = filter(source, mCachePostsMap.get(tag));
        } else {
            targetList = source;
        }
        cacheOnePage(source, tag);
        return targetList;
    }


    private List<PostBase> filter(List<PostBase> source, List<PostBase> filter) {

        Map<String, PostBase> tempMap = new ConcurrentHashMap<>();

        for (PostBase p : filter) {
            tempMap.put(p.getPid(), p);
        }

        List<PostBase> target = new ArrayList<>();

        for (PostBase p : source) {
            if (!tempMap.containsKey(p.getPid())) {
                target.add(p);
            }
        }
        return target;
    }


    public int refreshNewCount(List<PostBase> source, List<PostBase> filter) {
        if (CollectionUtil.isEmpty(filter)) {
            return source.size();
        } else {
            Map<String, PostBase> tempMap = new ConcurrentHashMap<>();
            for (PostBase p : filter) {
                tempMap.put(p.getPid(), p);
            }
            if (source != null && source.size() > 0) {
                int count = source.size();
                for (PostBase p : source) {
                    if (tempMap.containsKey(p.getPid())) {
                        count--;
                    }
                }
                if (count < 0) count = 0;
                return count;
            } else {
                return 0;
            }
        }
    }

    public void cacheOnePage(List<PostBase> source, String tag) {

        List<PostBase> postBases = mCachePostsMap.get(tag);
        if (postBases == null) {
            postBases = new ArrayList<>();
        }
        postBases.clear();
        postBases.addAll(source);
        mCachePostsMap.put(tag, postBases);
    }
}
