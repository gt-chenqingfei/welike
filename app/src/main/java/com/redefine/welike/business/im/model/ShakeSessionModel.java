package com.redefine.welike.business.im.model;

import com.redefine.im.Constants;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by nianguowang on 2018/6/29
 */
public class ShakeSessionModel extends SessionModel {

    private String shakeUrl;
    public ShakeSessionModel() {
        super(Constants.SESSION_SHAKE);
    }

    public String getShakeUrl() {
        return shakeUrl;
    }

    public void setShakeUrl(String shakeUrl) {
        this.shakeUrl = shakeUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShakeSessionModel that = (ShakeSessionModel) o;

        return new EqualsBuilder()
                .append(shakeUrl, that.shakeUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(shakeUrl)
                .toHashCode();
    }
}
