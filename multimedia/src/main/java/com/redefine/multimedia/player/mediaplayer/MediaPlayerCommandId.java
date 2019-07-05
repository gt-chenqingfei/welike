package com.redefine.multimedia.player.mediaplayer;

public class MediaPlayerCommandId {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    public static final int ON_PREPARED = generateID();
    public static final int ON_BUFFERING_START = generateID();
    public static final int ON_BUFFERING_END = generateID();
    public static final int ON_FIRST_FRAME_RENDERING = generateID();
    public static final int ON_PLAY_END = generateID();
    public static final int ON_SEEK = generateID();
    public static final int ON_CANCEL_PROCESS = generateID();

}
