package com.redefine.welike.business.feeds.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by mengnan on 2018/5/10.
 **/
public class LoopRequest extends BaseRequest {
    private static final String FEEDS_KEY_COUNT_ONE_PAGE = "count";
    private static final String FEEDS_KEY_CURSOR = "cursor";
    private static final String FEEDS_KEY_ORDER = "order";
    public static final int HOME_FEEDS = 1;
    public static final int LATEST_FEEDS = 2;
    private int feedsType;
    private boolean timeOrder;

    public LoopRequest(int feedsType, boolean timeOrder, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        this.feedsType = feedsType;
        this.timeOrder = timeOrder;
    }

    public void tryFeeds(String cursor, RequestCallback callback) throws Exception {
        if (feedsType == HOME_FEEDS) {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                setHost("feed/user/" + account.getUid() + "/feeds", true);
                setParam(FEEDS_KEY_COUNT_ONE_PAGE, GlobalConfig.LOOP_GET_NEW);
                if (!TextUtils.isEmpty(cursor)) {
                    setParam(FEEDS_KEY_CURSOR, cursor);
                }
                if (timeOrder) {
                    setParam(FEEDS_KEY_ORDER, "created");
                }
            }
        } else if (feedsType == LATEST_FEEDS) {
            setHost("feed/posts", true);
            setParam(FEEDS_KEY_COUNT_ONE_PAGE, GlobalConfig.LOOP_GET_NEW);
            if (!TextUtils.isEmpty(cursor)) {
                setParam(FEEDS_KEY_CURSOR, cursor);
            }
        }
        req(callback);
    }
}
