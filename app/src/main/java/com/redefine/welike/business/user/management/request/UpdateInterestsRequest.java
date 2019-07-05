package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import java.util.List;

/**
 * Created by honglin on 2018/5/15.
 */

public class UpdateInterestsRequest extends BaseRequest {


    public UpdateInterestsRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("/user/interest/update", true);
    }

    public void update(List<UserBase.Intrest> interests, RequestCallback callback) throws Exception {

        JSONArray interestArray = new JSONArray();
        if (!CollectionUtil.isEmpty(interests)) {
            for (UserBase.Intrest interest : interests) {
                interestArray.add(interest.getIid());
            }
        }
        setUrlExtParam("interestIds", interestArray.toJSONString());

        req(callback);

    }

}
