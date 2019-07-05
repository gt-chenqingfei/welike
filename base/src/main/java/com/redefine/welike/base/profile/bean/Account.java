package com.redefine.welike.base.profile.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/1/6.
 */

public class Account extends UserBase {
    public static final int PROFILE_COMPLETE_LEVEL_BASE = 1;
    public static final int PROFILE_COMPLETE_LEVEL_BASE_USERINFO = 2;
    public static final int PROFILE_COMPLETE_LEVEL_INTEREST = 3;
    public static final int PROFILE_COMPLETE_LEVEL_MAIN_DONE = 4;
    private static final long serialVersionUID = 7440057966633330355L;

    public static final int ACCOUNT_ACTIVE = 0;
    public static final int ACCOUNT_BLOCKED = 1;
    public static final int ACCOUNT_DEACTIVATE = 2;
    public static final int ACCOUNT_HALF = 3;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expired;
    private int completeLevel;
    private boolean login;
    private int status = 0;

    //new  is first LOGIN
    private boolean isFirst ;

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public int getCompleteLevel() {
        return completeLevel;
    }

    public void setCompleteLevel(int completeLevel) {
        this.completeLevel = completeLevel;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public Account copy() {
        Account account = new Account();
        account.setUid(uid);
        account.setNickName(nickName);
        account.setHeadUrl(headUrl);
        account.setSex(sex);
        account.setIntroduction(introduction);
        account.setPostsCount(postsCount);
        account.setFollowUsersCount(followUsersCount);
        account.setFollowedUsersCount(followedUsersCount);
        account.setLikedMyPostsCount(likedMyPostsCount);
        account.setMyLikedPostsCount(myLikedPostsCount);
        account.setAllowUpdateNickName(allowUpdateNickName);
        account.setAllowUpdateSex(allowUpdateSex);
        account.setNextUpdateNickNameDate(nextUpdateNickNameDate);
        account.setSexUpdateCount(sexUpdateCount);
        account.setAccessToken(accessToken);
        account.setRefreshToken(refreshToken);
        account.setTokenType(tokenType);
        account.setExpired(expired);
        account.setCompleteLevel(completeLevel);
        account.setLogin(login);
        account.setStatus(status);
        account.setVip(vip);
        if (intrests != null && intrests.size() > 0) {
            List<Intrest> newIntrests = new ArrayList<>();
            for (Intrest i : intrests) {
                Intrest ii = new Intrest();
                ii.setIid(i.getIid());
                ii.setLabel(i.getLabel());
                newIntrests.add(ii);
            }
            account.setIntrests(newIntrests);
        }
        return account;
    }

}
