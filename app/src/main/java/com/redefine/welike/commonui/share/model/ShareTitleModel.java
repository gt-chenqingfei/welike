package com.redefine.welike.commonui.share.model;

/**
 * Created by nianguowang on 2018/6/20
 */
public class ShareTitleModel {

    private int from;
    private String nickName;

    public ShareTitleModel(int from, String nickName) {
        this.from = from;
        this.nickName = nickName;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
