package com.redefine.welike.business.videoplayer.management.command;

/**
 * Created by nianguowang on 2018/8/9
 */
public interface CommandInvoker {

    interface VideoCommandInvoker {
        void invoke(VideoCommand videoCommand);
    }

    interface PostCommandInvoker {
        void invoker(PostCommand postCommand);
    }
}
