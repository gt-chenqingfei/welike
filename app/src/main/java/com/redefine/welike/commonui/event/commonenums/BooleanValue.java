package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/4
 */
public enum BooleanValue {
    YES(1), NO(0);
    private int value;
    BooleanValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
