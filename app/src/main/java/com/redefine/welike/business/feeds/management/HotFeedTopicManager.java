package com.redefine.welike.business.feeds.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.HotFeedTopicRequest;
import com.redefine.welike.business.browse.management.request.HotFeedTopicRequest1;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class HotFeedTopicManager {

    private HotFeedTopicCallback mListener;

    public static interface HotFeedTopicCallback {
        void onHotFeedTopicCallback(List<TopicSearchSugBean.TopicBean> topicBeans, int errCode);
    }

    public void setListener(HotFeedTopicCallback callback) {
        mListener = callback;
    }

    public void request(boolean auth) {
        BaseRequest request;
        if (auth) {
            request = new HotFeedTopicRequest(MyApplication.getAppContext());
        } else {
            request = new HotFeedTopicRequest1(MyApplication.getAppContext());
        }
        try {
            request.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    int realErrCode = errCode;
                    if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                        realErrCode = ErrorCode.ERROR_POST_NOT_FOUND;
                    }
                    final int resultCode = realErrCode;
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onHotFeedTopicCallback(null, resultCode);
                            }
                        }
                    });

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    JSONArray jsonArray = result.getJSONArray("list");
                    int length = jsonArray.size();
                    final List<TopicSearchSugBean.TopicBean> list = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        TopicSearchSugBean.TopicBean bean = TopicSearchSugBean.TopicBean.parse(jsonArray.getJSONObject(i));
                        if (bean != null) {
                            list.add(bean);
                        }
                    }
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onHotFeedTopicCallback(list, ErrorCode.ERROR_SUCCESS);
                            }
                        }
                    });
                }

            });
        } catch (final Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onHotFeedTopicCallback(null, ErrorCode.networkExceptionToErrCode(e));
                    }
                }
            });
        }
    }

}
