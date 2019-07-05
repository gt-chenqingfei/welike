package com.redefine.welike.base.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;

import java.util.ArrayList;
import java.util.List;

import static com.redefine.welike.base.request.UpdateLinkRequest.UPDATE_ACCOUNT_LINK_KEY;

/**
 * Created by nianguowang on 2018/8/17
 */
public class AddLinkRequest extends BaseRequest {
    public AddLinkRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("user/link/add", true);
    }

    public void req(int linkType, String link, RequestCallback callback) throws Exception {
        Account account = AccountManager.getInstance().getAccount();
        List<UserBase.Link> links = account.getLinks();
        if (links == null) {
            links = new ArrayList<>();
        }
        UserBase.Link link1 = new UserBase.Link();
        link1.setLink(link);
        link1.setLinkType(linkType);
        links.add(link1);
        putUserInfo(UPDATE_ACCOUNT_LINK_KEY, links);

        setParam("linkType", linkType);
        setParam("link", link);
        req(callback);
    }
}
