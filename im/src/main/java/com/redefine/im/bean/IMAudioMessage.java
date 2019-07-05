package com.redefine.im.bean;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMAudioMessage extends IMMessageBase {
    private String audioUrl;
    private String localFileName;

    public IMAudioMessage() { type = MESSAGE_TYPE_AUDIO; }

    public String getAudioUrl() { return audioUrl; }

    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getLocalFileName() { return localFileName; }

    public void setLocalFileName(String localFileName) { this.localFileName = localFileName; }
}
