package com.redefine.welike.business.location.management.bean;

import java.io.Serializable;

/**
 * Created by liubin on 2018/3/26.
 */

public class PoiInfo implements Serializable {
    private static final long serialVersionUID = 7439990069380009726L;
    private Location location;
    private int feedCount;
    private int userCount;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
