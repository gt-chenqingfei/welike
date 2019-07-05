package com.redefine.welike.common;

/**
 * Created by nianguowang on 2018/9/6
 */
public enum  VideoListVolumeManager {
    INSTANCE;

    private boolean mute = true;
    private boolean voiceUIOn = true;

    public boolean isVoiceUIOn() {
        return voiceUIOn;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean toggleVoice() {
        this.mute = !mute;
        return mute;
    }

}
