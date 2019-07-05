package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.request.TopicCardRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengnan on 2018/5/28.
 **/
public class TopicCardsManager {
    private TopicCardRequest request;

    public interface TopicCardsCallback {

        void onTopicCardsDone(List<String> list, int errorCode);

    }

    private TopicCardsCallback mCallback;

    public void reqTopicCards(TopicCardsCallback callback) {
        if (request != null) return;
        this.mCallback = callback;
        request = new TopicCardRequest(BaseHttpReq.REQUEST_METHOD_GET, MyApplication.getAppContext());
        try {
            request.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    handleError(request, errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    handleSuccess(request, result);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleError(BaseRequest request, final int errCode) {
        if (this.request == request) {
            this.request = null;
            if (mCallback != null) {
                mCallback.onTopicCardsDone(null, errCode);
            }
        }
    }

    private void handleSuccess(BaseRequest request, JSONObject result) {
        if (this.request == request) {
            this.request = null;
            final List<String> infos = new ArrayList<>();
            if (result != null) {
                JSONArray topicCardJSON = result.getJSONArray("list");
                if (topicCardJSON != null && topicCardJSON.size() > 0) {
                    for (int i = 0; i < topicCardJSON.size(); i++) {
                        String info = topicCardJSON.getString(i);
                        infos.add(info);
                    }
                }

            }
            if (mCallback != null) {
                mCallback.onTopicCardsDone(infos, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

}
