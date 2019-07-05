package com.redefine.multimedia.player.mediacontroller;

public class MediaControllerCommandId {
    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    public static final int BACK_BTN_CLICK = generateID();
    public static final int ROTATE_BTN_CLICK = generateID();
    public static final int UPDATE_PROGRESS = generateID();
    public static final int DOWNLOAD_BTN_CLICK = generateID();
    public static final int SHARE_BTN_CLICK = generateID();
    public static final int SHOW_CONTROLLER_BAR = generateID();

}
