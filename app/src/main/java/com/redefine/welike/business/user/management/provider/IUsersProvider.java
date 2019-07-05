package com.redefine.welike.business.user.management.provider;

/**
 * Created by liubin on 2018/1/20.
 */

public interface IUsersProvider {

    void tryRefreshUsers(String uid);

    void tryHisUsers(String uid);

    void setListener(UsersProviderCallback callback);

}
