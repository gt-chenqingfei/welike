package com.redefine.welike.base.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;

import java.util.List;

/**
 * Created by nianguowang on 2018/8/17
 */
public class UpdateLinkRequest extends BaseRequest {

    public static final String UPDATE_ACCOUNT_LINK_KEY = "links";

    public UpdateLinkRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("user/link/update", true);
    }

    public void req(long linkId, int linkType, String link, RequestCallback callback) throws Exception {
        Account account = AccountManager.getInstance().getAccount();
        List<UserBase.Link> links = account.getLinks();
        for (UserBase.Link linkBean : links) {
            if (linkBean.getLinkId() == linkId) {
                linkBean.setLink(link);
                break;
            }
        }
        putUserInfo(UPDATE_ACCOUNT_LINK_KEY, links);

        setParam("linkId", linkId);
        setParam("linkType", linkType);
        setParam("link", link);
        req(callback);
    }
}
