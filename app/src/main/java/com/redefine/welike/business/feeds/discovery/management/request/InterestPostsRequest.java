package com.redefine.welike.business.feeds.discovery.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.statistical.utils.GoogleUtil;

/**
 * Created by mengnan on 2018/6/14.
 **/
public class InterestPostsRequest extends BaseRequest {
    private static final String FEEDS_KEY_COUNT_ONE_PAGE = "count";
    private static final String FEEDS_KEY_CURSOR = "cursor";
    private static final String INTEREST_ID = "interestId";

    private String id;

    public InterestPostsRequest(String id, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        this.id = id;
    }

    public void tryFeeds(final String cursor, final RequestCallback callback) throws Exception {
        new Thread() {
            @Override
            public void run() {
                setHost("leaderboard/interest/24h", true);
                setParam(INTEREST_ID, id);
                setParam(FEEDS_KEY_COUNT_ONE_PAGE, GlobalConfig.POSTS_NUM_ONE_PAGE);
                setParam("gid", GoogleUtil.INSTANCE.forceGAID());
                if (!TextUtils.isEmpty(cursor)) {
                    setParam(FEEDS_KEY_CURSOR, cursor);
                }
                try {
                    req(callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
