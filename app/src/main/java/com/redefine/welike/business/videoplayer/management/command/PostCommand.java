package com.redefine.welike.business.videoplayer.management.command;

import com.redefine.richtext.RichItem;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.videoplayer.ui.view.PostController;

/**
 * Created by nianguowang on 2018/8/9
 */
public class PostCommand {
    public int commandId;
    public VideoPost videoPost;
    public RichItem richItem;
    public PostController postController;

    public PostCommand(int commandId, VideoPost videoPost) {
        this.commandId = commandId;
        this.videoPost = videoPost;
    }

    public PostCommand(int commandId, RichItem richItem, VideoPost videoPost) {
        this.commandId = commandId;
        this.richItem = richItem;
        this.videoPost = videoPost;
    }

    public PostCommand(int commandId, VideoPost videoPost, PostController postController) {
        this.commandId = commandId;
        this.videoPost = videoPost;
        this.postController = postController;
    }
}
