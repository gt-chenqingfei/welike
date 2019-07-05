package com.redefine.multimedia.player;

/**
 * Created by nianguowang on 2018/11/30
 */
public enum VideoPlayAction {
    PLAY_PAUSE(1), DOWNLOAD(2), FULL_SCREEN(3), MUTE(4), AVATAR(5), TEXT(6), ROTATE(7), CLOSE(8), SEEK(9);

    private int value;
    VideoPlayAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
