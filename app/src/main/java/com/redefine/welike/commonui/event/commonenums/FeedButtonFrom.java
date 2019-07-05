package com.redefine.welike.commonui.event.commonenums;

/**
 * Created by nianguowang on 2018/11/5
 */
public enum FeedButtonFrom {
    POST_CARD(1), POST_DETAIL(2), VIDEO_PLAYER(3);
    private int value;
    FeedButtonFrom(int value) {
            this.value = value;
        }

    public int getValue() {
        return value;
    }
}
