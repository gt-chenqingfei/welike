package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/4
 */
public enum SendStatus {
    FAIL(1), SUCCESS(2), DRAFT(3), CANCEL(4);
    private int value;
    SendStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
