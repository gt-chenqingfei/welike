package com.redefine.welike.business.search.ui.adapter;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.welike.business.user.management.bean.User;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/5.
 */

public class SearchUserHeaderBean extends BaseHeaderBean {

    private final List<User> mUsers;

    public SearchUserHeaderBean(List<User> users) {
        mUsers = users;
    }

    public List<User> getUsers() {
        return mUsers;
    }
}
