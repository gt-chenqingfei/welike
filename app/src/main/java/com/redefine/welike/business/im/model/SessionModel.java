package com.redefine.welike.business.im.model;

/**
 * Created by nianguowang on 2018/6/29
 */
public class SessionModel {

    private int sessionType;

    public  SessionModel(int sessionType) {
        this.sessionType = sessionType;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }
}
