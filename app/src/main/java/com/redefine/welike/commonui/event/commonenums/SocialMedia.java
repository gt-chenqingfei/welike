package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/5
 */
public enum SocialMedia {
    FACEBOOK(1), INSTAGRAM(2), YOUTUBE(3);
    private int value;
    SocialMedia(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
