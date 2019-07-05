package com.redefine.welike.business.im.model;

import com.redefine.im.Constants;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PushSessionModel extends SessionModel{

    private int unreadCount;
    public PushSessionModel() {
        super(Constants.SESSION_PUSH);
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PushSessionModel that = (PushSessionModel) o;

        return new EqualsBuilder()
                .append(unreadCount, that.unreadCount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(unreadCount)
                .toHashCode();
    }
}
