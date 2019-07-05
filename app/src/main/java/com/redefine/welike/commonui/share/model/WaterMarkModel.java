package com.redefine.welike.commonui.share.model;

/**
 * Created by nianguowang on 2018/6/20
 */
public class WaterMarkModel {

    private String userHeader;
    private String userNick;
    private String topicName;

    public WaterMarkModel(String userHeader, String userNick, String topicName) {
        this.userHeader = userHeader;
        this.userNick = userNick;
        this.topicName = topicName;
    }

    public String getUserHeader() {
        return userHeader;
    }

    public void setUserHeader(String userHeader) {
        this.userHeader = userHeader;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
