package com.redefine.welike.business.publisher.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.dao.welike.TopicSearchHistory;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.publisher.management.cache.TopicSearchHistoryCache;
import com.redefine.welike.business.publisher.management.request.TopicSearchSugRequest;
import com.redefine.welike.business.publisher.management.request.TopicSearchSugRequest1;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/12.
 */

public class TopicSugManager {
    private BaseRequest searchSugRequest;

    public void inputKeyword(final String keyword, final TopicSugManagerCallback callback) {
        if (searchSugRequest != null) {
            searchSugRequest.cancel();
            searchSugRequest = null;
        }
        if (!AccountManager.getInstance().isLogin()) {
            searchSugRequest = new TopicSearchSugRequest1(keyword, MyApplication.getAppContext());
        } else {
            searchSugRequest = new TopicSearchSugRequest(keyword, MyApplication.getAppContext());
        }
        try {
            searchSugRequest.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, final int errCode) {
                    if (request != searchSugRequest) {
                        return;
                    }
                    searchSugRequest = null;
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSugResult(keyword, null, errCode);
                            }
                        }

                    });
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (request != searchSugRequest) {
                        return;
                    }
                    searchSugRequest = null;
                    final TopicSearchSugBean sugResult = parseSearchResult(result);
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSugResult(keyword, sugResult, ErrorCode.ERROR_SUCCESS);
                            }
                        }

                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            searchSugRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onSugResult(keyword, null, errCode);
                    }
                }

            });
        }
    }

    private TopicSearchSugBean parseSearchResult(JSONObject result) {
        return TopicSearchSugBean.parse(result);
    }

    public interface TopicSugManagerCallback {

        void onSugResult(String query, TopicSearchSugBean results, int errCode);

    }

    public void deleteHistory(String keyword) {
        TopicSearchHistoryCache.getInstance().delete(keyword);
    }

    public List<TopicSearchHistory> listAllHistory() {
        return TopicSearchHistoryCache.getInstance().listAll();
    }

    public int getAllHistoryCount() {
        return TopicSearchHistoryCache.getInstance().getAllCount();
    }

    public void insert(TopicSearchSugBean.TopicBean keyword) {
        TopicSearchHistoryCache.getInstance().insert(keyword);
    }

    public void cleanAllHistory() {
        TopicSearchHistoryCache.getInstance().cleanAll();
    }

}
