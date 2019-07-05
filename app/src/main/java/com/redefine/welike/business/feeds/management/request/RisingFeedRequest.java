package com.redefine.welike.business.feeds.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.foundation.utils.ChannelHelper;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import java.util.ArrayList;

/**
 * Created by honlin on 2018/11/29
 */
public class RisingFeedRequest extends BaseRequest {

    private static final String FEEDS_KEY_COUNT_ONE_PAGE = "count";
    private static final String FEEDS_KEY_CURSOR = "cursor";


    public RisingFeedRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

    }

    public void request(String cursor, ArrayList<String> interests, RequestCallback callback) throws Exception {
        setHost("leaderboard/rising", true);
        setParam(FEEDS_KEY_COUNT_ONE_PAGE, GlobalConfig.POSTS_NUM_ONE_PAGE);
        ArrayList<String> tmp = new ArrayList<>();

        if (interests != null) {
            tmp.addAll(interests);
        }
        ArrayList<String> tags = ChannelHelper.getTags(MyApplication.getAppContext());
        if (tags.size() > 0) {
            tmp.add("\"" + tags.get(0) + "\"");
        }
        setParam("interests", tmp);
        setParam("userType", CommonHelper.getChannel(MyApplication.getAppContext()));
        if (!TextUtils.isEmpty(cursor)) {
            setParam(FEEDS_KEY_CURSOR, cursor);
        }
        String adgroup = ChannelHelper.tagGroup;
        if (TextUtils.isEmpty(adgroup)){
            setParam("retpid", adgroup);
        }
        req(callback);
    }
}
