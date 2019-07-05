package com.redefine.im.bean;

import android.support.annotation.NonNull;

import java.lang.Comparable;

/**
 * Created by liubin on 2018/2/2.
 */

public abstract class IMMessageBase implements Comparable<IMMessageBase> {
    public static final byte MESSAGE_STATUS_SENDING = 1;
    public static final byte MESSAGE_STATUS_SENT = 2;
    public static final byte MESSAGE_STATUS_FAILED = 3;
    public static final byte MESSAGE_STATUS_RECEIVED = 4;
    public static final byte MESSAGE_TYPE_UNKNOWN = 0;
    public static final byte MESSAGE_TYPE_TXT = 1;
    public static final byte MESSAGE_TYPE_PIC = 2;
    public static final byte MESSAGE_TYPE_AUDIO = 3;
    public static final byte MESSAGE_TYPE_VIDEO = 4;
    public static final byte MESSAGE_TYPE_TIME = 5;
    public static final byte MESSAGE_TYPE_SYSTEM = 6;

    protected String mid;
    protected String sid;
    protected String sessionNickName;
    protected String sessionHead;
    protected byte sessionType;
    protected boolean sessionEnableChat;
    protected boolean sessionVisableChat;
    protected String senderUid;
    protected String senderNickName = "";
    protected String senderHead;
    protected String remoteUid;
    protected boolean greet;
    protected byte status;
    protected long time;
    protected byte type = MESSAGE_TYPE_UNKNOWN;

    public String form = "";

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSessionNickName() {
        return sessionNickName;
    }

    public void setSessionNickName(String sessionNickName) {
        this.sessionNickName = sessionNickName;
    }

    public String getSessionHead() {
        return sessionHead;
    }

    public void setSessionHead(String sessionHead) {
        this.sessionHead = sessionHead;
    }

    public byte getSessionType() {
        return sessionType;
    }

    public void setSessionType(byte sessionType) {
        this.sessionType = sessionType;
    }

    public boolean isSessionEnableChat() {
        return sessionEnableChat;
    }

    public void setSessionEnableChat(boolean sessionEnableChat) {
        this.sessionEnableChat = sessionEnableChat;
    }

    public boolean isSessionVisableChat() {
        return sessionVisableChat;
    }

    public void setSessionVisableChat(boolean sessionVisableChat) {
        this.sessionVisableChat = sessionVisableChat;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderNickName() {
        return senderNickName == null ? "" : senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public String getSenderHead() {
        return senderHead;
    }

    public void setSenderHead(String senderHead) {
        this.senderHead = senderHead;
    }

    public String getRemoteUid() {
        return remoteUid;
    }

    public void setRemoteUid(String remoteUid) {
        this.remoteUid = remoteUid;
    }

    public boolean isGreet() {
        return greet;
    }

    public void setGreet(boolean greet) {
        this.greet = greet;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull IMMessageBase o) {
        return (int) (this.getTime() - o.getTime());
    }

    public IMMessageBase copy() {
        IMMessageBase message = null;
        if (this instanceof IMAudioMessage) {
            IMAudioMessage audioMessage = new IMAudioMessage();
            IMAudioMessage selfAudioMessage = (IMAudioMessage) this;
            audioMessage.setAudioUrl(selfAudioMessage.getAudioUrl());
            audioMessage.setLocalFileName(selfAudioMessage.getAudioUrl());
            message = audioMessage;
        } else if (this instanceof IMVideoMessage) {
            IMVideoMessage videoMessage = new IMVideoMessage();
            IMVideoMessage selfVideoMessage = (IMVideoMessage) this;
            videoMessage.setVideo(selfVideoMessage.getVideo());
            videoMessage.setThumb(selfVideoMessage.getThumb());
            videoMessage.setLocalFileName(selfVideoMessage.getLocalFileName());
            message = videoMessage;
        } else if (this instanceof IMTextMessage) {
            IMTextMessage textMessage = new IMTextMessage();
            IMTextMessage selfTextMessage = (IMTextMessage) this;
            textMessage.setText(selfTextMessage.getText());
            message = textMessage;
        } else if (this instanceof IMPicMessage) {
            IMPicMessage picMessage = new IMPicMessage();
            IMPicMessage selfPicMessage = (IMPicMessage) this;
            picMessage.setPicUrl(selfPicMessage.getPicUrl());
            picMessage.setPicLargeUrl(selfPicMessage.getPicLargeUrl());
            picMessage.setLocalFileName(selfPicMessage.getLocalFileName());
            message = picMessage;
        }
        if (message != null) {
            message.setMid(this.getMid());
            message.setSid(this.getSid());
            message.setSessionNickName(this.getSessionNickName());
            message.setSessionHead(this.getSessionHead());
            message.setSessionType(this.getSessionType());
            message.setSessionEnableChat(this.isSessionEnableChat());
            message.setSessionVisableChat(this.isSessionVisableChat());
            message.setSenderUid(this.getSenderUid());
            message.setSenderNickName(this.getSenderNickName());
            message.setSenderHead(this.getSenderHead());
            message.setStatus(this.getStatus());
            message.setTime(this.getTime());
        }
        return message;
    }

}
