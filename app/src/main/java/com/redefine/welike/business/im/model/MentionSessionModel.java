package com.redefine.welike.business.im.model;

import com.redefine.im.Constants;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by nianguowang on 2018/6/29
 */
public class MentionSessionModel extends SessionModel {

    private int unreadCount;

    public MentionSessionModel() {
        super(Constants.SESSION_MENTIONS);
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

        MentionSessionModel that = (MentionSessionModel) o;

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
