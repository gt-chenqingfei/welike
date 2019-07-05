package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/4
 */
public enum PostType {
    TEXT(1), IMAGE(2), VIDEO(3), POST_STATUS(5), POLL(6), OTHER(7), ARTICLE(8), COMMENT(10);
    private int value;
    PostType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
