package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/4
 */
public enum ShareChannel {
    WHATS_APP(1), FACEBOOK(2), INSTAGRAM(3), COPY_LINK(4), OTHER(5), QR_CODE(6), SHARE_APK_BUTTON_LONG(7), SHARE_APK_BUTTON_CIRCLE(8);
    private int value;
    ShareChannel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
