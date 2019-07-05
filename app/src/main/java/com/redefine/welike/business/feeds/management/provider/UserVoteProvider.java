package com.redefine.welike.business.feeds.management.provider;


import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.PostsProviderBase;
import com.redefine.welike.business.feeds.management.bean.PollInfo;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.request.UserVoteRequest;

import java.util.ArrayList;

/**
 * Created by mengnan on 2018/5/12.
 **/
public class UserVoteProvider extends PostsProviderBase implements RequestCallback {
    private UserVoteRequest userVoteRequest;
    private UserVoteProvider.UserVoteProviderCallback listener;


    public interface UserVoteProviderCallback {

        void onRefreshVote(Object result, int errCode);

    }

    public void setListener(UserVoteProvider.UserVoteProviderCallback listener) {
        this.listener = listener;
    }

    public void tryVote(String pid, String pollId, ArrayList<PollItemInfo> choiceIds, boolean isRepost) {
        if (userVoteRequest != null) return;

        userVoteRequest = new UserVoteRequest(MyApplication.getAppContext());
        try {
            userVoteRequest.vote(pid, pollId, choiceIds, isRepost, this);
        } catch (Exception e) {
            e.printStackTrace();
            userVoteRequest = null;
            refreshVote(null, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == userVoteRequest) {
            userVoteRequest = null;
            if (listener != null) {
                listener.onRefreshVote(null, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == userVoteRequest) {
            userVoteRequest = null;

            if (listener != null) {
                listener.onRefreshVote(getVoteInfo(result), ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void refreshVote(String result, int errCode) {
        if (null != listener) {
            listener.onRefreshVote(result, errCode);
        }
    }

    private PollInfo getVoteInfo(JSONObject result) {
        PollInfo pollInfo = PostsDataSourceParser.getPollInfo(result);
        return pollInfo;
    }

}
