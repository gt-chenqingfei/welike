package com.redefine.welike.base.track;

public class LogEventConstant {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    public static final int LOG_EVENT_VIDEO_PLAYER = generateID();
    public static final int LOG_EVENT_VIDEO_DOWNLOAD = generateID();
    public static final int LOG_EVENT_VIDEO_DOWNLOAD_SUCCESS = generateID();
    public static final int LOG_EVENT_VIDEO_AUTO_PLAY = generateID();
    public static final int LOG_EVENT_SHARE_QRCODE_GENERATE = generateID();
    public static final int LOG_EVENT_SHARE_QRCODE_DESTROY = generateID();
    public static final int LOG_EVENT_SHARE_RESULT_SUCCESS = generateID();
    public static final int LOG_EVENT_SHARE_RESULT_FAIL = generateID();
    public static final int LOG_EVENT_SHARE_RESULT_UNKUNOW = generateID();

}
