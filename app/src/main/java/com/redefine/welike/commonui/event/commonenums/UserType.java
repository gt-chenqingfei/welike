package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/5
 */
public enum UserType {
    OWNER(1), VISIT(2);
    private int value;
    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
