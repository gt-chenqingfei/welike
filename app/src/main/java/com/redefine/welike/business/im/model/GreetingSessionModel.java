package com.redefine.welike.business.im.model;

import com.redefine.im.Constants;
import com.redefine.im.room.MessageSession;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by nianguowang on 2018/6/29
 */
public class GreetingSessionModel extends SessionModel {

    private MessageSession messageSession;
    public GreetingSessionModel() {
        super(Constants.SESSION_STRANGE);
    }

    public MessageSession getMessageSession() {
        return messageSession;
    }

    public void setMessageSession(MessageSession messageSession) {
        this.messageSession = messageSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GreetingSessionModel that = (GreetingSessionModel) o;

        return new EqualsBuilder()
                .append(messageSession, that.messageSession)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(messageSession)
                .toHashCode();
    }
}
