package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;

import java.util.ArrayList;

/**
 * Created by mengnan on 2018/5/12.
 **/
public class UserVoteRequest extends BaseRequest {

    private Context context;

    public UserVoteRequest(Context context) {

        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        this.context = context;
    }

    public void vote(String pid, String pollId, ArrayList<PollItemInfo> choiceIds, boolean isRepost, RequestCallback callback) throws Exception {
        if (CollectionUtil.isEmpty(choiceIds)) {
            callback.onError(this, ErrorCode.ERROR_NETWORK_PARAMS);
            return;
        }
        String uid = AccountManager.getInstance().getAccount().getUid();
        setHost("feed/user/" + uid + "/vote", true);

        JSONObject bodyData = new JSONObject();

        JSONObject pollJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (PollItemInfo info : choiceIds) {
            jsonArray.add(info.id);
        }
        pollJson.put("id", pollId);
        pollJson.put("choices", jsonArray);
        bodyData.put("poll", pollJson);

        if (isRepost) {
            JSONObject commentJSON = new JSONObject();

            commentJSON.put("post", pid);
            String content = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "poll_comment_default_content");
            content = String.format(content, choiceIds.get(0).pollItemText);
            commentJSON.put("content", content);
            if (!SpManager.Setting.getCurrentMobileModelSetting(context)) {
                commentJSON.put("source", CommonHelper.getDeviceModel());
            }


            JSONObject postJSON = new JSONObject();
            postJSON.put("forwardPost", pid);
            postJSON.put("content", content);
            postJSON.put("summary", content);
            if (!SpManager.Setting.getCurrentMobileModelSetting(context)) {
                postJSON.put("source", CommonHelper.getDeviceModel());
            }


            bodyData.put("comment", commentJSON);
            bodyData.put("post", postJSON);
        }

        setBodyData(bodyData.toJSONString());
        req(callback);
    }
}
