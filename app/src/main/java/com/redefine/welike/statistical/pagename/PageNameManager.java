package com.redefine.welike.statistical.pagename;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nianguowang on 2018/4/27
 */
public enum PageNameManager {

    INSTANCE;

    private volatile Map<String, String> mPageNameMap = new ConcurrentHashMap<>();

    public synchronized void init(final Context context) {
        final INameParser activityParser = new ActivityNameParser();
        Map<String, String> activityNames = activityParser.parse(context);
        mPageNameMap.putAll(activityNames);
    }

    public synchronized String getActivityAlias(String activityName) {
        if(mPageNameMap.containsKey(activityName)) {
            return mPageNameMap.get(activityName);
        }
        return activityName;
    }
}
