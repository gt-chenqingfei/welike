package com.redefine.multimedia.player.mediacontroller;

public class MediaControllerCommand {

    public int cid;
    public int arg1;
    public int arg2;

    public MediaControllerCommand(int cid) {
        this.cid = cid;
    }

    public MediaControllerCommand(int cid, int arg1, int arg2) {
        this.cid = cid;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
}
