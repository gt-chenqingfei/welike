package com.redefine.welike.business.videoplayer.management.command;

/**
 * Created by nianguowang on 2018/8/9
 */
public class VideoCommand {

    public int commandId;
    public int arg1;
    public int arg2;

    public VideoCommand(int commandId) {
        this.commandId = commandId;
    }

    public VideoCommand(int commandId, int arg1, int arg2) {
        this.commandId = commandId;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
}
