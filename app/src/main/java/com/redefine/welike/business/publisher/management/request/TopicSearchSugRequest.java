package com.redefine.welike.business.publisher.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchSugRequest extends BaseRequest {
    private static final String SEARCH_KEY_QUERY = "query";
    private static final String SEARCH_KEY_PAGE_NUM = "pageNumber";
    private static final String SEARCH_KEY_COUNT_ONE_PAGE = "count";

    public TopicSearchSugRequest(String keyword, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("search/topic/name", true);
        setParam(SEARCH_KEY_QUERY, keyword);
        setParam(SEARCH_KEY_PAGE_NUM, 0);
        setParam(SEARCH_KEY_COUNT_ONE_PAGE, GlobalConfig.TOPIC_SUG_SEARCH_ONE_PAGE_NUM);
    }
}
