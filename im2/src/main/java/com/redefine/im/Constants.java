package com.redefine.im;

public class Constants {
    public static final int MESSAGE_STATUS_SENDING = 1;
    public static final int MESSAGE_STATUS_SENT = 2;
    public static final int MESSAGE_STATUS_FAILED = 3;
    public static final int MESSAGE_STATUS_RECEIVED = 4;

    public static final int MESSAGE_TYPE_UNKNOWN = 0;
    public static final int MESSAGE_TYPE_TXT = 1;
    public static final int MESSAGE_TYPE_PIC = 2;
    public static final int MESSAGE_TYPE_AUDIO = 3;
    public static final int MESSAGE_TYPE_VIDEO = 4;
    public static final int MESSAGE_TYPE_TIME = 5;
    public static final int MESSAGE_TYPE_SYSTEM = 6;
    public static final int MESSAGE_TYPE_CARD = 7;

    public static final byte SESSION_STRANGE = 1;
    public static final byte SESSION_SINGLE = 2;
    public static final byte SESSION_MENTIONS = 3;
    public static final byte SESSION_COMMENTS = 4;
    public static final byte SESSION_LIKES = 5;
    public static final byte SESSION_SHAKE = 6;
    public static final byte SESSION_PUSH = 7;


    public static final byte MESSAGE_GREET_STRANGE = 2;
    public static final byte MESSAGE_GREET_INVALID = 0;
    public static final String STRANGER_SESSION_SID = "007";
}
