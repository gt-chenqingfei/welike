package com.redefine.welike.statistical.manager;

import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nianguowang on 2018/5/4
 */
public enum PostEventManager {

    INSTANCE;

    private String type;
    private String oldType;
    private int action = EventConstants.AUTO_REFRESH;
    private Set<String> mPostSet = new HashSet<>();

    public void addPost(String postId) {
        mPostSet.add(postId);
    }

    public int getPostCount() {
        return mPostSet.size();
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setOldType(String type) {
        this.oldType = type;
    }

    public void setOldType(int type) {
        this.oldType = String.valueOf(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void sendEventStrategy(List<PostBase> postBases) {
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_LOAD_PAGE);
        int count = CollectionUtil.getCount(postBases);
        if (CollectionUtil.isEmpty(postBases)) {
            sendEvent();
            return;
        }
        String sequenceId = null;
        int hot = 0, operation = 0, newCount = 0;
        for (PostBase postBase : postBases) {
            sequenceId = postBase.getSequenceId();
            if (TextUtils.equals(postBase.getStrategy(), "hot")) {
                hot++;
            } else if (TextUtils.equals(postBase.getStrategy(), "operation")) {
                operation++;
            } else if (TextUtils.equals(postBase.getStrategy(), "new")) {
                newCount++;
            }
        }
        switch (action) {
            case EventConstants.PULL_DOWN_REFRESH:
                EventLog1.FeedRefresh.report1(type, count, hot, operation, newCount, FeedHelper.getDataForEvent(postBases).toString(), sequenceId);
                EventLog.Feed.report1(oldType, count, 0, hot, operation, newCount, sequenceId);
                break;
            case EventConstants.PULL_UP_REFRESH:
                EventLog1.FeedRefresh.report2(type, count, hot, operation, newCount, FeedHelper.getDataForEvent(postBases).toString(), sequenceId);
                EventLog.Feed.report2(oldType, count, 0, hot, operation, newCount, sequenceId);
                break;
            case EventConstants.AUTO_REFRESH:
                EventLog1.FeedRefresh.report3(type, count, hot, operation, newCount, FeedHelper.getDataForEvent(postBases).toString(), sequenceId);
                EventLog.Feed.report3(oldType, count, 0, hot, operation, newCount, sequenceId);
                break;
            case EventConstants.CLICK_BUTTON_REFRESH:
                EventLog1.FeedRefresh.report4(type, count, hot, operation, newCount, FeedHelper.getDataForEvent(postBases).toString(), sequenceId);
                EventLog.Feed.report4(oldType, count, 0, hot, operation, newCount, sequenceId);
                break;
        }
    }

    private void sendEvent() {
        switch (action) {
            case EventConstants.PULL_DOWN_REFRESH:
                EventLog1.FeedRefresh.report1(type, 0, 0, 0, 0, null, null);
                EventLog.Feed.report1(oldType, 0, 0 , 0, 0, 0, null);
                break;
            case EventConstants.PULL_UP_REFRESH:
                EventLog1.FeedRefresh.report2(type, 0, 0, 0, 0, null, null);
                EventLog.Feed.report2(oldType, 0, 0 , 0, 0, 0, null);
                break;
            case EventConstants.AUTO_REFRESH:
                EventLog1.FeedRefresh.report3(type, 0, 0, 0, 0, null, null);
                EventLog.Feed.report3(oldType, 0, 0 , 0, 0, 0, null);
                break;
            case EventConstants.CLICK_BUTTON_REFRESH:
                EventLog1.FeedRefresh.report4(type, 0, 0, 0, 0, null, null);
                EventLog.Feed.report4(oldType, 0, 0 , 0, 0, 0, null);
                break;
        }
    }

    public void reset() {
        mPostSet.clear();
        type = "";
        action = EventConstants.AUTO_REFRESH;
    }

    public static int getPostType(PostBase postBase) {
        if (postBase == null) {
            return 0;
        }
        int type = postBase.getType();
        if (type == PostBase.POST_TYPE_FORWARD && postBase instanceof ForwardPost) {
            ForwardPost post = (ForwardPost) postBase;
            PostBase rootPost = post.getRootPost();
            if (rootPost != null) {
                type = rootPost.getType();
            } else {
                type = 0;
            }
        }
        return type;
    }


    public static String getPostRootUid(PostBase postBase) {
        if (postBase == null) {
            return "";
        }
        String uid = postBase.getUid();
        if (postBase instanceof ForwardPost) {
            ForwardPost post = (ForwardPost) postBase;
            PostBase rootPost = post.getRootPost();
            if (rootPost != null) {
                uid = rootPost.getUid();
            }
        }
        return uid;
    }


    public static String getPostRootPid(PostBase postBase) {
        if (postBase == null) {
            return "";
        }
        String pid = postBase.getPid();
        if (postBase instanceof ForwardPost) {
            ForwardPost post = (ForwardPost) postBase;
            PostBase rootPost = post.getRootPost();
            if (rootPost != null) {
                pid = rootPost.getPid();
            }
        }
        return pid;
    }

}
