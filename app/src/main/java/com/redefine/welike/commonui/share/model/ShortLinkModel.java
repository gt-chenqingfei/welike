package com.redefine.welike.commonui.share.model;

import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;

/**
 * Created by nianguowang on 2018/6/20
 */
public class ShortLinkModel {

    private int from;
    private String shareId;
    private String userId;

    public ShortLinkModel(int from, String shareId) {

        this.from = from;
        this.shareId = shareId;
        Account account = AccountManager.getInstance().getAccount();
        if(account != null) {
            this.userId = account.getUid();
        } else {
            this.userId = "";
        }
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
