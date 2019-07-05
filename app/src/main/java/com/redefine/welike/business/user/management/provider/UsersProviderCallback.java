package com.redefine.welike.business.user.management.provider;

import com.redefine.welike.business.user.management.bean.User;

import java.util.List;

/**
 * Created by liubin on 2018/1/20.
 */

public interface UsersProviderCallback {

    void onRefreshUsers(final List<User> users, final String uid, final int newCount, final int errCode);

    void onReceiveHisUsers(final List<User> users, final String uid, final boolean last, final int errCode);

}
