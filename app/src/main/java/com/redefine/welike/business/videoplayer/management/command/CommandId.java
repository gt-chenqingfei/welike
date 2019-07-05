package com.redefine.welike.business.videoplayer.management.command;

/**
 * Created by nianguowang on 2018/8/9
 */
public class CommandId {
    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    ////////////////////Video CommandId///////////////////
    public static final int BACK_BTN_CLICK = generateID();
    public static final int ROTATE_BTN_CLICK = generateID();
    public static final int PAUSE_VIDEO = generateID();
    public static final int PLAY_VIDEO = generateID();
    public static final int UPDATE_PROGRESS = generateID();
    public static final int SEEK_PROGRESS = generateID();

    ////////////////////Post CommandId////////////////
    public static final int LIKE_BTN_CLICK = generateID();
    public static final int COMMENT_BTN_CLICK = generateID();
    public static final int SHARE_BTN_CLICK = generateID();
    public static final int DOWNLOAD_BTN_CLICK = generateID();
    public static final int FOLLOW_BTN_CLICK = generateID();
    public static final int AVATAR_BTN_CLICK = generateID();
    public static final int NICK_NAME_CLICK = generateID();
    public static final int POST_CONTENT_CLICK = generateID();
    public static final int RICH_ITEM_CLICK = generateID();
}
