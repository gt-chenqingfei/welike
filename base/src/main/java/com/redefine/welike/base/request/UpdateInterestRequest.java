package com.redefine.welike.base.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.bean.UserBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/7/31
 */
public class UpdateInterestRequest extends BaseRequest {

    public static final String UPDATE_ACCOUNT_INTERESTS_KEY = "interests";

    public UpdateInterestRequest(Context context, List<UserBase.Intrest> list) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("user/interest/update", true);
        List<Long> interestIds = new ArrayList<>(list.size());
        for (UserBase.Intrest intrest : list) {
            try {
                interestIds.add(Long.parseLong(intrest.getIid()));
            } catch (Exception e) {
                //do noting
            }
        }
        putUserInfo(UPDATE_ACCOUNT_INTERESTS_KEY, list);
        setBodyData(interestIds.toString());
    }
}
