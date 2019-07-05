package com.redefine.welike.business.topic.management.manager;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.TopicInfoRequest;
import com.redefine.welike.business.browse.management.request.TopicInfoRequest2;
import com.redefine.welike.business.browse.management.request.TopicRelatedUserRequest;
import com.redefine.welike.business.browse.management.request.TopicRelatedUserRequest2;
import com.redefine.welike.business.topic.management.bean.TopicInfo;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwenbo on 2018/4/11.
 */

public class TopicInfoManager extends SingleListenerManagerBase implements RequestCallback {

    private BaseRequest mTopicInfoRequest;
    private BaseRequest mRelatedUserRequest;
    private String mTid;
    private TopicInfo mTopicInfo;
    private boolean isBrowse;

    public void refresh(String id, boolean isBrowse) {
        if (mTopicInfoRequest != null) return;
        this.isBrowse = isBrowse;
        mTopicInfo = null;
        this.mTid = id;
        if (!isBrowse)
            mTopicInfoRequest = new TopicInfoRequest(mTid, MyApplication.getAppContext());
        else
            mTopicInfoRequest = new TopicInfoRequest2(mTid, MyApplication.getAppContext());
        try {
            mTopicInfoRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            mTopicInfoRequest = null;
            callLBSNearInfoGetterFailed(ErrorCode.networkExceptionToErrCode(e));
        }
    }

    private void callLBSNearInfoGetterCompleted(final TopicInfo topicInfo, final List<User> userList) {
        final TopicInfoGetterListener listener = getCallback();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.onTopicInfoGetterCompleted(topicInfo, userList);
                }
            }

        });
    }


    private TopicInfoGetterListener getCallback() {
        TopicInfoGetterListener callback = null;
        Object l = getListener();
        if (l != null && l instanceof TopicInfoGetterListener) {
            callback = (TopicInfoGetterListener) l;
        }
        return callback;
    }

    private void callLBSNearInfoGetterFailed(final int errCode) {
        final TopicInfoGetterListener listener = getCallback();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.onTopicInfoGetterFailed(errCode);
                }
            }

        });
    }

    public void setListener(TopicInfoGetterListener listener) {
        super.setListener(listener);
    }


    @Override
    public void onError(BaseRequest request, int errCode) {
        if (mTopicInfoRequest == request) {
            mTopicInfoRequest = null;
            callLBSNearInfoGetterFailed(errCode);
        } else if (mRelatedUserRequest == request) {
            mRelatedUserRequest = null;
            callLBSNearInfoGetterFailed(errCode);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (mTopicInfoRequest == request) {
            mTopicInfoRequest = null;

            int feedCount = 0;
            int userCount = 0;
            String photo = null;
            String topicName = null;
            String topicId = null;
            String topicIntroduce = null;
            JSONObject topicInfo = null;
            JSONObject postInfo = null;

            try {
                postInfo = result.getJSONObject("postRes");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                topicInfo = result.getJSONObject("topicRes");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                feedCount = topicInfo.getIntValue("postsCount");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                userCount = topicInfo.getIntValue("usersCount");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                photo = topicInfo.getString("bannerUrl");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                topicId = topicInfo.getString("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                topicName = topicInfo.getString("topicName");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                topicIntroduce = topicInfo.getString("description");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTopicInfo = new TopicInfo();
            mTopicInfo.bannerUrl = photo;
            mTopicInfo.postsCount = feedCount;
            mTopicInfo.userCount = userCount;
            mTopicInfo.topicName = topicName;
            mTopicInfo.tid = topicId;
            mTopicInfo.topPost = postInfo;
            mTopicInfo.topicDetailInfo = topicIntroduce;

            if (!isBrowse)
                mRelatedUserRequest = new TopicRelatedUserRequest(topicId, MyApplication.getAppContext());
            else
                mRelatedUserRequest = new TopicRelatedUserRequest2(topicId, MyApplication.getAppContext());
            try {
                mRelatedUserRequest.req(this);
            } catch (Exception e) {
                e.printStackTrace();
                mRelatedUserRequest = null;
                callLBSNearInfoGetterFailed(ErrorCode.networkExceptionToErrCode(e));
            }
        } else if (mRelatedUserRequest == request) {
            mRelatedUserRequest = null;
            List<User> users = UserParser.parseUsers(result);
            callLBSNearInfoGetterCompleted(mTopicInfo, users);
        }
    }

    public interface TopicInfoGetterListener {

        void onTopicInfoGetterCompleted(final TopicInfo nearInfo, final List<User> userList);

        void onTopicInfoGetterFailed(int errCode);
    }

}
