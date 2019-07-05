package com.redefine.welike.base.request;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;

/**
 * Created by liubin on 2018/1/24.
 */

public class UpdateAccountRequest extends BaseRequest {
    public static final String UPDATE_ACCOUNT_NICK_NAME_KEY = "nickName";
    public static final String UPDATE_ACCOUNT_SEX_KEY = "sex";
    public static final String UPDATE_ACCOUNT_HEAD_KEY = "head";
    public static final String UPDATE_ACCOUNT_INTRO_KEY = "intro";
    public static final String UPDATE_ACCOUNT_FINISH_LEVEL_KEY = "completeLevel";
    public static final String UPDATE_ACCOUNT_INTERESTS_KEY = "interests";

    public UpdateAccountRequest(Account account, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        if (account != null) {
            setHost("user/update/", true);

            JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("id", account.getUid());
            bodyJSON.put("nickName", account.getNickName());
            putUserInfo(UPDATE_ACCOUNT_NICK_NAME_KEY, account.getNickName());
            bodyJSON.put("sex", Byte.toString(account.getSex()));
            putUserInfo(UPDATE_ACCOUNT_SEX_KEY, account.getSex());
            if (!TextUtils.isEmpty(account.getHeadUrl())) {
                bodyJSON.put("avatarUrl", account.getHeadUrl());
                putUserInfo(UPDATE_ACCOUNT_HEAD_KEY, account.getHeadUrl());
            }
            if (account.getIntroduction() != null) {
                bodyJSON.put("introduction", account.getIntroduction());
                putUserInfo(UPDATE_ACCOUNT_INTRO_KEY, account.getIntroduction());
            }
            bodyJSON.put("finishLevel", account.getCompleteLevel());
            putUserInfo(UPDATE_ACCOUNT_FINISH_LEVEL_KEY, account.getCompleteLevel());

            if (account.getIntrests() != null && account.getIntrests().size() > 0) {
                JSONArray intrestsArr = new JSONArray();
                for (UserBase.Intrest i : account.getIntrests()) {
                    JSONObject intrestJSON = new JSONObject();
                    intrestJSON.put("id", i.getIid());
                    intrestJSON.put("name", i.getLabel());
                    intrestJSON.put("icon", i.getIcon());
                    intrestsArr.add(intrestJSON);
                }
                bodyJSON.put("interests", intrestsArr);
                putUserInfo(UPDATE_ACCOUNT_INTERESTS_KEY, account.getIntrests());
            } else {
                JSONArray empty = new JSONArray();
                bodyJSON.put("interests", empty);
            }

            setBodyData(bodyJSON.toJSONString());
        }
    }

    public UpdateAccountRequest(String settings, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("user/update/", true);

            JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("id", account.getUid());
            bodyJSON.put("settings", settings);
            setBodyData(bodyJSON.toJSONString());
        }
    }

}
