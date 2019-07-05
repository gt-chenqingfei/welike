package com.redefine.welike.business.supertopic.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.supertopic.management.bean.SuperTopicDetailInfo;
import com.redefine.welike.business.supertopic.management.request.SuperTopicDetailRequest;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SuperTopicDetailManager extends SingleListenerManagerBase implements RequestCallback {
    private SuperTopicDetailRequest detailRequest;

    public void setListener(SuperTopicDetailInfoCallback callback) {
        super.setListener(callback);
    }

    public void superTopicInfo(String topicId) {
        detailRequest = new SuperTopicDetailRequest(MyApplication.getAppContext());
        try {
            detailRequest.detail(topicId, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (detailRequest == request) {
            callSuperTopicDetailInfoFailed(errCode);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (detailRequest == request) {

            int feedCount = 0;
            String photo = null;
            String topicName = null;
            String topicId = null;
            String topicIntroduce = null;
            JSONArray topPosts = null;

            try {
                topPosts = result.getJSONArray("posts");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                photo = result.getString("bannerUrl");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                topicId = result.getString("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                topicName = result.getString("name");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                topicIntroduce = result.getString("description");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                feedCount = result.getInteger("postsCount");
            } catch (Exception e) {
                e.printStackTrace();
            }

            SuperTopicDetailInfo superTopicDetailInfo = new SuperTopicDetailInfo();
            superTopicDetailInfo.tid = topicId;
            superTopicDetailInfo.topicName = topicName;
            superTopicDetailInfo.postsCount = feedCount;
            superTopicDetailInfo.topicInfo = topicIntroduce;
            superTopicDetailInfo.bannerUrl = photo;
            if (topPosts != null && topPosts.size() > 0) {
                superTopicDetailInfo.topPosts = new ArrayList<>();
                for (int i = 0; i < topPosts.size(); i++) {
                    JSONObject p = topPosts.getJSONObject(i);
                    PostBase post = PostsDataSourceParser.parsePostBase(p);
                    superTopicDetailInfo.topPosts.add(post);
                }
            }

            callSuperTopicDetailInfoCompleted(superTopicDetailInfo);
        }
    }

    public interface SuperTopicDetailInfoCallback {

        void onSuperTopicDetailInfoSuccessed(SuperTopicDetailInfo info);
        void onSuperTopicDetailInfoError(int errorCode);

    }

    private SuperTopicDetailInfoCallback getCallback() {
        SuperTopicDetailInfoCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof SuperTopicDetailInfoCallback) {
            callback = (SuperTopicDetailInfoCallback)l;
        }
        return callback;
    }

    private void callSuperTopicDetailInfoCompleted(final SuperTopicDetailInfo topicInfo) {
        final SuperTopicDetailInfoCallback listener = getCallback();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.onSuperTopicDetailInfoSuccessed(topicInfo);
                }
            }

        });
    }

    private void callSuperTopicDetailInfoFailed(final int errCode) {
        final SuperTopicDetailInfoCallback listener = getCallback();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.onSuperTopicDetailInfoError(errCode);
                }
            }

        });
    }

}
