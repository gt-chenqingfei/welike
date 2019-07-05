package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/5
 */
public enum ResultEnum {
    SUCCESS(1), FAIL(2), UNKNOW(3);
    private int value;
    ResultEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
