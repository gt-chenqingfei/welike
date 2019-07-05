package com.redefine.welike.business.publisher.management.bean;

import com.redefine.welike.business.location.management.bean.Location;

/**
 * Created by liubin on 2018/3/13.
 */

public abstract class DraftPostBase extends DraftBase {
    private static final long serialVersionUID = -1780661019652430713L;
    protected Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
