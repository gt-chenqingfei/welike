package com.redefine.welike.business.location.management.bean;

import com.redefine.welike.business.user.management.bean.User;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSUser {
    private long passTime;
    private User user;

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
