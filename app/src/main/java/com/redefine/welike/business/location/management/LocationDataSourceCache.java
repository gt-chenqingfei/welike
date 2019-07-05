package com.redefine.welike.business.location.management;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.business.feeds.management.bean.PostBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengnan on 2018/5/31.
 **/
public class LocationDataSourceCache {

    private final List<PostBase> mHotPostList = new ArrayList<>();

    private final List<PostBase> mLatestPostList = new ArrayList<>();

    public List<PostBase> getHotPostList() {
        return mHotPostList;
    }

    public List<PostBase> getLatestPostList() {
        return mLatestPostList;
    }

    public void addHotHisData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mHotPostList.addAll(feeds);
    }

    public void addHotNewData(List<PostBase> feeds) {
        mHotPostList.clear();
        if(CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mHotPostList.addAll(feeds);
    }

    public void addHotFeed(PostBase feed) {
        if (feed == null) {
            return;
        }
        mHotPostList.add(0, feed);
    }

    public void addLatestHisData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mLatestPostList.addAll(feeds);
    }

    public void addLatestNewData(List<PostBase> feeds) {
        mLatestPostList.clear();
        if(CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mLatestPostList.addAll(feeds);
    }

    public void addLatestFeed(PostBase feed) {
        if (feed == null) {
            return;
        }
        mLatestPostList.add(0, feed);
    }
}
