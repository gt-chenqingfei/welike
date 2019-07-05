package com.redefine.im.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMSession implements Parcelable, Comparable<IMSession> {
    public static final byte SESSION_TYPE_SINGLE = 1;
    public static final byte SESSION_TYPE_STRANGER = 2;
    public static final String STRANGER_SESSION_SID = "007";
    private String sid;
    private String nickName;
    private String head;
    private String singleUid;
    private byte msgType;
    private byte sessionType;
    private long time;
    private boolean enableChat = true;
    private boolean visableChat = true;
    private String content;
    private int unreadCount;
    private boolean greet;

    public IMSession(String sid) { this.sid = sid; }

    protected IMSession(Parcel in) {
        sid = in.readString();
        nickName = in.readString();
        head = in.readString();
        singleUid = in.readString();
        msgType = in.readByte();
        sessionType = in.readByte();
        time = in.readLong();
        int enableChatInt = in.readInt();
        if (enableChatInt == 0) {
            enableChat = false;
        } else {
            enableChat = true;
        }
        int visableChatInt = in.readInt();
        if (visableChatInt == 0) {
            visableChat = false;
        } else {
            visableChat = true;
        }
        content = in.readString();
        unreadCount = in.readInt();
        int greetInt = in.readInt();
        if (greetInt == 0) {
            greet = false;
        } else {
            greet = true;
        }
    }

    public static final Creator<IMSession> CREATOR = new Creator<IMSession>() {
        @Override
        public IMSession createFromParcel(Parcel in) {
            return new IMSession(in);
        }

        @Override
        public IMSession[] newArray(int size) {
            return new IMSession[size];
        }
    };

    public String getSid() { return sid; }

    public String getNickName() { return nickName; }

    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getHead() { return head; }

    public void setHead(String head) { this.head = head; }

    public String getSingleUid() { return singleUid; }

    public void setSingleUid(String singleUid) { this.singleUid = singleUid; }

    public byte getMsgType() { return msgType; }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getSessionType() { return sessionType; }

    public void setSessionType(byte sessionType) { this.sessionType = sessionType; }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isEnableChat() { return enableChat; }

    public void setEnableChat(boolean enableChat) { this.enableChat = enableChat; }

    public boolean isVisableChat() { return visableChat; }

    public void setVisableChat(boolean visableChat) { this.visableChat = visableChat; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUnreadCount() { return unreadCount; }

    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public boolean isGreet() {
        return greet;
    }

    public void setGreet(boolean greet) {
        this.greet = greet;
    }

    public void copy(IMSession adapterSession) {
        this.sid = adapterSession.sid;
        this.nickName = adapterSession.nickName;
        this.head = adapterSession.head;
        this.singleUid = adapterSession.singleUid;
        this.msgType = adapterSession.msgType;
        this.sessionType = adapterSession.sessionType;
        this.time = adapterSession.time;
        this.enableChat = adapterSession.enableChat;
        this.visableChat = adapterSession.visableChat;
        this.content = adapterSession.content;
        this.unreadCount = adapterSession.unreadCount;
        this.greet = adapterSession.greet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sid);
        dest.writeString(nickName);
        dest.writeString(head);
        dest.writeString(singleUid);
        dest.writeByte(msgType);
        dest.writeByte(sessionType);
        dest.writeLong(time);
        if (enableChat) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        if (visableChat) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(content);
        dest.writeInt(unreadCount);
        if (greet) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public int compareTo(@NonNull IMSession o) {
        return (int) (o.getTime() - this.getTime());
    }
}
